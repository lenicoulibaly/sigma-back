package lenicorp.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "lenicorp")
@EntityScan(basePackages = "lenicorp")
public class JpaScanConfig {
    // Cette configuration force Spring à scanner tout le package racine "lenicorp"
    // pour trouver les Repositories et les Entités JPA, peu importe s'ils sont
    // dans "admin" ou "metier".
}