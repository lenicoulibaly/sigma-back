package lenicorp.admin.archive.model.dtos.request;

import lenicorp.admin.archive.model.dtos.validator.ValidDocType;
import lenicorp.admin.archive.model.dtos.validator.ValidFileExtension;
import lenicorp.admin.archive.model.dtos.validator.ValidFileSize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@ValidFileExtension @ValidFileSize
public class UploadDocReq
{
    private Long objectId; //Peut être l'id d'un utilisateur, d'une association, d'une section, ou d'un autre objet futur quand le projet grandira
    @ValidDocType
    private String docTypeCode;
    private String docNum;
    private String docName;
    private String docDescription;
    private MultipartFile file;
}
