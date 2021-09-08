package info.fingo.urlopia.api.v2.reports.attendance;

import info.fingo.urlopia.user.User;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
public class AttendanceListPage {
    public static final int NUMBER_OF_USERS_ON_ONE_PAGE = 5;

    private final List<User> usersOnPage = new LinkedList<>();

    public void addUser(User user) {
        if (!isFull()) {
            usersOnPage.add(user);
        }
    }

    public boolean isFull() {
        return usersOnPage.size() == NUMBER_OF_USERS_ON_ONE_PAGE;
    }

    public boolean isEmpty() {
        return usersOnPage.isEmpty();
    }

    public List<User> getUsersOnPage() {
        return Collections.unmodifiableList(usersOnPage);
    }
}
