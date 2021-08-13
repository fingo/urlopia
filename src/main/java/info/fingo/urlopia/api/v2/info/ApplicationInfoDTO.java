package info.fingo.urlopia.api.v2.info;

import java.time.ZonedDateTime;

public record ApplicationInfoDTO(String version,
                                 String commitId,
                                 ZonedDateTime buildZonedTime)
{}