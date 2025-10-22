package lenicorp.admin.security.controller.repositories;

import lenicorp.admin.security.model.views.VProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for VProfile entities
 */
@Repository
public interface VProfileRepo extends JpaRepository<VProfile, String>
{
    // JpaRepository already provides methods like findAll(), findById(), etc.
}
