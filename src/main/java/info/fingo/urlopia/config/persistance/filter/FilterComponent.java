package info.fingo.urlopia.config.persistance.filter;

public record FilterComponent(String key, 
                              Operator operator, 
                              String value) {}