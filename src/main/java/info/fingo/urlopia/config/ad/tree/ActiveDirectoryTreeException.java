package info.fingo.urlopia.config.ad.tree;

public class ActiveDirectoryTreeException extends RuntimeException {

    private static final String MISSING_PARENT = "Missing parent node for object %s";

    private ActiveDirectoryTreeException(String message) {
        super(message);
    }

    public static ActiveDirectoryTreeException missingParent(String child) {
        return new ActiveDirectoryTreeException(MISSING_PARENT.formatted(child));
    }

}
