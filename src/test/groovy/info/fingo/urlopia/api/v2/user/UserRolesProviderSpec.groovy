package info.fingo.urlopia.api.v2.user

import info.fingo.urlopia.acceptance.AcceptanceService
import info.fingo.urlopia.user.NoSuchUserException
import info.fingo.urlopia.user.User
import spock.lang.Specification

class UserRolesProviderSpec extends Specification{

    private AcceptanceService acceptanceService
    private UserRolesProvider userRolesProvider

    void setup(){
        acceptanceService = Mock(AcceptanceService)
        userRolesProvider = new UserRolesProvider(acceptanceService)
    }

    def "getRolesFromUser() WHEN user is inactive SHOULD throw NoSuchUserException"(){
        given:
        def user = Mock(User){
            isActive() >> false
            getLeader() >> false
            getAdmin() >> false
        }

        when:
        userRolesProvider.getRolesFromUser(user)

        then:
        thrown(NoSuchUserException)

    }

    def "getRolesFromUser() WHEN user has active acceptances SHOULD add leader role to result "(){
        given:
        def user = Mock(User){
            isActive() >> true
            getLeader() >> false
            getAdmin() >> false
        }
        acceptanceService.hasActiveAcceptances(_ as User) >> true

        when:
        def result = userRolesProvider.getRolesFromUser(user)

        then:
        result.contains(User.Role.LEADER.toString())

    }

    def "getRolesFromUser() WHEN user is active SHOULD add worker role to result "(){
        given:
        def user = Mock(User){
            isActive() >> true
            getLeader() >> false
            getAdmin() >> false
        }
        acceptanceService.hasActiveAcceptances(_ as User) >> false

        when:
        def result = userRolesProvider.getRolesFromUser(user)

        then:
        result.contains(User.Role.WORKER.toString())

    }

    def "getRolesFromUser() WHEN user is active and is leader SHOULD add leader role to result "(){
        given:
        def user = Mock(User){
            isActive() >> true
            getLeader() >> true
            getAdmin() >> false
        }
        acceptanceService.hasActiveAcceptances(_ as User) >> false

        when:
        def result = userRolesProvider.getRolesFromUser(user)

        then:
        result.contains(User.Role.LEADER.toString())
        result.contains(User.Role.WORKER.toString())
    }

    def "getRolesFromUser() WHEN user is active and is admin SHOULD add admin role to result "(){
        given:
        def user = Mock(User){
            isActive() >> true
            getLeader() >> false
            getAdmin() >> true
        }
        acceptanceService.hasActiveAcceptances(_ as User) >> false

        when:
        def result = userRolesProvider.getRolesFromUser(user)

        then:
        result.contains(User.Role.ADMIN.toString())
        result.contains(User.Role.WORKER.toString())
    }
}
