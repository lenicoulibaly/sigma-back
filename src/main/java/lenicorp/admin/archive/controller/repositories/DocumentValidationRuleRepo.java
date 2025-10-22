package lenicorp.admin.archive.controller.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import lenicorp.admin.archive.model.entities.DocumentValidationRule;
import lenicorp.admin.types.model.entities.Type;

import java.util.List;

@Repository
public interface DocumentValidationRuleRepo extends JpaRepository<DocumentValidationRule, Long>
{
    /**
     * Find all validation rules for a specific document type code
     * @param typeCode the document type code
     * @return list of validation rules
     */
    @Query("SELECT d FROM DocumentValidationRule d WHERE d.documentType.code = :typeCode")
    List<DocumentValidationRule> findByDocumentTypeCode(String typeCode);

    /**
     * Check if an extension is allowed for a specific document type
     * @param typeCode the document type code
     * @param extension the file extension
     * @return true if the extension is allowed, false otherwise
     */
    @Query("SELECT COUNT(d) > 0 FROM DocumentValidationRule d WHERE d.documentType.code = :typeCode AND LOWER(d.allowedExtension) = LOWER(:extension)")
    boolean isExtensionAllowedForType(String typeCode, String extension);

    /**
     * Get the maximum file size allowed for a specific document type and extension
     * @param typeCode the document type code
     * @param extension the file extension
     * @return the maximum file size in bytes, or null if no rule exists
     */
    @Query("SELECT d.maxFileSize FROM DocumentValidationRule d WHERE d.documentType.code = :typeCode AND LOWER(d.allowedExtension) = LOWER(:extension)")
    Long getMaxFileSizeForTypeAndExtension(String typeCode, String extension);
}
