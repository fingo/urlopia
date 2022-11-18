package info.fingo.urlopia.api.v2.automatic.vacation.days.model

import info.fingo.urlopia.user.User
import spock.lang.Specification

class AutomaticVacationDayDTOSpec extends Specification{

    def "from SHOULD map correctly AutomaticVacationDay to AutomaticVacationDayDTO"(){
        given:
        def user = Mock(User) {
            getId() >> 1L
            getFullName() >> "Jane Doe"
            getWorkTime() >> 8.0
        }
        def automaticVacationDay = Mock(AutomaticVacationDay) {
            getNextYearHoursProposition() >> 100
            getNextYearDaysBase() >> 26
            getUser() >> user
        }

        when:
        def result = AutomaticVacationDayDTO.from(automaticVacationDay)

        then:
        result.userId() == 1L
        result.userFullName() == "Jane Doe"
        result.workTime() == 8.0
        result.nextYearDaysBase() == 26
        result.nextYearProposition() == 100
    }

}
