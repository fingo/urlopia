package info.fingo.urlopia.user

import info.fingo.urlopia.config.persistance.filter.Filter
import spock.lang.Specification

class UserServiceSpec extends Specification {
    def userRepository = Mock(UserRepository)
    def userService = new UserService(userRepository)
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
