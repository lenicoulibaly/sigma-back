package lenicorp.admin.security.controller.repositories;

import lenicorp.admin.security.model.entities.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuthTokenRepo extends JpaRepository<AuthToken, Long>
{
    @Query("SELECT t FROM AuthToken t WHERE t.token = :token")
    AuthToken findByToken(@Param("token") String token);

    @Query("SELECT COUNT(t) > 0 FROM AuthToken t WHERE t.token = :token AND t.user.userId = :userId")
    boolean existsByTokenAndUserId(@Param("token") String token, @Param("userId") Long userId);

    // This method uses a default implementation since it requires logic beyond a simple query
    default boolean tokenHasNotExpired(String token)
    {
        AuthToken authToken = findByToken(token);
        if (authToken == null) return false;
        return authToken.getExpirationDate() != null && authToken.getExpirationDate().isAfter(LocalDateTime.now());
    }

    @Query("SELECT COUNT(t) > 0 FROM AuthToken t WHERE t.token = :token AND t.alreadyUsed = true")
    boolean tokenIsAlreadyUsed(@Param("token") String token);
}
