package info.fingo.urlopia.user

import info.fingo.urlopia.config.ad.ActiveDirectory
import info.fingo.urlopia.config.persistance.filter.Filter
import spock.lang.Specification

class UserServiceSpec extends Specification {
    def userRepository = Mock(UserRepository)
    def activeDirectory = Mock(ActiveDirectory)
    def userService = new UserService(userRepository, activeDirectory)
    def filter = Mock(Filter)

    def "get() SHOULD return list of users"() {
        given:
        def users = [Mock(User)]
        userRepository.findAll(filter) >> users

        when:
        def output = userService.get(filter)

        then:
        output == users

    }
}
