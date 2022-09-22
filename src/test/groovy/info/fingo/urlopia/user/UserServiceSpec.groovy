package info.fingo.urlopia.user

import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException
import info.fingo.urlopia.config.ad.ActiveDirectory
import info.fingo.urlopia.config.persistance.filter.Filter
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
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

    def "isCurrentUserAdmin WHEN called with other role than admin SHOULD return false"(){
        given:
        def mockedAuthentication = Mock(Authentication){
            getAuthorities() >> [roles]
        }

        def mockedContext = Mock(SecurityContext){
            getAuthentication() >> mockedAuthentication
        }
        SecurityContextHolder.setContext(mockedContext)

        when:
        def result = userService.isCurrentUserAdmin()

        then:
        !result

        where:
        roles << [new SimpleGrantedAuthority("ROLE_ROLES_WORKER"),
                  new SimpleGrantedAuthority("ROLE_ROLES_LEADER")]
    }

    def "isCurrentUserAdmin WHEN called with admin SHOULD return true"(){
        given:
        def mockedAuthentication = Mock(Authentication){
            getAuthorities() >> [new SimpleGrantedAuthority("ROLE_ROLES_ADMIN")]
        }

        def mockedContext = Mock(SecurityContext){
            getAuthentication() >> mockedAuthentication
        }
        SecurityContextHolder.setContext(mockedContext)

        when:
        def result = userService.isCurrentUserAdmin()

        then:
        result
    }

    def "getCurrentUserId() WHEN called with user not from db SHOULD throw UnauthorizedException"(){
        given:
        def mockedAuthentication = Mock(Authentication){
            getPrincipal() >> "example"
        }
        def mockedContext = Mock(SecurityContext){
            getAuthentication() >> mockedAuthentication
        }
        SecurityContextHolder.setContext(mockedContext)

        userRepository.findFirstByPrincipalName(_ as String) >> Optional.empty()

        when:
        userService.getCurrentUserId()

        then:
        thrown(UnauthorizedException)
    }

    def "getCurrentUserId() WHEN called with user from db SHOULD not throw UnauthorizedException"(){
        given:
        def mockedAuthentication = Mock(Authentication){
            getPrincipal() >> "example"
        }
        def mockedContext = Mock(SecurityContext){
            getAuthentication() >> mockedAuthentication
        }
        SecurityContextHolder.setContext(mockedContext)

        def user = Mock(User){
            getId() >> 1
        }
        userRepository.findFirstByPrincipalName(_ as String) >> Optional.of(user)

        when:
        userService.getCurrentUserId()

        then:
        notThrown(UnauthorizedException)
    }
}
