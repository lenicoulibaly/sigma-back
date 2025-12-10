package lenicorp.admin.workflowengine.model.repositories;

import lenicorp.admin.workflowengine.model.entities.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    Optional<Workflow> findByCodeAndActiveTrue(String code);

    List<Workflow> findByTargetTableName_CodeAndActiveTrue(String targetTableNameCode);

    @Query(value = """
            SELECT w FROM Workflow w 
            WHERE (
                UPPER(FUNCTION('unaccent', COALESCE(w.code, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(w.libelle, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(w.type.code, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(w.targetTableName.code, ''))) LIKE :key
            )
            AND (:active IS NULL OR w.active = :active)
            """,
            countQuery = """
            SELECT COUNT(w) FROM Workflow w 
            WHERE (
                UPPER(FUNCTION('unaccent', COALESCE(w.code, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(w.libelle, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(w.type.code, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(w.targetTableName.code, ''))) LIKE :key
            )
            AND (:active IS NULL OR w.active = :active)
            """)
    Page<Workflow> search(@Param("key") String key, @Param("active") Boolean active, Pageable pageable);
}
