package info.fingo.urlopia.config.persistance;

import info.fingo.urlopia.config.persistance.filter.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T> extends JpaSpecificationExecutor<T> {

    List<T> findAll(Filter filter);

    Page<T> findAll(Filter filter, Pageable pageable);

    <P> List<P> findAll(Filter filter, Class<P> projectionClass);

    <P> List<P> findAll(Specification<T> spec, Class<P> projectionClass);

    <P> List<P> findAll(Filter filter, Sort sort, Class<P> projectionClass);

    <P> List<P> findAll(Specification<T> spec, Sort sort, Class<P> projectionClass);

    <P> Page<P> findAll(Filter filter, Pageable pageable, Class<P> projectionClass);

    <P> Page<P> findAll(Specification<T> spec, Pageable pageable, Class<P> projectionClass);

}