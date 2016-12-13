package info.fingo.urlopia.request;

import info.fingo.urlopia.history.HistoryService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserFactory;
import info.fingo.urlopia.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * @author Tomasz Urbas
 */
@RunWith(MockitoJUnitRunner.class)
public class AcceptanceServiceTest {

    @InjectMocks
    AcceptanceService acceptanceService;

    @Mock
    UserRepository userRepository;
    @Mock
    AcceptanceRepository acceptanceRepository;
    @Mock
    RequestService requestService;
    @Mock
    HistoryService historyService;
    @Mock
    AcceptanceFactory acceptanceFactory;
    @Mock
    UserFactory userFactory;
    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @Before
    public void setup() {
        // Users
        User user_admin = new User("user@example.com");
        ReflectionTestUtils.setField(user_admin, "id", 10L);
        user_admin.setAdmin(true);

        User user_leader = new User("user2@example.com");
        ReflectionTestUtils.setField(user_leader, "id", 11L);

        User user_worker = new User("user3@example.com");
        ReflectionTestUtils.setField(user_worker, "id", 12L);

        // Requests
        Request request = new Request(user_worker, null, null, null, null, null);

        // Acceptances
        Acceptance acceptance = new Acceptance(request, user_leader);
        ReflectionTestUtils.setField(acceptance, "id", 10L);

        Acceptance acceptance2 = new Acceptance(request, user_leader);
        ReflectionTestUtils.setField(acceptance2, "id", 11L);
        acceptance2.setDecider(user_leader);

        // Acceptance Repository
        when(acceptanceRepository.findOne(anyLong()))
                .thenReturn(null);
        when(acceptanceRepository.findOne(eq(10L)))
                .thenReturn(acceptance);
        when(acceptanceRepository.findOne(eq(11L)))
                .thenReturn(acceptance2);

        when(acceptanceRepository.findByRequestId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(acceptanceRepository.findByRequestId(eq(10L)))
                .thenReturn(Arrays.asList(acceptance, acceptance2));

        when(acceptanceRepository.findByLeaderId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(acceptanceRepository.findByLeaderId(eq(10L)))
                .thenReturn(Arrays.asList(acceptance, acceptance2));

        when(acceptanceRepository.countByLeaderIdAndRequestModifiedAfter(anyLong(), any(LocalDateTime.class)))
                .thenReturn(0);
        when(acceptanceRepository.countByLeaderIdAndRequestModifiedAfter(anyLong(), eq(LocalDateTime.of(2016, 10, 1, 10, 10))))
                .thenReturn(1);

        // Acceptance Factory
        when(acceptanceFactory.create(anyObject()))
                .thenReturn(null);
        when(acceptanceFactory.create(eq(acceptance)))
                .thenReturn(new AcceptanceDTO(acceptance.getId(), null, null));
        when(acceptanceFactory.create(eq(acceptance2)))
                .thenReturn(new AcceptanceDTO(acceptance2.getId(), null, null));

        // User Repository
        when(userRepository.findOne(anyLong()))
                .thenReturn(null);
        when(userRepository.findOne(eq(10L)))
                .thenReturn(user_admin);
        when(userRepository.findOne(eq(11L)))
                .thenReturn(user_leader);
        when(userRepository.findOne(eq(12L)))
                .thenReturn(user_worker);

        // User Factory
        when(userFactory.create(anyObject()))
                .thenReturn(null);
        when(userFactory.create(eq(user_worker)))
                .thenReturn(new UserDTO(user_worker.getId(), null));
    }

    @Test
    public void getAcceptanceTest() {
        AcceptanceDTO acceptanceDTO = acceptanceService.getAcceptance(10);
        assertEquals(10L, acceptanceDTO.getId());
    }

    @Test
    public void getAcceptancesFromRequestTest() {
        List<AcceptanceDTO> requestAcceptances = acceptanceService.getAcceptancesFromRequest(10);
        assertEquals(2, requestAcceptances.size());
        assertEquals(10L, requestAcceptances.get(0).getId());
        assertEquals(11L, requestAcceptances.get(1).getId());
    }

    @Test
    public void getAcceptancesFromLeader() {
        List<AcceptanceDTO> leaderAcceptances = acceptanceService.getAcceptancesFromLeader(10);
        assertEquals(2, leaderAcceptances.size());
        assertEquals(10L, leaderAcceptances.get(0).getId());
        assertEquals(11L, leaderAcceptances.get(1).getId());

        leaderAcceptances = acceptanceService.getAcceptancesFromLeader(10, LocalDateTime.of(2016, 10, 1, 10, 10));
        assertEquals(2, leaderAcceptances.size());
        assertEquals(10L, leaderAcceptances.get(0).getId());
        assertEquals(11L, leaderAcceptances.get(1).getId());

        leaderAcceptances = acceptanceService.getAcceptancesFromLeader(10, LocalDateTime.of(2016, 10, 1, 10, 20));
        assertEquals(0, leaderAcceptances.size());
    }

    @Test
    public void acceptTest() {
        // accepted by admin
        boolean accepted = acceptanceService.accept(10L, 10L);
        assertTrue(accepted);
        assertEquals(userRepository.findOne(10L), acceptanceRepository.findOne(10L).getDecider());

        // accepted by leader
        setup();
        accepted = acceptanceService.accept(10L, 11L);
        assertTrue(accepted);
        assertEquals(userRepository.findOne(11L), acceptanceRepository.findOne(10L).getDecider());

        // accepted by requester
        setup();
        accepted = acceptanceService.accept(10L, 12L);
        assertFalse(accepted);

        // acceptance already is accepted or rejected
        setup();
        accepted = acceptanceService.accept(11L, 10L);
        assertFalse(accepted);
    }

    @Test
    public void rejectTest() {
        // rejected by admin
        boolean rejected = acceptanceService.reject(10L, 10L);
        assertTrue(rejected);
        assertEquals(userRepository.findOne(10L), acceptanceRepository.findOne(10L).getDecider());

        // rejected by leader
        setup();
        rejected = acceptanceService.reject(10L, 11L);
        assertTrue(rejected);
        assertEquals(userRepository.findOne(11L), acceptanceRepository.findOne(10L).getDecider());

        // rejected by requester
        setup();
        rejected = acceptanceService.reject(10L, 12L);
        assertTrue(rejected);
        assertEquals(userRepository.findOne(12L), acceptanceRepository.findOne(10L).getDecider());

        // acceptance already is accepted or rejected
        setup();
        rejected = acceptanceService.reject(11L, 10L);
        assertFalse(rejected);
    }

    @Test
    public void insertTest() {
        ArgumentCaptor<Acceptance> acceptanceCaptor = ArgumentCaptor.forClass(Acceptance.class);
        when(acceptanceRepository.save(acceptanceCaptor.capture())).thenReturn(new Acceptance());

        Request request = new Request();
        User user = new User("insert@example.com");
        acceptanceService.insert(request, user);

        assertEquals(request, acceptanceCaptor.getValue().getRequest());
        assertEquals(user, acceptanceCaptor.getValue().getLeader());
    }
}