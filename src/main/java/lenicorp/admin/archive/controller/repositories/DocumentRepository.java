package lenicorp.admin.archive.controller.repositories;

import lenicorp.admin.archive.model.dtos.response.ReadDocDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import lenicorp.admin.archive.model.entities.Document;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>
{
    /**
     * Get the document path by document ID
     * @param docId the document ID
     * @return the document path
     */
    @Query("SELECT d.docPath FROM Document d WHERE d.docId = :docId")
    String getDocumentPath(@Param("docId") Long docId);

    /**
     * Find documents by user
     * @param userId the user ID
     * @return list of documents
     */
    List<Document> findByUserUserId(Long userId);

    /**
     * Find documents by association
     * @param associationId the association ID
     * @return list of documents
     */
    //List<Document> findByAssociationId(Long associationId);

    /**
     * Find documents by section
     * @param sectionId the section ID
     * @return list of documents
     */
    //List<Document> findBySectionId(Long sectionId);

    /**
     * Find documents by name containing the given text (case insensitive)
     * @param name the name to search for
     * @return list of documents
     */
    @Query("SELECT d FROM Document d WHERE LOWER(d.docName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Document> findByDocNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Get all documents for a versement
     * @param versementId the versement ID
     * @return a list of documents
     */
    //List<Document> findByVersementId(Long versementId);

    /**
     * Get the logo for an association
     * @param assoId the association ID
     * @return the logo document
     */
    //Document findLogoByAssociationId(Long assoId);

    @Query("select d from Document d")
    Page<ReadDocDTO> getAllDocsForObject(Long userId, Long assoId, Long sectionId, String key, PageRequest pageRequest);
}
