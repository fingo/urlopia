package info.fingo.urlopia;

import info.fingo.urlopia.config.persistance.BaseRepositoryClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication  // NOSONAR
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryClass.class)
public class UrlopiaApplication {

    public static final String DEFAULT_LANGUAGE = "pl";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) {
        SpringApplication.run(UrlopiaApplication.class, args); // NOSONAR
    }
}
