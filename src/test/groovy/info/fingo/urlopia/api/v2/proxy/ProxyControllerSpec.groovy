package info.fingo.urlopia.api.v2.proxy

import info.fingo.urlopia.api.v2.proxy.presence.PresenceConfirmationProxyInput
import info.fingo.urlopia.api.v2.proxy.presence.PresenceConfirmationProxyService
import spock.lang.Specification

class ProxyControllerSpec extends Specification {
    def sampleProxyToken = "sampleProxyToken"
    def presenceConfirmationProxyService = Mock(PresenceConfirmationProxyService)
    def proxyController = new ProxyController(sampleProxyToken, presenceConfirmationProxyService)

    def "confirmPresence() WHEN proxy input contains invalid token SHOULD throw an exception"() {
        given:
        def invalidToken = "invalidToken"
        def presenceConfirmationProxyInput = new PresenceConfirmationProxyInput(invalidToken, "", "")

        when:
        proxyController.confirmPresence(presenceConfirmationProxyInput)

        then:
        def ex = thrown(ProxyException)
        ex.getMessage() == ProxyException.invalidToken(invalidToken).getMessage()
    }
}
