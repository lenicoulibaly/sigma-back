package lenicorp.admin.archive.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.types.model.entities.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "document_validation_rule",
        uniqueConstraints =
        {
           @UniqueConstraint(
                name = "uk_doc_type_extension",
                columnNames = {"TYPE_CODE", "allowed_extension"})
        })
@Data @NoArgsConstructor @AllArgsConstructor
public class DocumentValidationRule
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOC_VAL_RULE_GEN")
    @SequenceGenerator(name = "DOC_VAL_RULE_GEN", sequenceName = "DOC_VAL_RULE_GEN")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "TYPE_CODE", nullable = false)
    private Type documentType;
    @Column(name = "allowed_extension", nullable = false)
    private String allowedExtension; // ex: "pdf", "jpg"
    @Column(name = "max_file_size")
    private Long maxFileSize; // en octets (ex: 5242880 pour 5 Mo)
}
