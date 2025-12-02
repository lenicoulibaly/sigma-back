package lenicorp.admin.security.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditContext
{
    private String connexionId;
    private String ipAddress;
    private String macAddress;
}
