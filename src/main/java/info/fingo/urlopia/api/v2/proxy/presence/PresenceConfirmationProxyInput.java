package info.fingo.urlopia.api.v2.proxy.presence;

import info.fingo.urlopia.api.v2.proxy.ProxyInput;

public record PresenceConfirmationProxyInput(String token, String email, String hours) implements ProxyInput {

}
