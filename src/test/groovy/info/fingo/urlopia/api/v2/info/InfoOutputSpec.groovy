package info.fingo.urlopia.api.v2.info

import spock.lang.Specification

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class InfoOutputSpec extends Specification{

    def "fromApplicationInfoDTO() WHEN called with ApplicationInfoDTO SHOULD map it correctly to InfoOutput object"(){
        given: "valid build time with valid zoneID"
        def zonedId = ZoneId.systemDefault()
        def buildTime = LocalDate.of(2021,12,12)
        def buildTimeInstant = buildTime.atStartOfDay(zonedId).toInstant()
        def buildZonedTime = ZonedDateTime.ofInstant(buildTimeInstant, zonedId)

        and: "valid applicationInfoDto object"
        def applicationInfoDTO = new ApplicationInfoDTO(version,commitId,buildZonedTime)

        and: "valid expected value"
        def correctInfoOutput = new InfoOutput(version,commitId,buildZonedTime)


        when:
        def result = InfoOutput.fromApplicationInfoDTO(applicationInfoDTO);

        then:
        result == correctInfoOutput

        where:
        commitId           | version
        "DSF143DSA34534"   | "V1"
        "VMLD46RM7I876ISD" | "V2"
        "BCVBNCIOSDJ4"     | "V2.3"
        "DGFGDQWLZJFSDI8"  | "V4"


    }
}
