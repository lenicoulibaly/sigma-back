package lenicorp.admin.archive.controller.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import lenicorp.admin.archive.model.dtos.request.UpdateDocReq;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.archive.model.dtos.response.Base64FileDto;
import lenicorp.admin.archive.model.dtos.response.ReadDocDTO;
import lenicorp.admin.archive.model.entities.Document;
import lenicorp.admin.types.model.dtos.TypeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

public interface IDocumentService
{
	void uploadFile(InputStream file, String destinationPath) throws RuntimeException;
	byte[] downloadFile(String filePAth);
	ResponseEntity<?> downloadFile(Long docI);

    @Transactional
	Document uploadDocument(UploadDocReq dto) throws IOException;

	boolean deleteDocument(Long docId) throws UnknownHostException;

	@Transactional
	Document updateDocument(UpdateDocReq dto) throws IOException;

    void displayPdf(HttpServletResponse response, byte[] reportBytes, String displayName)  throws Exception;

    Part downloadMultipartFile(String filePAth);
	String generatePath(String extension, String typeCode, String objectName) throws IOException;
    boolean deleteFile(String filePath);

	Page<ReadDocDTO> searchObjectDocs(Long objectI, String tableName, String key, Pageable pageable);

    List<byte[]> getFileByObjectIdAndTableNameAndTypeCode(Long objectId, String tableName, String typeCode);

    void renameFile(String oldPath, String newPath);

	Page<ReadDocDTO> getAllDocsForObject(Long userId, Long assoId, Long sectionId, String key, PageRequest pageRequest);

	//------

	void uploadFile(MultipartFile file, String destinationPath) throws RuntimeException;

	//------
    Document getDocumentById(Long docId);

    // Latest document fetcher
    ReadDocDTO getLatestByTypeAndObject(Long objectId, String tableName, String typeCode);

    // Existence check for a document by object, table and type
    boolean existsByTypeAndObject(Long objectId, String tableName, String typeCode);
}
