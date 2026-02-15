package lenicorp.admin.archive.controller.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import lenicorp.admin.archive.controller.repositories.DocumentRepository;
import lenicorp.admin.archive.model.constants.FileConstants;
import lenicorp.admin.archive.model.dtos.DocMapper;
import lenicorp.admin.archive.model.dtos.request.UpdateDocReq;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.archive.model.dtos.response.Base64FileDto;
import lenicorp.admin.archive.model.dtos.response.ReadDocDTO;
import lenicorp.admin.archive.model.entities.Document;
import lenicorp.admin.archive.model.utils.FileUtils;
import lenicorp.admin.exceptions.AppException;
import lenicorp.admin.types.controller.repositories.TypeRepo;
import lenicorp.admin.types.model.dtos.TypeDTO;
import lenicorp.admin.types.model.entities.Type;
import lenicorp.admin.utilities.Base64ToFileConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lenicorp.admin.utilities.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.*;

@RequiredArgsConstructor @Service
public class DocumentService implements IDocumentService
{
	private final TypeRepo typeRepo;
	private final DocMapper docMapper;
	private final DocumentRepository docRepo;

	@Override
	public byte[] downloadFile(String filePAth)
	{
		File file = new File(filePAth);
		Path path = Paths.get(file.toURI());
		try
		{
			return Files.readAllBytes(path);
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new AppException("Erreur de téléchargement");
		}
	}

	@Override
	public MultipartFile downloadMultipartFile(Long docI)
	{
		Document doc = docRepo.findById(docI).orElseThrow(() -> new AppException("Document introuvable"));
		byte[] bytes = downloadFile(doc.getDocPath());
		MultipartFile multipartFile = new MockMultipartFile(
				"file",                     // nom du champ
				doc.getDocName(),            // nom du fichier
				doc.getDocMimeType(), // content-type (à adapter si besoin)
				bytes                        // contenu
		);
		return multipartFile;
	}

	@Override
	public ResponseEntity<?> downloadFile(Long docId) {
		Document doc = docRepo.findById(docId).orElseThrow(() -> new AppException("Document introuvable"));
		if(doc == null) return ResponseEntity.notFound().build();
		String filename = doc.getDocType().name;
		String docPath = doc.getDocPath();
		docPath = Optional.ofNullable(docPath).orElse("");
		//docPath = docPath.replace("\\", "\\\\");
		try {
			// Le chemin où se trouve le fichier
			Path filePath = Paths.get(docPath);
			File file = filePath.toFile();

			if (!file.exists()) {
				return ResponseEntity.notFound().build();
			}

			// Définir les headers pour le téléchargement
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_OCTET_STREAM) // Type générique
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
					.body(file);
		} catch (Exception ex) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@Override
	public boolean deleteFile(String filePath)
	{
		File file = new File(filePath);
		return file == null ? false : file.delete();
	}

	@Override
	public Page<ReadDocDTO> searchObjectDocs(Long objectI, String tableName, String key, Pageable pageable)
	{
		key = StringUtils.stripAccentsToUpperCase(key);
        Page<ReadDocDTO> docs = docRepo.searchObjectDocs(objectI, tableName, key, pageable);
        docs.forEach(doc->doc.setFile(downloadFile(doc.getDocPath())));
		return docs;
	}

	@Override
	public List<ReadDocDTO> searchObjectDocs(Long objectI, String tableName, String key)
	{
		key = StringUtils.stripAccentsToUpperCase(key);
		return docRepo.searchObjectDocs(objectI, tableName, key);
	}

	@Override
    public List<byte[]> getFileByObjectIdAndTableNameAndTypeCode(Long objectId, String tableName, String typeCode)
    {
        List<ReadDocDTO> logoDocs = docRepo.findByObjectIdAndTableNameAndTypeCode(objectId, tableName, typeCode);
        if(logoDocs == null || logoDocs.isEmpty()) return Collections.emptyList();
        List<byte[]> logos = logoDocs.stream().map(ReadDocDTO::getDocPath).map(this::downloadFile).toList();
        return logos;
    }

    @Override
    public ReadDocDTO getLatestByTypeAndObject(Long objectId, String tableName, String typeCode) {
        List<ReadDocDTO> docs = docRepo.findLatestByObjectAndTableAndType(objectId, tableName, typeCode, PageRequest.of(0, 1));
        if (docs == null || docs.isEmpty()) return null;
        ReadDocDTO dto = docs.get(0);
        try {
            byte[] bytes = downloadFile(dto.getDocPath());
            dto.setFile(bytes);
        } catch (Exception ex) {
            throw new AppException("Erreur lors du chargement du fichier: " + ex.getMessage());
        }
        return dto;
    }

    @Override
    public boolean existsByTypeAndObject(Long objectId, String tableName, String typeCode) {
        return docRepo.existsByObjectAndTableAndType(objectId, tableName, typeCode);
    }

	@Override
	public void renameFile(String oldPath, String newPath)
	{
		if(new File(oldPath).exists())
		{
			try {
				Files.move(Paths.get(oldPath), Paths.get(newPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void uploadFile(MultipartFile file, String destinationPath) throws RuntimeException
	{
		try {
			Path dest = Paths.get(destinationPath);
			Path parent = dest.getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}
			try (InputStream is = file.getInputStream()) {
				Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new AppException("Erreur lors de l'enregistrement du fichier: " + e.getMessage());
		}
	}

	@Override
	public void uploadFile(InputStream file, String destinationPath) throws RuntimeException
	{
		try {
			Path dest = Paths.get(destinationPath);
			Path parent = dest.getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}
			Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new AppException("Erreur lors de l'enregistrement du fichier: " + e.getMessage());
		} finally {
			try { file.close(); } catch (IOException ignored) {}
		}
	}

	@Transactional @Override
	public Document uploadDocument(UploadDocReq dto) throws IOException
    {
		if(dto.getDocTypeCode() == null ) throw new AppException("Le type de document ne peut être null");
		MultipartFile file = dto.getFile();
        InputStream fileInputStream = dto.getFile().getInputStream();
		if(file == null) throw new AppException("Aucun fichier sélectionné");
		Type docType = typeRepo.findById(dto.getDocTypeCode().toUpperCase(Locale.ROOT)).orElseThrow(()->new AppException("Type de document inconnu"));
		if(docType == null || !"DOC".equals(docType.typeGroup.groupCode))  throw new AppException("Ce type de document n'est pris en charge par le système");
		Document doc = docMapper.mapToDocument(dto);
		//FileUtils.InputStreamDetails mimeTypeResult = FileUtils.getInputStreamDetails(fileInputStream);
		//file = mimeTypeResult.getInputStream();
		String mimeType = file.getContentType();
		String extension = FileUtils.getExtensionFromMimeType(mimeType);
		String path = generatePath(extension, dto.getDocTypeCode(), docType.name);
		doc.setDocPath(path);
		doc.setDocExtension(extension);
		doc.setDocMimeType(mimeType);
		doc.setDocName(docType.name);
		uploadFile(file, doc.getDocPath());
		doc = docRepo.save(doc);
		return doc;
	}

	private String getExtensionFromFileName(String fileName) {
		if (fileName == null) return "";
		int lastDotIndex = fileName.lastIndexOf('.');
		return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
	}

	@Transactional @Override
	public boolean deleteDocument(Long docId) {
		Document doc = docRepo.findById(docId).orElseThrow(() -> new AppException("Document introuvable"));
		if (doc == null) throw new AppException("Document inexistant");
		docRepo.delete(doc);
		//logService.logg(ArchiveActions.DELETE_DOCUMENT, doc, new Document(), ArchiveTable.DOCUMENT);
		//this.deleteFile(doc.getDocPath());
		return true;
	}

	@Transactional @Override
	public Document updateDocument(UpdateDocReq dto) throws IOException
    {
		Document doc = docRepo.findById(dto.getDocId()).orElseThrow(() -> new AppException("Document introuvable"));
		if (doc == null) throw new AppException("Document inexistant");

        String oldDocPath = doc.getDocPath();
        doc.setDocNum(Optional.ofNullable(dto.getDocNum()).orElse(doc.getDocNum()));
        doc.setDocDescription(Optional.ofNullable(dto.getDocDescription()).orElse(doc.getDocDescription()));
        doc.setDocName(Optional.ofNullable(dto.getDocName()).orElse(doc.getDocName()));
        Type newType = dto.getDocTypeCode() == null ? null : typeRepo.findById(dto.getDocTypeCode()).orElseThrow(()->new AppException("Type de document inconnu"));

		if(dto.getDocTypeCode() != null && !doc.getDocType().code.equals(dto.getDocTypeCode()))
		{
			doc.setDocType(newType);
		}
        MultipartFile file = dto.getFile();
        if(file == null) return doc;
        InputStream fileInputStreams = file.getInputStream();
        String mimeType = file.getContentType();
        String extension = FileUtils.getExtensionFromMimeType(mimeType);
        doc.setDocExtension(extension);
        doc.setDocMimeType(mimeType);
        String newPath = generatePath(extension, dto.getDocTypeCode(), doc.getDocDescription());
        doc.setDocPath(newPath);

        FileUtils.ComparisonResult comparisonResult = FileUtils.areFilesIdenticalByContent(file, oldDocPath);
        if (!comparisonResult.isIdentical())
        {
            newPath = generatePath(extension, dto.getDocTypeCode(), newType.name);
            doc.setDocPath(newPath);
            uploadFile(comparisonResult.getInputStream(), newPath);
        }
		return doc;
	}


	@Override
	public void displayPdf(HttpServletResponse response, byte[] fileBytes, String displayName)  throws Exception
	{
		// Configurez l'en-tête de la réponse HTTP
		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "inline; filename=" + displayName +".pdf");
		response.setContentLength(fileBytes.length);

		// Écrivez le rapport Jasper dans le flux de sortie de la réponse HTTP
		OutputStream outStream = response.getOutputStream();
		outStream.write(fileBytes);
		outStream.flush();
		outStream.close();
	}

	@Override
	public Page<ReadDocDTO> getAllDocsForObject(Long userId, Long assoId, Long sectionId, String key, PageRequest pageRequest)
	{
		key = key == null || key.trim().equals("") ? "" : StringUtils.stripAccentsToUpperCase(key);
		return docRepo.getAllDocsForObject(userId, assoId, sectionId, key, pageRequest);
	}

	@Override
	public Part downloadMultipartFile(String filePath) {
		// This is a placeholder implementation
		// In a real implementation, you would convert the file to a Part
		throw new AppException("downloadMultipartFile not implemented");
	}

	@Override
	public String generatePath(String extension, String typeCode, String objectName) throws IOException
    {
		String uuid = UUID.randomUUID().toString();
		extension = extension == null  || extension.trim().equals("")? "" : "." + extension.toLowerCase();
		String path = FileConstants.UPLOAD_DIR + File.separator + typeCode + "_" + uuid  + extension;
		return path;
	}

	// Compare two Part files
	public boolean areFilesIdentical(Part file1, Part file2) throws Exception
	{
		// Vérifiez les métadonnées
		if (!file1.getSubmittedFileName().equals(file2.getSubmittedFileName())) {
			return false;
		}

		if (!file1.getContentType().equals(file2.getContentType())) {
			return false;
		}

		// Vérifiez les tailles
		if (file1.getSize() != file2.getSize()) {
			return false;
		}

		// Vérifiez le contenu ou le hash
		String hash1 = calculateFileHash(file1);
		String hash2 = calculateFileHash(file2);
		return hash1.equals(hash2);
	}

	// Compare a Part file with a file on disk
	public boolean areFilesIdenticalByHash(Part file, String localFilePath) throws Exception {
		String fileHash = calculateFileHash(file);
		String localFileHash = calculateFileHash(Files.readAllBytes(new File(localFilePath).toPath()));

		return fileHash.equals(localFileHash);
	}

	// Calculate hash for a Part file
	private String calculateFileHash(Part file) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		try (InputStream is = file.getInputStream()) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				digest.update(buffer, 0, bytesRead);
			}
		}
		return bytesToHex(digest.digest());
	}

	// Calculate hash for a byte array
	private String calculateFileHash(byte[] fileBytes) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(fileBytes);
		return bytesToHex(hash);
	}

	// Convert bytes to hex string
	private String bytesToHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	@Override
	public Document getDocumentById(Long docId)
    {
		if (docId == null) {
			throw new AppException("L'identifiant du document ne peut être null");
		}

		Document doc = docRepo.findById(docId).orElse(null);
		if (doc == null) {
			throw new AppException("Document inexistant");
		}
		return doc;
	}
}
