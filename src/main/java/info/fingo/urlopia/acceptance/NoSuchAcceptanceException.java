package info.fingo.urlopia.acceptance;

public class NoSuchAcceptanceException extends RuntimeException{

    private static final String ERROR_MESSAGE = "There is no acceptance with %s: %s";

    private NoSuchAcceptanceException(String errorMessage) {
        super(errorMessage);
    }

    public static NoSuchAcceptanceException invalidId(Long id) {
        return new NoSuchAcceptanceException(ERROR_MESSAGE.formatted("id", id.toString()));
    }
}
