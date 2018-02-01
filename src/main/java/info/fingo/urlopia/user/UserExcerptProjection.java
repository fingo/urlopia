package info.fingo.urlopia.user;

import info.fingo.urlopia.team.TeamExcerptProjection;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

public interface UserExcerptProjection {

    Long getId();

    String getMail();

    String getFirstName();

    String getLastName();

    @Value("#{target.firstName} #{target.lastName}")
    String getName();

    Boolean getLeader();

    Boolean getB2b();

    Boolean getEc();

    Boolean getActive();

    Float getWorkTime();

    Set<TeamExcerptProjection> getTeams();

}
