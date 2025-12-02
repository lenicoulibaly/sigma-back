package lenicorp.admin.archive.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.security.model.entities.AppUser;
import lenicorp.admin.types.model.entities.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
@Audited
@Table(name = "document")
public class Document extends AuditableEntity
{
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOC_ID_GEN")
	@SequenceGenerator(name = "DOC_ID_GEN", sequenceName = "DOC_ID_GEN")
	@Column(name = "doc_id")
	private Long docId;
	@Column(name = "doc_num")
	private String docNum;
	@Column(name = "doc_name")
	private String docName;
	@Column(name = "doc_description", length = 10000)
	private String docDescription;
	@Column(name = "doc_path")
	private String docPath;
	@Column(name = "doc_extension")
	private String docExtension;
	@Column(name = "doc_mime_type")
	private String docMimeType;
	@ManyToOne @JoinColumn(name = "TYPE_CODE") @NotAudited
	private Type docType;

	@ManyToOne @JoinColumn(name = "TABLE_NAME")
	private Type objectTableName;

	private Long objectId; //L'ID de l'objet auquel appartient le document

    private byte[] file;
}
