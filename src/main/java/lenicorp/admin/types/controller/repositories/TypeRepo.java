package lenicorp.admin.types.controller.repositories;

import lenicorp.admin.types.model.dtos.TypeDTO;
import lenicorp.admin.types.model.entities.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Type entities
 */
@Repository
public interface TypeRepo extends JpaRepository<Type, String> {
    
    /**
     * Check if a Type exists with the given code
     */
    @Query("SELECT COUNT(t) > 0 FROM Type t WHERE UPPER(t.code) = UPPER(:code)")
    boolean existsByCode(@Param("code") String code);
    
    /**
     * Check if a Type exists with the given name
     */
    @Query("SELECT COUNT(t) > 0 FROM Type t WHERE UPPER(t.name) = UPPER(:name)")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT COUNT(t) > 0 FROM Type t WHERE UPPER(t.name) = UPPER(:name) and t.code <> :code")
    boolean existsByName(@Param("name") String name, @Param("code") String code);
    
    /**
     * Check if a Type exists with the given name but different code
     */
    @Query("SELECT COUNT(t) > 0 FROM Type t WHERE UPPER(t.name) = UPPER(:name) AND UPPER(t.code) <> UPPER(:code)")
    boolean existsByNameAndNotCode(@Param("name") String name, @Param("code") String code);
    
    /**
     * Find direct child types for a parent type
     */
    @Query("SELECT tm.child FROM TypeMapping tm WHERE UPPER(tm.parent.code) = UPPER(:parentCode)")
    List<Type> findDirectChildTypes(@Param("parentCode") String parentCode);
    
    /**
     * Find types by group code
     */
    @Query("""
    select new lenicorp.admin.types.model.dtos.TypeDTO(t.code, t.name, t.ordre, t.typeGroup.groupCode, t.description)
                   from Type t  where UPPER(t.typeGroup.groupCode) = UPPER(:groupCode)""")
    List<TypeDTO> findByGroupCode(@Param("groupCode") String groupCode);
    
    /**
     * Get parent type codes for a type
     */
    @Query("SELECT tm.parent.code FROM TypeMapping tm WHERE UPPER(tm.child.code) = UPPER(:typeCode)")
    List<String> getParentTypeCodes(@Param("typeCode") String typeCode);
    
    /**
     * Check if a Type exists with the given code and group code
     */
    @Query("SELECT COUNT(t) > 0 FROM Type t WHERE UPPER(t.code) = UPPER(:typeCode) AND UPPER(t.typeGroup.groupCode) = UPPER(:groupCode)")
    boolean existsByCodeAndGroupCode(@Param("typeCode") String typeCode, @Param("groupCode") String groupCode);
    
    /**
     * Search types by key (code, name, or description) and optional group codes
     * Note: This is a native query that requires the unaccent extension in PostgreSQL
     */
    @Query(value = "SELECT * FROM Type t WHERE (unaccent(UPPER(t.code)) LIKE CONCAT('%', unaccent(UPPER(:key)), '%') OR unaccent(UPPER(t.name)) LIKE CONCAT('%', unaccent(UPPER(:key)), '%') OR unaccent(UPPER(t.description)) LIKE CONCAT('%', unaccent(UPPER(:key)), '%')) AND (:hasGroupFilter = false OR t.group_code IN :groupCodes) ORDER BY t.group_code, t.name", 
           nativeQuery = true)
    List<Type> searchTypesByKey(@Param("key") String key, @Param("groupCodes") List<String> groupCodes, @Param("hasGroupFilter") boolean hasGroupFilter);

    @Query("""
        select new lenicorp.admin.types.model.dtos.TypeDTO(t.code, t.name, t.ordre, t.typeGroup.groupCode, t.description) 
        from Type t where (:hasGroupFilter = false or t.typeGroup.groupCode in :groupCodes) and (
        locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(t.code, '') ) as string))) >0 or
        locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(t.name, '') ) as string))) >0 or
        locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(t.description, '') ) as string))) >0)
        """)
    Page<TypeDTO> searchTypes(@Param("key") String key, @Param("groupCodes") List<String> groupCodes, @Param("hasGroupFilter") boolean hasGroupFilter, Pageable pageable);

    @Query("""
        select new lenicorp.admin.types.model.dtos.TypeDTO(t.code, t.name, t.ordre, t.typeGroup.groupCode, t.description) 
        from TypeMapping tm join tm.child t where tm.parent.code = ?1
        """)
    List<TypeDTO> findDirectSousTypes(String parentCode);

    @Query("""
        select new lenicorp.admin.types.model.dtos.TypeDTO(t.code, t.name, t.ordre, t.typeGroup.groupCode, t.description) 
        from Type t where t.typeGroup.groupCode = ?1
""")
    List<TypeDTO> findByTypeGroup(String groupCode);
}