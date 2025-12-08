package lenicorp.admin.workflowengine.execution.archive.impl;

import lenicorp.admin.archive.controller.service.IDocumentService;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.archive.model.entities.Document;
import lenicorp.admin.workflowengine.execution.archive.ArchiveGateway;
import lenicorp.admin.workflowengine.execution.dto.AttachmentRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchiveGatewayImpl implements ArchiveGateway {
    private final IDocumentService documentService;

    @Override
    public List<AttachmentRef> saveAll(List<MultipartFile> files, List<String> fileTypeCodes, Long objectId, String objectTableName) {
        List<AttachmentRef> res = new ArrayList<>();
        if (files == null || files.isEmpty()) return res;
        for (int i = 0; i < files.size(); i++) {
            MultipartFile f = files.get(i);
            if (f == null || f.isEmpty()) continue;
            String docTypeCode = (fileTypeCodes != null && i < fileTypeCodes.size()) ? fileTypeCodes.get(i) : null;
            UploadDocReq dto = new UploadDocReq();
            dto.setObjectId(objectId);
            dto.setObjectTableName(objectTableName);
            dto.setDocTypeCode(docTypeCode);
            dto.setDocName(f.getOriginalFilename());
            dto.setDocDescription("WF Transition Attachment");
            dto.setFile(f);
            try {
                Document d = documentService.uploadDocument(dto);
                res.add(new AttachmentRef(d.getDocId(), d.getDocName(), d.getDocMimeType(), f.getSize()));
            } catch (IOException e) {
                throw new RuntimeException("Failed to store attachment: " + f.getOriginalFilename(), e);
            }
        }
        return res;
    }
}
