package lenicorp.admin.archive.model.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReadDocDTO
{
	private Long docId;
	private String docNum;
	private String docName;
	private String docDescription;
	private String docPath;
	private String docUniqueCode;
	private String docTypeName;
	private String docExtension;
	private String docMimeType;
	private byte[] file;

	public ReadDocDTO(Long docId, String docNum, String docName, String docDescription,
					  String docPath, String docUniqueCode, String docTypeName,
					  String extension, String mimeType)
	{
		this.docId = docId;
		this.docNum = docNum;
		this.docName = docName;
		this.docDescription = docDescription;
		this.docPath = docPath;
		this.docUniqueCode = docUniqueCode;
		this.docTypeName = docTypeName;
		this.docExtension = extension;
		this.docMimeType = mimeType;
	}
}
