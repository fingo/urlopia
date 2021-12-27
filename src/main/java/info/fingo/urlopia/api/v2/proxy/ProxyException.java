package info.fingo.urlopia.api.v2.proxy;

public class ProxyException extends RuntimeException {
    private static final String INVALID_TOKEN_FMT = "Given proxy token '%s' is invalid";
    private static final String INVALID_TIME_RANGE_MSG = "Given time range is invalid";

    private ProxyException(String message) {
        super(message);
    }

    public static ProxyException invalidToken(String token) {
        return new ProxyException(INVALID_TOKEN_FMT.formatted(token));
    }

    public static ProxyException invalidTimeRange() {
        return new ProxyException(INVALID_TIME_RANGE_MSG);
    }
}
