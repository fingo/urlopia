package info.fingo.urlopia.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<RequestExcerptProjection> findBy(Pageable pageable);

    Page<RequestExcerptProjection> findByRequesterId(Long requesterId, Pageable pageable);

    List<Request> findByRequesterId(Long requesterId);

    @Query("SELECT r FROM Request r " +
            "WHERE r.requester.id = :requesterId " +
                "AND (YEAR(r.startDate) = :year OR YEAR(r.endDate) = :year)")
    List<Request> findByRequesterIdAndYear(@Param("requesterId") Long requesterId, @Param("year") Integer year);

}
