package lenicorp.admin.archive.model.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Base64FileDto
{
    private String base64String;
    private byte [] bytes;
}
