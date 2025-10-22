package lenicorp.admin.security.controller.repositories;

import lenicorp.admin.security.model.entities.AppAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepo extends JpaRepository<AppAuthority, String>
{
    @Query("SELECT COUNT(a) > 0 FROM AppAuthority a WHERE a.code = :code")
    boolean existsByCode(@Param("code") String code);

    @Query("SELECT COUNT(a) > 0 FROM AppAuthority a WHERE a.name = :name")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT COUNT(a) > 0 FROM AppAuthority a WHERE a.name = :name AND a.code <> :code")
    boolean existsByName(@Param("name") String name, @Param("code") String code);

    @Query("SELECT COUNT(a) > 0 FROM AppAuthority a WHERE a.code = :code AND a.type.code = :typeCode")
    boolean existsByCodeAndType(@Param("code") String code, @Param("typeCode") String typeCode);
}
