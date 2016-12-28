package info.fingo.urlopia;

import info.fingo.urlopia.ad.ActiveDirectory;
import info.fingo.urlopia.ad.LocalUser;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.mail.MailReceiver;
import info.fingo.urlopia.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * Main class with runner.
 * Beans are used to generate & add some data to the db on app startup.
 *
 * @author Mateusz Wiśniewski
 */
@EnableScheduling
@EnableAsync
@SpringBootApplication  // NOSONAR
public class UrlopiaApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlopiaApplication.class);
    public static final String DEFAULT_LANGUAGE = "pl";

    //External values
    @Value("${drop.create}")
    private String dropCreate;

    @Value("${spring.datasource.driver.class.name}")
    private String privateDriverClassName;
    private static String driverClassName;

    @Value("${spring.datasource.url}")
    private String privateUrl;
    private static String url;

    @Value("${spring.datasource.username}")
    private String privateUsername;
    private static String username;

    @Value("${spring.datasource.password}")
    private String privatePassword;
    private static String password;

    @Value("${ad.adminsTeam}")
    private String adminsTeam;

    @Value("${ad.adminUrlopia}")
    private String adminUrlopia;

    @Autowired
    private ActiveDirectory activeDirectory;

    @Autowired
    private MailReceiver mailReceiver;

    @Autowired
    private UserService userService;

    @Autowired
    private HolidayService holidayService;

    //init all static variables
    @PostConstruct
    public void init(){
        driverClassName = privateDriverClassName;
        url = privateUrl;
        username = privateUsername;
        password = privatePassword;
    }

    public static void main(String[] args) {
        SpringApplication.run(UrlopiaApplication.class, args); // NOSONAR
    }

    private static void dropCreateDatabase() {
        String drop = "scripts/drop_tables.sql";
        String create = "scripts/create_tables.sql";

        InputStream dropStream = (UrlopiaApplication.class).getClassLoader().getResourceAsStream(drop);
        InputStream createStream = (UrlopiaApplication.class).getClassLoader().getResourceAsStream(create);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dropStream))) {
            drop = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(3); // NOSONAR
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(createStream))) {
            create = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(3); // NOSONAR
        }

        String[] dropQueries = drop.split(";");
        String[] createQueries = create.split(";");

        Connection connection;
        Statement statement;
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(
                    url,
                    username,
                    password);
            connection.setAutoCommit(false);
            statement = connection.createStatement();

            for (String query : dropQueries)
                statement.executeUpdate(query);
            connection.commit();

            for (String query : createQueries)
                statement.executeUpdate(query);
            connection.commit();

            statement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(4);
        }
    }

    @Bean
    public CommandLineRunner startup() {
        return args -> {
            if ("true".equals(dropCreate)) {
                dropCreateDatabase();

                // set holidays lists
                setHolidaysForThisAndNextYear();
            }
            // synchronize db with ad & set admins
            userService.synchronize();
            setAdminsFromTeam();

            // start listening for new mails
            mailReceiver.start();
        };
    }

    // set all users from team specified in application-default.properties as admins
    private void setAdminsFromTeam() {
        String teamName = adminsTeam;
        assert teamName != null;

        // Setting admins from admins team
        for (LocalUser localUser : activeDirectory.getUsers()) {
            localUser.getTeams().stream()
                    .filter(userTeam -> teamName.equals(userTeam.getName()))
                    .forEach(userTeam -> userService.setAdmin(localUser.getPrincipalName()));
        }

        // Setting admin from app admin
        userService.setAdmin(adminUrlopia);
    }

    private void setHolidaysForThisAndNextYear() {
        //generate static and dynamic holidays to database (also scheduled on 01/01 every year)
        holidayService.addAllHolidays(holidayService.generateHolidaysList(LocalDate.now().getYear()));
        holidayService.addAllHolidays(holidayService.generateHolidaysList(LocalDate.now().getYear() + 1));
    }
}
