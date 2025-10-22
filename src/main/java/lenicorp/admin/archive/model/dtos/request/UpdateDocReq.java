package lenicorp.admin.archive.model.dtos.request;

import lenicorp.admin.archive.model.dtos.validator.ExistingDocId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateDocReq
{
    @ExistingDocId
    private Long docId;
    private String docTypeCode;
    private String docNum;
    private String docName;
    private String docDescription;
    private MultipartFile file;

    public UpdateDocReq(Long docId, String docTypeCode, String docNum, String docDescription, MultipartFile file)
    {
        this.docId = docId;
        this.docTypeCode = docTypeCode;
        this.docNum = docNum;
        this.docDescription = docDescription;
        this.file = file;
    }
}
