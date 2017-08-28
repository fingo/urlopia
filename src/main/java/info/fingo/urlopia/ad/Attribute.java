package info.fingo.urlopia.ad;

public enum Attribute {
    PRINCIPAL_NAME("userPrincipalName"),
    MAIL("mail"),
    FIRST_NAME("givenname"),
    LAST_NAME("sn"),
    MEMBER("member"),
    MEMBER_OF("memberOf"),
    MANAGED_OBJECTS("managedObjects"),
    MANAGED_BY("managedBy"),
    NAME("name"),
    DISTINGUISHED_NAME("distinguishedName"),
    CREATED_TIME("whenCreated"),
    CHANGED_TIME("whenChanged");

    private String key;

    Attribute(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
