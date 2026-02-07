package lenicorp.admin.types.controller.repositories;

import lenicorp.admin.types.model.dtos.TypeGroupDTO;
import lenicorp.admin.types.model.entities.TypeGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for TypeGroup entities
 */
@Repository
public interface TypeGroupRepo extends JpaRepository<TypeGroup, String> {
    
    /**
     * Check if a TypeGroup exists with the given group code
     */
    @Query("SELECT COUNT(tg) > 0 FROM TypeGroup tg WHERE UPPER(tg.groupCode) = UPPER(:groupCode)")
    boolean existsByGroupCode(@Param("groupCode") String groupCode);
    
    /**
     * Check if a TypeGroup exists with the given name
     */
    @Query("SELECT COUNT(tg) > 0 FROM TypeGroup tg WHERE UPPER(tg.name) = UPPER(:name)")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT COUNT(tg) > 0 FROM TypeGroup tg WHERE UPPER(tg.name) = UPPER(:name) and UPPER(tg.groupCode) = UPPER(:code)")
    boolean existsByName(@Param("name") String name, @Param("code") String code);
    
    /**
     * Check if a TypeGroup exists with the given name but different group code
     */
    @Query("SELECT COUNT(tg) > 0 FROM TypeGroup tg WHERE UPPER(tg.name) = UPPER(:name) AND UPPER(tg.groupCode) <> UPPER(:groupCode)")
    boolean existsByNameAndNotGroupCode(@Param("name") String name, @Param("groupCode") String groupCode);
    
    /**
     * Find group code by type code
     */
    @Query("SELECT t.typeGroup.groupCode FROM Type t WHERE UPPER(t.code) = UPPER(:typeCode)")
    String findGroupCodeByTypeCode(@Param("typeCode") String typeCode);
    
    /**
     * Get all type groups as DTOs
     */
    @Query("SELECT new lenicorp.admin.types.model.dtos.TypeGroupDTO(tg.groupCode, tg.name) FROM TypeGroup tg")
    List<TypeGroupDTO> getAllTypeGroupes();
    
    /**
     * Search type groups by key (name or code)
     * Note: This is a native query that requires the unaccent extension in PostgreSQL
     */
    @Query(value = "SELECT * FROM Type_Group tg WHERE unaccent(UPPER(tg.group_code)) LIKE CONCAT('%', unaccent(UPPER(:key)), '%') OR unaccent(UPPER(tg.name)) LIKE CONCAT('%', unaccent(UPPER(:key)), '%') ORDER BY name", 
           nativeQuery = true)
    List<TypeGroup> searchTypeGroupsByKey(@Param("key") String key);
    
    /**
     * Count type groups matching the searchAccessible key
     */
    @Query(value = "SELECT COUNT(*) FROM Type_Group tg WHERE unaccent(UPPER(tg.group_code)) LIKE CONCAT('%', unaccent(UPPER(:key)), '%') OR unaccent(UPPER(tg.name)) LIKE CONCAT('%', unaccent(UPPER(:key)), '%')", 
           nativeQuery = true)
    long countTypeGroupsByKey(@Param("key") String key);

    @Query("""
    select new lenicorp.admin.types.model.dtos.TypeGroupDTO(t.groupCode, t.name) 
        from TypeGroup t where
        locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(t.groupCode, '') ) as string))) >0 or
        locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(t.name, '') ) as string))) >0
    """)
    Page<TypeGroupDTO> searchTypeGroups(String key, Pageable pageable);
}