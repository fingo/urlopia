package info.fingo.urlopia.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty("spring.flyway.enabled")
public class FlywayConfig {

    private static final String FLYWAY_TABLE_NAME = "FLYWAY_MAIN_SCHEMA_HISTORY";
    private static final String SCRIPTS_PATH = "scripts";

    @Value("${urlopia.flyway.baseline-version:1}")
    private String baselineVersion;

    @Bean
    public Flyway flyway(DataSource dataSource) {
        var flyway = Flyway.configure()
                .locations(SCRIPTS_PATH)
                .table(FLYWAY_TABLE_NAME)
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .baselineVersion(baselineVersion)
                .load();
        flyway.migrate();
        return flyway;
    }
}
