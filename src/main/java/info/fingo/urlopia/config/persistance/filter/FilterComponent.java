package info.fingo.urlopia.config.persistance.filter;

public class FilterComponent {

    private String key;

    private Operator operator;

    private String value;

    public FilterComponent(String key, Operator operator, String value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }


}