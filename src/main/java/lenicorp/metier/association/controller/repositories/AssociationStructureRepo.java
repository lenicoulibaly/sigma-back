package lenicorp.metier.association.controller.repositories;

import lenicorp.metier.association.model.entities.AssociationStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssociationStructureRepo extends JpaRepository<AssociationStructure, Long> {
    @Query("select (count(a) > 0) from AssociationStructure a where a.association.assoId = ?1 and a.structure.strId = ?2")
    boolean existsByAssoIdAndStrId(Long assoId, Long strId);
}
