package lenicorp.admin.types.model.repositories;

import lenicorp.admin.types.model.entities.Type;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeRepository extends JpaRepository<Type, String> {
}
