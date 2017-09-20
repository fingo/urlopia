package info.fingo.urlopia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication  // NOSONAR
public class UrlopiaApplication {

    public static final String DEFAULT_LANGUAGE = "pl";

    public static void main(String[] args) {
        SpringApplication.run(UrlopiaApplication.class, args); // NOSONAR
    }
}
