package info.fingo.urlopia.config.authentication

import spock.lang.Specification

class WebTokenServiceSpec extends Specification {

    def "isCurrentUserAnAdmin() WHEN user is an admin SHOULD return true"() {
        given:
        def webTokenService = Spy(WebTokenService) {
            getRoles() >> ["ROLES_ADMIN"]}

        when:
        def result = webTokenService.isCurrentUserAnAdmin()

        then:
        result
    }

    def "isCurrentUserAnAdmin() WHEN user is not an admin SHOULD return false"() {
        given:
        def webTokenService = Spy(WebTokenService) {
            getRoles() >> ["ROLES_WORKER"]}

        when:
        def result = webTokenService.isCurrentUserAnAdmin()

        then:
        !result
    }
}


