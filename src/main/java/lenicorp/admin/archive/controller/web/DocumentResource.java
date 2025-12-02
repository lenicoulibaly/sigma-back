package lenicorp.admin.archive.controller.web;

import lenicorp.admin.archive.controller.service.IDocumentService;
import lenicorp.admin.archive.model.dtos.request.UpdateDocReq;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.archive.model.dtos.response.ReadDocDTO;
import lenicorp.admin.archive.model.entities.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;

@RestController @RequestMapping("/documents") @RequiredArgsConstructor
@Validated
public class DocumentResource
{
    private final IDocumentService docService;

    @Validated()
    @PostMapping(value = "/{objectTableName}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Document uploadDocument( @Validated
            @ModelAttribute UploadDocReq dto,
            @PathVariable("objectTableName") String objectTableName) throws IOException
    {
        dto.setObjectTableName(objectTableName);
        return docService.uploadDocument(dto);
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Document updateDocument(@ModelAttribute UpdateDocReq dto) throws IOException {
        return docService.updateDocument(dto);
    }

    @DeleteMapping("/delete/{docId}")
    public ResponseEntity<Boolean> deleteDocument(@PathVariable("docId") Long docId) throws IOException
    {
        boolean result = docService.deleteDocument(docId);
        return ResponseEntity.ok(result);
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

    @GetMapping(value = "/search/{tableName}/{objectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ReadDocDTO> searchObjectDocs(@PathVariable(value = "tableName", required = true) String tableName,
                                             @PathVariable(value = "objectId", required = true) Long objectId,
                                             @RequestParam(value = "key", required = false) String key, Pageable pageable)
    {
        tableName = tableName.toUpperCase();
        return docService.searchObjectDocs(objectId, tableName, key, pageable);
    }
}
