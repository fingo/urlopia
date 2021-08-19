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
        public <T extends Comparable<? super T>> Predicate generatePredicate(CriteriaBuilder cb,
                                                                             Path<T> parameter,
                                                                             T value) {
            return cb.equal(parameter, value);
        }
    },

    LIKE(".:") {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends Comparable<? super T>> Predicate generatePredicate(CriteriaBuilder cb,
                                                                             Path<T> parameter,
                                                                             T value) {
            if (!(value instanceof String)) {
                String format = "Type `%s` for `LIKE` operation is not supported.";
                String valueClassName = value.getClass().getCanonicalName();
                String exceptionMessage = String.format(format, valueClassName);
                throw new UnsupportedOperationException(exceptionMessage);
            }

            String valuePattern = String.format("%%%s%%", value).toLowerCase();
            return cb.like(cb.lower((Path<String>) parameter), valuePattern);
        }
    },

    GREATER_OR_EQUAL(">:") {
        @Override
        public <T extends Comparable<? super T>> Predicate generatePredicate(CriteriaBuilder cb,
                                                                             Path<T> parameter,
                                                                             T value) {
            return cb.greaterThanOrEqualTo(parameter, value);
        }
    },

    LESS_OR_EQUAL("<:") {
        @Override
        public <T extends Comparable<? super T>> Predicate generatePredicate(CriteriaBuilder cb,
                                                                             Path<T> parameter,
                                                                             T value) {
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

    abstract public <T extends Comparable<? super T>> Predicate generatePredicate(CriteriaBuilder cb,
                                                                                  Path<T> parameter,
                                                                                  T value);

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <E> Specification<E> generateSpecification(String key, String value) {
        return (root, query, cb) -> {
            Path<?> parameter = this.getParameterPath(root, key);
            Class<?> type = parameter.getJavaType();

            if (type == String.class) {
                var stringParameter = (Path<String>) parameter;
                return this.generatePredicate(cb, stringParameter, value);
            } else if (type == LocalDate.class) {
                var dateParameter =  (Path<LocalDate>) parameter;
                var formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_FORMAT);
                var dateValue = LocalDate.parse(value, formatter);
                return this.generatePredicate(cb, dateParameter, dateValue);
            } else if (type == LocalDateTime.class) {
                var dateTimeParameter =  (Path<LocalDateTime>) parameter;
                var formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_TIME_FORMAT);
                var dateTimeValue = LocalDateTime.parse(value, formatter);
                return this.generatePredicate(cb, dateTimeParameter, dateTimeValue);
            } else if (type == Boolean.class) {
                var booleanParameter = (Path<Boolean>) parameter;
                var booleanValue = Boolean.valueOf(value);
                return this.generatePredicate(cb, booleanParameter, booleanValue);
            } else if (type == Long.class) {
                var longParameter = (Path<Long>) parameter;
                var longValue = Long.valueOf(value);
                return this.generatePredicate(cb, longParameter, longValue);
            } else if (type.isEnum()) {
                for (var enumConstant : type.getEnumConstants()) {
                    if (enumConstant.toString().equalsIgnoreCase(value)) {
                        var enumParameter = (Path<Enum>) parameter;
                        var enumValue = Enum.valueOf((Class) type, value);
                        return this.generatePredicate(cb, enumParameter, enumValue);
                    }
                }
            }

            String exceptionMessage = String.format("Type `%s` is not supported", type.getCanonicalName());
            throw new UnsupportedOperationException(exceptionMessage);
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

    private static final List<String> signs;

    static {
        signs = Arrays.stream(Operator.values())
                .map(Operator::getSign)
                .collect(Collectors.toList());
    }

    public static List<String> signs() {
        return signs;
    }

    public static Optional<Operator> fromSign(String sign) {
        return Arrays.stream(Operator.values())
                .filter(operation -> operation.getSign().equals(sign))
                .findFirst();
    }
}
