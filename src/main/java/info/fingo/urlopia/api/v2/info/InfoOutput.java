package info.fingo.urlopia.api.v2.info;

import java.time.ZonedDateTime;

public record InfoOutput(String version,
                         String commitId,
                         ZonedDateTime buildZonedTime){

    public static InfoOutput fromApplicationInfoDTO(ApplicationInfoDTO applicationInfoDTO){
        return  new InfoOutput(applicationInfoDTO.version(),
                            applicationInfoDTO.commitId(),
                            applicationInfoDTO.buildZonedTime());
    }
}
