package info.fingo.urlopia.team;

import org.springframework.beans.factory.annotation.Value;

public interface TeamExcerptProjection {

    String getName();

    @Value("#{(target.leader != null) ? target.leader.id : ''}")
    String getLeaderId();

}
