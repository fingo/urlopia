package info.fingo.urlopia.config.ad;

public enum ActiveDirectoryObjectClass {
    PERSON("person"),
    GROUP("group"),
    ORGANIZATIONAL_UNIT("organizationalUnit");

    private final String key;

    ActiveDirectoryObjectClass(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
