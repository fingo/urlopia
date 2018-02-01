package info.fingo.urlopia.config.persistance.filter;

import info.fingo.urlopia.UrlopiaApplication;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Operator {

    EQUAL(":") {
        @Override
        public <T extends Comparable<? super T>> Predicate generatePredicate(
                CriteriaBuilder cb, Path<T> parameter, T value) {
            return cb.equal(parameter, value);
        }
    },

    LIKE(".:") {
        @Override
        public <T extends Comparable<? super T>> Predicate generatePredicate(
                CriteriaBuilder cb, Path<T> parameter, T value) {
            if (!(value instanceof String)) {
                throw new UnsupportedOperationException(String.format(
                        "Type `%s` for `LIKE` operation is not supported.", value.getClass().getCanonicalName()));
            }
            String valuePattern = String.format("%%%s%%", value);
            return cb.like(
                    cb.lower((Path<String>) parameter),
                    valuePattern.toLowerCase()
            );
        }
    },

    GREATER_OR_EQUAL(">:") {
        @Override
        public <T extends Comparable<? super T>> Predicate generatePredicate(
                CriteriaBuilder cb, Path<T> parameter, T value) {
            return cb.greaterThanOrEqualTo(parameter, value);
        }
    },

    LESS_OR_EQUAL("<:") {
        @Override
        public <T extends Comparable<? super T>> Predicate generatePredicate(
                CriteriaBuilder cb, Path<T> parameter, T value) {
            return cb.lessThanOrEqualTo(parameter, value);
        }
    };

    private final String sign;

    Operator(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    abstract public <T extends Comparable<? super T>> Predicate generatePredicate(
            CriteriaBuilder cb, Path<T> parameter,T value);

    public <E> Specification<E> generateSpecification(String key, String value) {
        return (root, query, cb) -> {
            Path<?> parameter = this.getParameterPath(root, key);

            Object type = parameter.getJavaType();
            if (type == String.class) {
                Path<String> stringParameter = (Path<String>) parameter;
                return this.generatePredicate(cb, stringParameter, value);
            } else if (type == LocalDate.class) {
                Path<LocalDate> dateParameter =  (Path<LocalDate>) parameter;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_FORMAT);
                LocalDate dateValue = LocalDate.parse(value, formatter);
                return this.generatePredicate(cb, dateParameter, dateValue);
            } else if (type == LocalDateTime.class) {
                Path<LocalDateTime> dateTimeParameter =  (Path<LocalDateTime>) parameter;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_TIME_FORMAT);
                LocalDateTime dateTimeValue = LocalDateTime.parse(value, formatter);
                return this.generatePredicate(cb, dateTimeParameter, dateTimeValue);
            } else if (type == Boolean.class) {
                Path<Boolean> booleanParameter = (Path<Boolean>) parameter;
                Boolean booleanValue = Boolean.valueOf(value);
                return this.generatePredicate(cb, booleanParameter, booleanValue);
            } else if (type == Long.class) {
                Path<Long> longParameter = (Path<Long>) parameter;
                Long longValue = Long.valueOf(value);
                return this.generatePredicate(cb, longParameter, longValue);
            }

            throw new UnsupportedOperationException(String.format("Type `%s` is not supported",
                    ((Class) type).getCanonicalName()));
        };
    }

    private <E> Path<?> getParameterPath(Root<E> root, String key) {
        String[] parameters = key.split("\\.");
        Path<?> path = root.get(parameters[0]);
        for (int i = 1; i < parameters.length; i++) {
            path = path.get(parameters[i]);
        }
        return path;
    }

    // STATIC CONTENT

    private static final List<String> signs = Arrays.stream(Operator.values())
            .map(Operator::getSign)
            .collect(Collectors.toList());

    public static Optional<Operator> fromSign(String sign) {
        return Arrays.stream(Operator.values())
                .filter(operation -> operation.getSign().equals(sign))
                .findFirst();
    }

    public static List<String> signs() {
        return signs;
    }
}
