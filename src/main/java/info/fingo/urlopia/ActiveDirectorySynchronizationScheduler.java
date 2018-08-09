package info.fingo.urlopia;

import info.fingo.urlopia.team.ActiveDirectoryTeamSynchronizer;
import info.fingo.urlopia.user.ActiveDirectoryUserSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ActiveDirectorySynchronizationScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectorySynchronizationScheduler.class);

    private final ActiveDirectoryUserSynchronizer userSynchronizer;

    private final ActiveDirectoryTeamSynchronizer teamSynchronizer;

    @Autowired
    public ActiveDirectorySynchronizationScheduler(ActiveDirectoryUserSynchronizer userSynchronizer, ActiveDirectoryTeamSynchronizer teamSynchronizer) {
        this.userSynchronizer = userSynchronizer;
        this.teamSynchronizer = teamSynchronizer;
    }

    @Scheduled(cron = "0 00 01 * * *")
    public void fullSynchronization() {
        LOGGER.info("*** FULL SYNCHRONIZATION START ***");
        userSynchronizer.addNewUsers();
        userSynchronizer.deactivateDeletedUsers();
        userSynchronizer.synchronizeFull();
        teamSynchronizer.addNewTeams();
        teamSynchronizer.synchronize();
        teamSynchronizer.assignUsersToTeams();
        teamSynchronizer.removeDeletedTeams();
        LOGGER.info("*** FULL SYNCHRONIZATION END ***");
    }

    @Scheduled(cron = "0 0-59/5 * * * *")
    public void continuousSynchronization() {
        LOGGER.info("*** CONTINUOUS SYNCHRONIZATION START ***");
        userSynchronizer.addNewUsers();
        userSynchronizer.synchronizeIncremental();
        teamSynchronizer.addNewTeams();
        teamSynchronizer.synchronize();
        LOGGER.info("*** CONTINUOUS SYNCHRONIZATION END ***");
    }

    @Bean
    public CommandLineRunner startupSynchronization() {
        return args -> this.fullSynchronization();
    }

}
