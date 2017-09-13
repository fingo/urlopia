package info.fingo.urlopia.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepositoryX extends JpaRepository<Request, Long> {

    Page<RequestExcerptProjection> findBy(Pageable pageable);

    Page<RequestExcerptProjection> findByRequesterId(Long requesterId, Pageable pageable);

    List<Request> findByRequesterId(Long requesterId);

}
