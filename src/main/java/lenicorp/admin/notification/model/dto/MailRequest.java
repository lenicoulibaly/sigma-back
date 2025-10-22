package lenicorp.admin.notification.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class MailRequest {
    
    @Email
    @NotBlank
    private String to;
    private String recipientName;
    private List<@Email String> cc;
    
    private List<@Email String> bcc;
    
    @NotBlank
    private String subject;
    
    @NotBlank
    private String content;
    
    private boolean isHtml = true;
    
    private List<MailAttachment> attachments;
    
    private Map<String, String> headers;
}