package lenicorp.admin.notification.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MailResponse
{
    private boolean success;
    private String messageId;
    private String errorMessage;
    private LocalDateTime sentAt;
}