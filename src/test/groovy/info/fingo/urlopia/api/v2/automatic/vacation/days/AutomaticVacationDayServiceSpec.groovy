package info.fingo.urlopia.api.v2.automatic.vacation.days

import info.fingo.urlopia.api.v2.automatic.vacation.days.model.AutomaticVacationDay
import info.fingo.urlopia.api.v2.automatic.vacation.days.model.UpdateUserConfig
import info.fingo.urlopia.history.HistoryLogInput
import info.fingo.urlopia.history.HistoryLogService
import spock.lang.Specification
import info.fingo.urlopia.user.User

class AutomaticVacationDayServiceSpec extends Specification{

    def automaticVacationDaysRepository = Mock(AutomaticVacationDaysRepository)
    def historyLogService = Mock(HistoryLogService)
    def automaticVacationDayService = new AutomaticVacationDayService(automaticVacationDaysRepository, historyLogService)

    def "addForNewUser SHOULD create new record in repository"(){
        given:
        def user = Mock(User)

        when:
        automaticVacationDayService.addForNewUser(user)

        then:
        1 * automaticVacationDaysRepository.save(_ as AutomaticVacationDay)

    }

    def "update WHEN automaticVacationDay for given user not exists SHOULD throw AutomaticVacationDaysNotFoundException"(){
        given:
        automaticVacationDaysRepository.findByUserId(_ as Long) >> Optional.empty()
        def updateUserConfig = new UpdateUserConfig(1,1,1)

        when:
        automaticVacationDayService.update(updateUserConfig)

        then:
        thrown(AutomaticVacationDaysNotFoundException)
    }

    def "update WHEN automaticVacationDay for given user not exists SHOULD throw AutomaticVacationDaysNotFoundException"(){
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
        automaticVacationDaysRepository.findByUserId(_ as Long) >> Optional.of(automaticVacationDay)
        automaticVacationDaysRepository.save(_ as AutomaticVacationDay) >> automaticVacationDay
        def updateUserConfig = new UpdateUserConfig(1,1,1)

        when:
        automaticVacationDayService.update(updateUserConfig)

        then:
        notThrown(AutomaticVacationDaysNotFoundException)
    }

    def "getAll WHEN non full-time worker has empty proposition SHOULD count value for him and save it"(){
        def user = Mock(User) {
            getWorkTime() >> 7.0f
            getId() >> 1L
        }
        def vacationDays = new AutomaticVacationDay(user, 26, 0)

        automaticVacationDaysRepository.findAll() >> [vacationDays]
        automaticVacationDaysRepository.save(_ as AutomaticVacationDay) >> {AutomaticVacationDay automaticVacationDay -> automaticVacationDay}

        when:
        def result = automaticVacationDayService.getAll()

        then:
        result.get(0).nextYearProposition() == 0
    }

    def "getAll WHEN full-time worker has empty proposition SHOULD count value for him and save it"(){
        def user = Mock(User) {
            getWorkTime() >> 8.0f
            getId() >> 1L
        }
        def vacationDays = new AutomaticVacationDay(user, 26, 0)
        automaticVacationDaysRepository.findAll() >> [vacationDays]
        automaticVacationDaysRepository.save(_ as AutomaticVacationDay) >> {AutomaticVacationDay automaticVacationDay -> automaticVacationDay}

        when:
        def result = automaticVacationDayService.getAll()

        then:
        result.get(0).nextYearProposition() == 26 * 8
    }

    def "addHours WHEN proposition is empty SHOULD not invoke save on historyLog"() {
        def user = Mock(User) {
            getWorkTime() >> 7.0f
            getId() >> 1L
        }
        def automaticVacationDay = Mock(AutomaticVacationDay) {
            getNextYearHoursProposition() >> 0
            getNextYearDaysBase() >> 26
            getUser() >> user
        }
        automaticVacationDaysRepository.findAll() >> [automaticVacationDay]
        automaticVacationDaysRepository.save(_ as AutomaticVacationDay) >> {AutomaticVacationDay avc -> avc}

        when:
        automaticVacationDayService.addHoursForNewYear()

        then:
        0 * historyLogService.createBySystem(_ as HistoryLogInput, _ as Long)

    }


    def "addHours WHEN proposition is not empty SHOULD  invoke save on historyLog"() {
        def user = Mock(User) {
            getWorkTime() >> 8.0f
            getId() >> 1L
        }
        def automaticVacationDay = Mock(AutomaticVacationDay) {
            getNextYearHoursProposition() >> 100
            getNextYearDaysBase() >> 26
            getUser() >> user
        }
        automaticVacationDaysRepository.findAll() >> [automaticVacationDay]

        when:
        automaticVacationDayService.addHoursForNewYear()

        then:
        1 * historyLogService.createBySystem(_ as HistoryLogInput, _ as Long)

    }


}
