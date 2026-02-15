package lenicorp.admin.archive.controller.repositories;

import lenicorp.admin.archive.model.dtos.response.ReadDocDTO;
import lenicorp.admin.types.model.dtos.TypeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
     * @param name the name to searchAccessible for
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

    @Query("""
        select new lenicorp.admin.types.model.dtos.TypeDTO(t.code, t.name, t.ordre, t.typeGroup.groupCode, t.description)
        from Type t where t.typeGroup.groupCode = 'DOC' order by t.ordre
        """)
    List<TypeDTO> getTypeDocument();

    @Query("""
        select new lenicorp.admin.types.model.dtos.TypeDTO(t.code, t.name, t.ordre, t.typeGroup.groupCode, t.description)
        from Type t where t.typeGroup.groupCode = 'DOC' 
        and exists (select tm.mappingId from TypeMapping tm where tm.parent.code = ?1 and tm.child.code = t.code)
        order by t.ordre
        """)
    List<TypeDTO> getTypeDocument(String parentTypeCode);

    @Query("""
    select new lenicorp.admin.archive.model.dtos.response.ReadDocDTO(
        d.docId, d.docNum, d.docName, d.docDescription, d.docPath
            , d.docType.code, d.docType.name, d.docExtension, d.docMimeType) 
        from Document d where d.objectTableName.code = coalesce(:tableName, d.objectTableName.code) 
        and d.objectId = coalesce(:objectId, d.objectId) 
        and (locate(upper(function('unaccent', :key)), upper(function('unaccent', d.docName))) > 0
            or locate(upper(function('unaccent', :key)), upper(function('unaccent', d.docDescription))) > 0
            or locate(upper(function('unaccent', :key)), upper(function('unaccent', d.docNum))) > 0)
    """)
    Page<ReadDocDTO> searchObjectDocs(@Param("objectId") Long objectId,
                                    @Param("tableName")String tableName,
                                    @Param("key")String key, Pageable pageable);

    @Query("""
    select new lenicorp.admin.archive.model.dtos.response.ReadDocDTO(
        d.docId, d.docNum, d.docName, d.docDescription, d.docPath
            , d.docType.code, d.docType.name, d.docExtension, d.docMimeType) 
        from Document d where d.objectTableName.code = coalesce(:tableName, d.objectTableName.code) 
        and d.objectId = coalesce(:objectId, d.objectId) 
        and (locate(upper(function('unaccent', :key)), upper(function('unaccent', d.docName))) > 0
            or locate(upper(function('unaccent', :key)), upper(function('unaccent', d.docDescription))) > 0
            or locate(upper(function('unaccent', :key)), upper(function('unaccent', d.docNum))) > 0)
    """)
    List<ReadDocDTO> searchObjectDocs(@Param("objectId") Long objectId,
                                      @Param("tableName")String tableName,
                                      @Param("key")String key);

    @Query("""
    select new lenicorp.admin.archive.model.dtos.response.ReadDocDTO(
        d.docId, d.docNum, d.docName, d.docDescription, d.docPath
            , d.docType.code, d.docType.name, d.docExtension, d.docMimeType) 
        from Document d where d.objectId = ?1 and d.objectTableName.code = ?2 and d.docType.code = ?3
    """)
    List<ReadDocDTO> findByObjectIdAndTableNameAndTypeCode(Long assoId, String tableName, String typeCode);

    @Query("""
    select new lenicorp.admin.archive.model.dtos.response.ReadDocDTO(
        d.docId, d.docNum, d.docName, d.docDescription, d.docPath
            , d.docType.code, d.docType.name, d.docExtension, d.docMimeType)
        from Document d where d.objectId = :objectId and d.objectTableName.code = :tableName and d.docType.code = :typeCode
        order by d.docId desc
    """)
    List<ReadDocDTO> findLatestByObjectAndTableAndType(@Param("objectId") Long objectId,
                                                       @Param("tableName") String tableName,
                                                       @Param("typeCode") String typeCode,
                                                       Pageable pageable);

    /**
     * Vérifie s'il existe au moins un document pour un objet/table/type donnés
     */
    @Query("""
        select (count(d) > 0) from Document d
        where d.objectId = :objectId and d.objectTableName.code = :tableName and d.docType.code = :typeCode
    """)
    boolean existsByObjectAndTableAndType(@Param("objectId") Long objectId,
                                          @Param("tableName") String tableName,
                                          @Param("typeCode") String typeCode);
}
