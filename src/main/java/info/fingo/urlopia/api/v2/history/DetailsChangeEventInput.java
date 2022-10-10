package info.fingo.urlopia.api.v2.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import info.fingo.urlopia.history.UserDetailsChangeEvent;

import java.time.LocalDateTime;

public record DetailsChangeEventInput(@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime created,
                                      Long userId,
                                      UserDetailsChangeEvent event,
                                      float oldWorkTime,
                                      float newWorkTime) {
    public DetailsChangeEventInput(@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime created,
                                   Long userId,
                                   UserDetailsChangeEvent event){
        this(created, userId, event, 0, 0);
    }
}
