package lenicorp.admin.notification.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailAttachment {
    
    private String fileName;
    private byte[] content;
    private String contentType;
    private boolean inline = false;
    private String contentId; // Pour les images inline
}