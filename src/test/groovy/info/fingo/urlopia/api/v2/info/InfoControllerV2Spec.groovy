package info.fingo.urlopia.api.v2.info

import spock.lang.Specification

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class InfoControllerV2Spec extends Specification{
    private InfoService infoService
    private ApplicationInfoDTO applicationInfoDTO
    private InfoOutput correctInfoOutput
    private InfoControllerV2 infoControllerV2

    void setup(){
        infoService = Mock(InfoService)
        infoControllerV2 = new InfoControllerV2(infoService)

        def version = "some valid version"
        def commitId = "ABCDEF Some valid commit letters"
        def zonedId = ZoneId.systemDefault()
        def buildTime = LocalDate.of(2021,12,12)
        def buildTimeInstant = buildTime.atStartOfDay(zonedId).toInstant()
        def buildZonedTime = ZonedDateTime.ofInstant(buildTimeInstant, zonedId);
        applicationInfoDTO = new ApplicationInfoDTO(version,commitId,buildZonedTime)
        correctInfoOutput = new InfoOutput(version,commitId,buildZonedTime)

    }

    def "getApplicationInfo() WHEN called SHOULD call infoService and return mapped value"(){
        given:
        infoService.getApplicationInfo() >> applicationInfoDTO

        when:
        def result = infoControllerV2.getApplicationInfo()

        then:
        result == correctInfoOutput
    }
}
