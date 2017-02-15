package info.fingo.urlopia.mail;

import info.fingo.urlopia.ad.ActiveDirectory;
import info.fingo.urlopia.ad.LocalUser;
import info.fingo.urlopia.request.Acceptance;
import info.fingo.urlopia.request.AcceptanceService;
import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Jakub Licznerski
 *         Created on 15.02.2017.
 */
public class MailDeciderTest {

    private MailDecider mailDecider;
    private ActiveDirectory activeDirectory;
    private UserService userService;
    private AcceptanceService acceptanceService;

    @Before
    public void init() {

        activeDirectory = mock(ActiveDirectory.class);
        userService = mock(UserService.class);
        acceptanceService = mock(AcceptanceService.class);

        LocalUser localUser = new LocalUser();
        localUser.setPrincipalName("test@mail.com");
        localUser.setMail("test@mail.com");
        localUser.setTeams(new LinkedList<>());

        when(activeDirectory.getUser(anyString())).thenReturn(Optional.of(localUser));

        UserDTO userDTO = new UserDTO(1L, "test@mail.com");
        when(userService.getUser(anyString())).thenReturn(userDTO);

        when(acceptanceService.accept(anyLong(), anyLong())).thenThrow(RequestAccepted.class);
        when(acceptanceService.reject(anyLong(), anyLong())).thenThrow(RequestRejected.class);

        mailDecider = new MailDecider();
        mailDecider.userService = userService;
        mailDecider.activeDirectory = activeDirectory;
        mailDecider.mailParser = new MailParser();
        mailDecider.acceptanceService = acceptanceService;
    }


    @Test(expected = RequestAccepted.class)
    public void resolveAcceptTest() {
        Mail mail = new Mail("test@mail.com","Tester", "urlopia@fingo.info", "Urlopia", "RE: Wniosek [5]", "OK");

        mailDecider.resolve(mail);
    }

    @Test(expected = RequestRejected.class)
    public void resolveRejectTest() {
        Mail mail = new Mail("test@mail.com","Tester", "urlopia@fingo.info", "Urlopia", "RE: Wniosek [5]", "Nein");

        mailDecider.resolve(mail);
    }


    private class RequestAccepted extends Exception {}
    private class RequestRejected extends Exception {}

}