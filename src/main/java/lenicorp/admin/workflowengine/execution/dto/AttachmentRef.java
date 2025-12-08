package lenicorp.admin.workflowengine.execution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AttachmentRef {
    private Long documentId; // ID from archive module (Document.docId)
    private String name;
    private String contentType;
    private Long size;
}
