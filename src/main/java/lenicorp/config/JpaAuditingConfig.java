package lenicorp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig
{
    @Bean
    public AuditorAware<String> auditorAware()
    {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated())
                return Optional.empty();

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails)
            {
                return Optional.ofNullable(userDetails.getUsername());
            }

            if (principal instanceof String s)
            {
                // Spring often uses "anonymousUser" as a placeholder; ignore it
                if ("anonymousUser".equalsIgnoreCase(s)) return Optional.empty();
                return Optional.of(s);
            }

            return Optional.empty();
        };
    }
}
