package info.fingo.urlopia.team;

import org.springframework.beans.factory.annotation.Value;

public interface TeamExcerptProjection {

    String getName();

    @Value("#{(target.leader != null) ? target.leader.id : ''}")
    String getLeaderId();

    @Value("#{(target.leader != null) ? target.leader.firstName : null}")
    String getLeaderFirstName();

    @Value("#{(target.leader != null) ? target.leader.lastName : null}")
    String getLeaderLastName();

    @Value("#{(target.leader != null) ? target.leader.mail : null}")
    String getLeaderMailAddress();

    @Value("#{(target.leader != null) ? target.leader.ec : null}")
    Boolean getLeaderEc();

    @Value("#{(target.leader != null) ? target.leader.b2b : null}")
    Boolean getLeaderB2B();

}
