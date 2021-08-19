package info.fingo.urlopia.api.v2.info

import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import spock.lang.Specification

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class InfoServiceSpec  extends Specification{
    private BuildProperties buildProperties;
    private GitProperties gitProperties;
    private InfoService infoService;

    def setup(){
        buildProperties = Mock(BuildProperties)
        gitProperties = Mock(GitProperties)
        infoService = new InfoService(buildProperties,gitProperties)
    }

    def "getApplicationInfo() WHEN called SHOULD return ApplicationInfoDTO with correct version,commitId and buildTime"(){
        given: "a valid version message"
        def version = "some valid version"

        and: "valid commit message with shortcut"
        def commitId = "ABCDEF Some valid commit letters"
        def commitIdShortcut = commitId.substring(0,6);



        and: "valid build time with valid zoneID"
        def zonedId = ZoneId.systemDefault()
        def buildTime = LocalDate.of(2021,12,12)
        def buildTimeInstant = buildTime.atStartOfDay(zonedId).toInstant()
        def buildZonedTime = ZonedDateTime.ofInstant(buildTimeInstant, zonedId);

        and: "valid expected value"
        def correctApplicationInfoDTO = new ApplicationInfoDTO(version,commitIdShortcut,buildZonedTime)

        buildProperties.getVersion() >> version
        buildProperties.getTime() >> buildTimeInstant
        gitProperties.getCommitId() >> commitId


        when:
        def result = infoService.getApplicationInfo();

        then:
        correctApplicationInfoDTO == result
    }

}
