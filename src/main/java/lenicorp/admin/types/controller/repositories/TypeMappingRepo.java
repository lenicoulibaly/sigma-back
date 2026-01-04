package lenicorp.admin.types.controller.repositories;

import lenicorp.admin.types.model.entities.TypeMapping;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for TypeMapping entities
 */
@Repository
public interface TypeMappingRepo extends JpaRepository<TypeMapping, Long> {
    
    /**
     * Check if a parent type has a direct child type
     */
    @Query("SELECT COUNT(tm) > 0 FROM TypeMapping tm WHERE UPPER(tm.parent.code) = UPPER(:parentCode) AND UPPER(tm.child.code) = UPPER(:childCode)")
    boolean parentHasDirectSousType(@Param("parentCode") String parentCode, @Param("childCode") String childCode);
    
    /**
     * Find child type codes for a parent
     */
    @Query("SELECT tm.child.code FROM TypeMapping tm WHERE UPPER(tm.parent.code) = UPPER(:parentCode)")
    List<String> findChildCodesByParentCode(@Param("parentCode") String parentCode);
    
    /**
     * Find child type codes that are not in the input list
     */
    @Query("SELECT tm.child.code FROM TypeMapping tm WHERE UPPER(tm.parent.code) = UPPER(:parentCode) AND UPPER(tm.child.code) NOT IN (:inputCodes)")
    List<String> findChildCodesNotInList(@Param("parentCode") String parentCode, @Param("inputCodes") List<String> inputCodes);
    
    /**
     * Find type codes that could be added as children (they exist but are not currently children)
     */
    @Query("SELECT t.code FROM Type t WHERE NOT EXISTS (SELECT tm FROM TypeMapping tm WHERE UPPER(tm.parent.code) = UPPER(:parentCode) AND UPPER(tm.child.code) = UPPER(t.code)) AND UPPER(t.code) IN (:inputCodes)")
    List<String> findPotentialChildCodes(@Param("parentCode") String parentCode, @Param("inputCodes") List<String> inputCodes);
    
    /**
     * Remove child types from a parent
     */
    @Modifying
    @Query("DELETE FROM TypeMapping tm WHERE UPPER(tm.parent.code) = UPPER(:parentCode) AND UPPER(tm.child.code) IN (:childCodes)")
    void removeChildTypes(@Param("parentCode") String parentCode, @Param("childCodes") List<String> childCodes);

    /**
     * Liste des types enfants du type dont le code est en 1er paramètre
     * mais dont le code ne fait pas partie de la liste de code fournie en 2n paramètre
     */
    @Query("""
    select distinct t.code from TypeMapping tm join tm.child t where tm.parent.code = ?1 and t.code not in ?2
    """)
    List<String> findSousTypeCodesToRemove(String code, List<String> inputSousTypeCodes);

    @Query("""
    select t.code from TypeMapping tm join tm.child t where tm.parent.code = ?1
    """)
    List<String> findAllSousTypeCodes(String code);

    /**
     * Liste des typeCode présents dans la liste de code fournie en 2n paramètre
     * mais qui ne sont pas enfants du type dont le code est passé en 1er premier paramètre
     */
    @Query("""
    select t.code from Type t where t.code in ?2 and 
    not exists (select tm from TypeMapping tm where tm.parent.code = ?1 and tm.child.code = t.code)
    """)
    List<String> findSousTypeCodesToAdd(@NotNull(message = "Le code est obligatoire") @NotBlank(message = "Le code est obligatoire") String code, List<String> inputSousTypeCodes);

    @Modifying
    @Query("""
    delete from TypeMapping tm where tm.parent.code = ?1 and tm.child.code in ?2
    """)
    void removeSousTypes(@NotNull(message = "Le code est obligatoire") @NotBlank(message = "Le code est obligatoire") String code, List<String> sousTypeCodesToRemove);

    @Modifying
    @Query("""
    delete from TypeMapping tm where tm.child.code = ?1 and tm.parent.code in ?2
    """)
    void removeParents(@NotNull(message = "Le code est obligatoire") @NotBlank(message = "Le code est obligatoire") String childCode, List<String> parentTypeCodesToRemove);

    @Query("""
    select distinct t.code from TypeMapping tm join tm.parent t where tm.child.code = ?1 and t.code not in ?2
    """)
    List<String> findParentCodesToRemove(String childCode, List<String> inputParentCodes);

    @Query("""
    select t.code from Type t where t.code in ?2 and 
    not exists (select tm from TypeMapping tm where tm.child.code = ?1 and tm.parent.code = t.code)
    """)
    List<String> findParentCodesToAdd(String childCode, List<String> inputParentCodes);
}