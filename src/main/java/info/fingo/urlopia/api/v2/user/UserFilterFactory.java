package info.fingo.urlopia.api.v2.user;

import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import org.springframework.stereotype.Component;

@Component
public class UserFilterFactory {

    public Filter getActiveECUsersFilter(){
        return Filter.newBuilder()
                .and("active", Operator.EQUAL, "true")
                .and("ec", Operator.EQUAL, "true")
                .and("b2b", Operator.EQUAL, "false")
                .build();
    }

    public Filter getInactiveECUsersFilter(){
        return Filter.newBuilder()
                .and("active", Operator.EQUAL, "false")
                .and("ec", Operator.EQUAL, "true")
                .and("b2b", Operator.EQUAL, "false")
                .build();
    }

    public Filter getActiveB2BUsersFilter(){
        return  Filter.newBuilder()
                .and("active", Operator.EQUAL, "true")
                .and("ec", Operator.EQUAL, "false")
                .and("b2b", Operator.EQUAL, "true")
                .build();
    }

}
