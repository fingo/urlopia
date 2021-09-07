package info.fingo.urlopia.config.persistance.filter;

import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Filter {

    private final List<List<FilterComponent>> components;

    private Filter(List<List<FilterComponent>> components) {
        this.components = components;
    }

    public static Filter empty() {
        return new Filter(Collections.emptyList());
    }

    public Builder toBuilder() {
        return new Builder(components);
    }

    public <E> Specification<E> generateSpecification() {
        return this.components.stream()
                .map(this::<E>generateSpecificationFrom)
                .reduce(null, (spec1, spec2) -> Specification.where(spec1).and(spec2));

   }

   private <E> Specification<E>generateSpecificationFrom(List<FilterComponent> components){
        return components.stream()
                .map(component -> {
                    var operator = component.operator();
                    var key = component.key();
                    var value = component.value();
                    return operator.<E>generateSpecification(key, value);
                })
                .reduce(null, (spec1, spec2) -> Specification.where(spec1).or(spec2));
   }


    // STATIC CONTENT

    public static final String OR_OPERATOR = "|";

    public static final String SOFT_OR_OPERATOR = ",";

    private static final Pattern OPERATORS_PATTERN;

    static {
        var operatorsPattern = Operator.signs().stream()
                .sorted((s1, s2) -> Integer.compare(s2.length(), s1.length()))
                .map(Pattern::quote)
                .reduce((s1, s2) -> String.format("%s|%s", s1, s2))
                .orElse("");
        OPERATORS_PATTERN = Pattern.compile("^([\\w._]+?)(" + operatorsPattern + ")([\\w-_,: ąĄćĆęĘłŁńŃóÓśŚżŻźŹ]+?)$");
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
        var builder = Filter.newBuilder();
        Filter.splitIntoComponents(filter)
                .forEach(builder::or);
        return builder.build();
    }

    private static List<FilterComponent> splitIntoComponents(String filter) {
        List<FilterComponent> components = new LinkedList<>();
        var oneAttributeFilters = filter.split(Pattern.quote(OR_OPERATOR));

        for(var oneAttributeFilter : oneAttributeFilters) {
            var matcher = OPERATORS_PATTERN.matcher(oneAttributeFilter);
            if (!matcher.find()) {
                return Collections.emptyList();
            }

            var key = matcher.group(1);
            var operatorSign = matcher.group(2);
            var operator = Operator.fromSign(operatorSign);
            if (operator.isEmpty()) {
                return Collections.emptyList();
            }

            var values = matcher.group(3).split(Pattern.quote(SOFT_OR_OPERATOR));

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

        public void and(List<FilterComponent> components) {
            this.and();
            components.forEach(this::or);
        }

        public Builder and(String key,
                           Operator operator,
                           String value) {
            FilterComponent component = new FilterComponent(key, operator, value);
            this.and();
            return this.or(component);
        }

        public void and() {
            this.orComponents = new LinkedList<>();
            this.andComponents.add(this.orComponents);
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
