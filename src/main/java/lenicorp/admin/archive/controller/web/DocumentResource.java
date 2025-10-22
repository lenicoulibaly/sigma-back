package lenicorp.admin.archive.controller.web;

import lenicorp.admin.archive.controller.service.AbstractDocumentService;
import lenicorp.admin.archive.controller.service.DocServiceProvider;
import lenicorp.admin.archive.model.dtos.request.UpdateDocReq;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.archive.model.dtos.response.Base64FileDto;
import lenicorp.admin.archive.model.dtos.response.ReadDocDTO;
import lenicorp.admin.archive.model.entities.Document;
import lenicorp.admin.types.model.dtos.TypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentResource {

    @Autowired
    private DocServiceProvider docServiceProvider;

    @Autowired 
    private AbstractDocumentService docService;

    @GetMapping("/{typeDocUniqueCode}/types")
    public List<TypeDTO> getTypeDocumentReglement(@PathVariable("typeDocUniqueCode") String typeDocUniqueCode)
    {
        AbstractDocumentService docService = docServiceProvider.getDocUploader(typeDocUniqueCode);
        return docService.getTypeDocumentReglement(typeDocUniqueCode);
    }

    @PostMapping("/{groupDocUniqueCode}/upload")
    public Document uploadDocument(
            @RequestParam("objectId") Long objectId,
            @RequestParam("docTypeCode") String docTypeCode,
            @RequestParam(value = "docNum", required = false) String docNum,
            @RequestParam(value = "docName", required = false) String docName,
            @RequestParam(value = "docDescription", required = false) String docDescription,
            @RequestParam("file") MultipartFile file,
            @PathVariable("groupDocUniqueCode") String groupDocUniqueCode) throws IOException
    {
        groupDocUniqueCode = groupDocUniqueCode == null || groupDocUniqueCode.trim().equals("") 
            ? ""
            : groupDocUniqueCode.replace("-", "_").toUpperCase();

        AbstractDocumentService docUploader = docServiceProvider.getDocUploader(groupDocUniqueCode);

        // Create a DTO with the parameters
        UploadDocReq dto = new UploadDocReq();
        dto.setObjectId(objectId);
        dto.setDocTypeCode(docTypeCode);
        dto.setDocNum(docNum);
        dto.setDocName(docName);
        dto.setDocDescription(docDescription);

        // Convert MultipartFile to InputStream
        try (InputStream inputStream = file.getInputStream()) {
            // Use reflection to set the file field
            java.lang.reflect.Field fileField = UploadDocReq.class.getDeclaredField("file");
            fileField.setAccessible(true);
            fileField.set(dto, inputStream);

            Document doc = docUploader.uploadDocument(dto);
            return doc;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IOException("Error processing file: " + e.getMessage(), e);
        }
    }

    @PutMapping("/update")
    public Document updateDocument(
            @RequestParam("docId") Long docId,
            @RequestParam(value = "docTypeCode", required = false) String docTypeCode,
            @RequestParam(value = "docNum", required = false) String docNum,
            @RequestParam(value = "docName", required = false) String docName,
            @RequestParam(value = "docDescription", required = false) String docDescription,
            @RequestParam("file") MultipartFile file) throws IOException {

        // Create a DTO with the parameters
        UpdateDocReq dto = new UpdateDocReq();
        dto.setDocId(docId);
        dto.setDocTypeCode(docTypeCode);
        dto.setDocNum(docNum);
        dto.setDocName(docName);
        dto.setDocDescription(docDescription);

        // Convert MultipartFile to InputStream
        try (InputStream inputStream = file.getInputStream()) {
            // Use reflection to set the file field
            java.lang.reflect.Field fileField = UpdateDocReq.class.getDeclaredField("file");
            fileField.setAccessible(true);
            fileField.set(dto, inputStream);

            Document doc = docService.updateDocument(dto);
            return doc;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IOException("Error processing file: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete/{docId}")
    public ResponseEntity<Boolean> deleteDocument(@PathVariable("docId") Long docId) throws IOException
    {
        boolean result = docService.deleteDocument(docId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ReadDocDTO>> getUserDocs(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "key", defaultValue = "") String key) {
        // For now, return an empty list
        // In a real implementation, you would query the database for documents
        return ResponseEntity.ok(new ArrayList<ReadDocDTO>());
    }

    @GetMapping("/associations/{assoId}")
    public ResponseEntity<List<ReadDocDTO>> getAssociationDocs(
            @PathVariable("assoId") Long assoId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "key", defaultValue = "") String key) {
        // For now, return an empty list
        // In a real implementation, you would query the database for documents
        return ResponseEntity.ok(new ArrayList<ReadDocDTO>());
    }

    @GetMapping("/sections/{sectionId}")
    public ResponseEntity<List<ReadDocDTO>> getSectionDocs(
            @PathVariable("sectionId") Long sectionId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "key", defaultValue = "") String key) {
        // For now, return an empty list
        // In a real implementation, you would query the database for documents
        return ResponseEntity.ok(new ArrayList<ReadDocDTO>());
    }

    @GetMapping("/get-base64/{docId}")
    public Base64FileDto displayDocument(@PathVariable("docId") Long docId) throws Exception {
        return docService.displayDocument(docId);
    }

    @GetMapping(value = "/download/{docId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadFile(@PathVariable("docId") Long docId) {
        Document doc = docService.getDocumentById(docId);
        byte[] fileBytes = docService.downloadFile(doc.getDocPath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getDocName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, doc.getDocMimeType())
                .body(fileBytes);
    }
}
