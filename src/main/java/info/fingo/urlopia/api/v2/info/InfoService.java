package info.fingo.urlopia.api.v2.info;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class InfoService {
    private final BuildProperties buildProperties;
    private final GitProperties gitProperties;


    public ApplicationInfoDTO getApplicationInfo() {
        var version = buildProperties.getVersion();
        var commitId = gitProperties.getCommitId().substring(0, 6);
        var buildTime = buildProperties.getTime();
        var buildZonedTime = ZonedDateTime.ofInstant(buildTime, ZoneId.systemDefault());
        return new ApplicationInfoDTO(version,commitId,buildZonedTime);
    }
}
