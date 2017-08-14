package info.fingo.urlopia;

import info.fingo.urlopia.team.TeamSynchronizer;
import info.fingo.urlopia.user.UserSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ActiveDirectorySynchronizationScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectorySynchronizationScheduler.class);

    private final UserSynchronizer userSynchronizer;

    private final TeamSynchronizer teamSynchronizer;

    public ActiveDirectorySynchronizationScheduler(UserSynchronizer userSynchronizer, TeamSynchronizer teamSynchronizer) {
        this.userSynchronizer = userSynchronizer;
        this.teamSynchronizer = teamSynchronizer;
    }

    @Scheduled(cron = "0 00 01 * * *")
    public void dailySynchronization() {
        LOGGER.info("*** DAILY SYNCHRONIZATION START ***");
        userSynchronizer.findNewUsers();
        userSynchronizer.deactivateDeletedUsers();
        userSynchronizer.fullSynchronize();
        teamSynchronizer.findNewTeams();
        teamSynchronizer.fullSynchronize();
        teamSynchronizer.assignUsersToTeams();
        teamSynchronizer.removeDeletedTeams();
        LOGGER.info("*** DAILY SYNCHRONIZATION STOP ***");
    }

    @Scheduled(cron = "0 0-59/5 * * * *")
    public void continuousSynchronization() {
        LOGGER.info("*** CONTINUOUS SYNCHRONIZATION START ***");
        userSynchronizer.findNewUsers();
        userSynchronizer.checkModifications();
        teamSynchronizer.findNewTeams();
        teamSynchronizer.checkModifications();
        LOGGER.info("*** CONTINUOUS SYNCHRONIZATION STOP ***");
    }

    @Bean
    public CommandLineRunner startupSynchronization() {
        return args -> {
            LOGGER.info("*** STARTUP SYNCHRONIZATION START ***");
            userSynchronizer.findNewUsers();
            teamSynchronizer.findNewTeams();
            teamSynchronizer.assignUsersToTeams();
            LOGGER.info("*** STARTUP SYNCHRONIZATION STOP ***");
        };
    }

}
