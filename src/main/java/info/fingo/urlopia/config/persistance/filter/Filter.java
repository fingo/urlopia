package info.fingo.urlopia.config.persistance.filter;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Filter {

    private List<List<FilterComponent>> components;

    private Filter(List<List<FilterComponent>> components) {
        this.components = components;
    }

    public Builder toBuilder() {
        return new Builder(components);
    }

    public <E> Specification<E> generateSpecification() {
        return this.components.stream()
                .map(andComponents -> andComponents.stream()
                        .map(component -> {
                            Operator operator = component.getOperator();
                            String key = component.getKey();
                            String value = component.getValue();
                            return operator.<E>generateSpecification(key, value);
                        })
                        .reduce(null, (spec1, spec2) -> Specifications.where(spec1).or(spec2)))
                .reduce(null, (spec1, spec2) -> Specifications.where(spec1).and(spec2));
    }

    // STATIC CONTENT

    public static String OR_OPERATOR = "|";

    public static String SOFT_OR_OPERATOR = ",";

    private static Pattern operatorsPattern;

    static {
        String operatorsPattern = Operator.signs().stream()
                .sorted((s1, s2) -> Integer.compare(s2.length(), s1.length()))
                .map(Pattern::quote)
                .reduce((s1, s2) -> String.format("%s|%s", s1, s2))
                .orElse("");
        Filter.operatorsPattern = Pattern.compile("^([\\w._]+?)(" + operatorsPattern + ")([\\w-_,: ąćęłńóśżź]+?)$");
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Filter from(String[] filters) {
        Builder builder = Filter.newBuilder();
        Arrays.stream(filters)
                .map(Filter::splitIntoComponents)
                .forEach(builder::and);
        return builder.build();
    }

    public static Filter from(String filter) {
        Builder builder = Filter.newBuilder();
        Filter.splitIntoComponents(filter)
                .forEach(builder::or);
        return builder.build();
    }

    private static List<FilterComponent> splitIntoComponents(String filter) {
        List<FilterComponent> components = new LinkedList<>();
        String[] oneAttributeFilters = filter.split(Pattern.quote(OR_OPERATOR));
        for(String oneAttributeFilter : oneAttributeFilters) {
            Matcher matcher = operatorsPattern.matcher(oneAttributeFilter);
            if (!matcher.find()) {
                return Collections.emptyList();
            }

            String key = matcher.group(1);
            String operatorSign = matcher.group(2);
            Optional<Operator> operator = Operator.fromSign(operatorSign);
            if (!operator.isPresent()) {
                return Collections.emptyList();
            }

            String[] values = matcher.group(3).split(Pattern.quote(SOFT_OR_OPERATOR));

            Arrays.stream(values)
                    .map(value -> new FilterComponent(key, operator.get(), value))
                    .forEach(components::add);
        }
        return components;
    }

    public static class Builder {

        private List<List<FilterComponent>> andComponents;

        private List<FilterComponent> orComponents;

        private Builder(List<List<FilterComponent>> andComponents) {
            this.andComponents = new LinkedList<>(andComponents);
            this.and();
        }

        private Builder() {
            this.andComponents = new LinkedList<>();
            this.and();
        }

        public Builder and(Filter filter) {
            List<List<FilterComponent>> components = filter.components;
            andComponents.addAll(components);
            return this.and();
        }


        public Builder and(List<FilterComponent> components) {
            this.and();
            components.forEach(this::or);
            return this;
        }

        public Builder and(String key, Operator operator, String value) {
            FilterComponent component = new FilterComponent(key, operator, value);
            this.and();
            return this.or(component);
        }

        public Builder and() {
            this.orComponents = new LinkedList<>();
            this.andComponents.add(this.orComponents);
            return this;
        }

        public Builder or(FilterComponent component) {
            orComponents.add(component);
            return this;
        }

        public Filter build() {
            List<List<FilterComponent>> components = andComponents.stream()
                    .filter(andComponent -> !andComponent.isEmpty())
                    .collect(Collectors.toList());
            return new Filter(components);
        }
    }

}
