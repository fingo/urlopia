package info.fingo.urlopia.config.persistance;

import info.fingo.urlopia.config.persistance.filter.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class BaseRepositoryClass<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements BaseRepository<T> {

    private final ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();

    public BaseRepositoryClass(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    public List<T> findAll(Filter filter) {
        Specification<T> spec = filter.generateSpecification();
        return super.findAll(spec);
    }

    @Override
    public Page<T> findAll(Filter filter, Pageable pageable) {
        Specification<T> spec = filter.generateSpecification();
        return super.findAll(spec, pageable);
    }

    @Override
    public <R> List<R> findAll(Filter filter, Class<R> projectionClass) {
        Specification<T> spec = filter.generateSpecification();
        return this.findAll(spec, projectionClass);
    }

    @Override
    public <R> List<R> findAll(Specification<T> spec, Class<R> projectionClass) {
        List<T> result = super.findAll(spec);
        return result.stream()
                .map(item -> projectionFactory.createProjection(projectionClass, item))
                .collect(Collectors.toList());
    }

    @Override
    public <P> List<P> findAll(Filter filter, Sort sort, Class<P> projectionClass) {
        Specification<T> spec = filter.generateSpecification();
        return this.findAll(spec, sort, projectionClass);
    }

    @Override
    public <P> List<P> findAll(Specification<T> spec, Sort sort, Class<P> projectionClass) {
        List<T> result = super.findAll(spec, sort);
        return result.stream()
                .map(item -> projectionFactory.createProjection(projectionClass, item))
                .collect(Collectors.toList());
    }

    @Override
    public <R> Page<R> findAll(Filter filter, Pageable pageable, Class<R> projectionClass) {
        Specification<T> spec = filter.generateSpecification();
        return this.findAll(spec, pageable, projectionClass);
    }

    @Override
    public <R> Page<R> findAll(Specification<T> spec, Pageable pageable, Class<R> projectionClass) {
        TypedQuery<T> query = super.getQuery(spec, pageable);
        return this.readPageWithProjection(spec, pageable, query, projectionClass);
    }

    private <R> Page<R> readPageWithProjection(Specification<T> spec, Pageable pageable,
                                               TypedQuery<T> query, Class<R> projectionType) {
        Page<T> result = (pageable == null)
                ? new PageImpl<>(query.getResultList())
                : super.readPage(query, super.getDomainClass(), pageable, spec);
        return result.map(item -> projectionFactory.createProjection(projectionType, item));
    }

}