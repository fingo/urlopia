package info.fingo.urlopia.history;

import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.*;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserFactory;
import info.fingo.urlopia.user.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
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
public class HistoryServiceTest {

    @InjectMocks
    HistoryService historyService;

    @Mock
    UserRepository userRepository;
    @Mock
    RequestRepository requestRepository;
    @Mock
    HistoryRepository historyRepository;
    @Mock
    HolidayService holidayService;
    @Mock
    AcceptanceService acceptanceService;
    @Mock
    UserFactory userFactory;
    @Mock
    HistoryFactory historyFactory;

    @Test
    public void insertTest() {
        // TEST OBJECTS
        Request request = new Request(null, null, null, null);

        UserDTO requester = new UserDTO(0, null);
        requester.setWorkTime(8f);

        RequestDTO requestDTO = new RequestDTO(10, null, null, requester, LocalDate.now(), LocalDate.now(), null);

        // MOCKITO
        when(holidayService.getAllHolidaysDates()).thenReturn(Collections.emptyList());
        when(requestRepository.findOne(eq(10L))).thenReturn(request);

        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        when(historyRepository.save(historyCaptor.capture())).thenReturn(new History());

        // TESTS
        historyService.insert(requestDTO);
        assertEquals(request, historyCaptor.getValue().getRequest());
        assertTrue(historyCaptor.getValue().getHours() < 0);
    }

    @Test
    public void insertReversedTest() {
        // TEST OBJECTS
        Request request = new Request(null, null, null, null);

        UserDTO requester = new UserDTO(0, null);
        requester.setWorkTime(8f);

        RequestDTO requestDTO = new RequestDTO(10, null, null, requester, LocalDate.now(), LocalDate.now(), null);

        // MOCKITO
        when(holidayService.getAllHolidaysDates()).thenReturn(Collections.emptyList());
        when(requestRepository.findOne(eq(10L))).thenReturn(request);

        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        when(historyRepository.save(historyCaptor.capture())).thenReturn(new History());

        // TESTS
        historyService.insertReversed(requestDTO);
        assertEquals(request, historyCaptor.getValue().getRequest());
        assertTrue(historyCaptor.getValue().getHours() > 0);
    }

    @Test
    public void insertForOccasionalTest() {
        // TEST OBJECTS
        User user = new User("user@example.com");

        Request request = new Request(user, null, null, null);
        ReflectionTestUtils.setField(request, "id", 10L);

        Request request2 = new Request(user, null, null, null);
        ReflectionTestUtils.setField(request2, "id", 11L);

        Request request3 = new Request(user, null, null, null);
        ReflectionTestUtils.setField(request3, "id", 12L);

        UserDTO userDTO = new UserDTO(10L, "user@example.com");
        UserDTO userDTO2 = new UserDTO(11L, "user2@example.com");

        AcceptanceDTO acceptanceDto = new AcceptanceDTO(10L, null, userDTO, userDTO2, true);
        AcceptanceDTO acceptanceDto2 = new AcceptanceDTO(11L, null, userDTO, userDTO, false);
        AcceptanceDTO acceptanceDto3 = new AcceptanceDTO(12L, null, userDTO, userDTO2, true);

        // MOCKITO
        when(acceptanceService.getAcceptancesFromRequest(eq(10L))).thenReturn(Arrays.asList(acceptanceDto, acceptanceDto2));
        when(acceptanceService.getAcceptancesFromRequest(eq(11L))).thenReturn(Collections.singletonList(acceptanceDto));
        when(acceptanceService.getAcceptancesFromRequest(eq(12L))).thenReturn(Arrays.asList(acceptanceDto, acceptanceDto3));
        when(userFactory.create(eq(user))).thenReturn(userDTO);

        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        when(historyRepository.save(historyCaptor.capture())).thenReturn(new History());

        // TESTS
        // Two acceptances, both accepted
        historyService.insertForOccasional(request3, 8f, null, 1);
        assertEquals(request3, historyCaptor.getValue().getRequest());

        // Two acceptances, one is cancelled
        historyCaptor.getAllValues().clear();
        historyService.insertForOccasional(request, 8f, null, 1);
        assertTrue(historyCaptor.getAllValues().isEmpty());

        // One acceptance, which is accepted
        historyService.insertForOccasional(request2, 8f, null, 1);
        assertEquals(request2, historyCaptor.getValue().getRequest());

        // One acceptance, which is cancelled
        historyCaptor.getAllValues().clear();
        historyService.insertForOccasional(request, 8f, null, 1);
        assertTrue(historyCaptor.getAllValues().isEmpty());
    }

    @Test
    public void addHistoryTest() {
        // TEST OBJECTS
        User user = new User("user@example.com");
        ReflectionTestUtils.setField(user, "id", 10L);

        User user2 = new User("user2@example.com");
        ReflectionTestUtils.setField(user, "id", 11L);

        // MOCKITO
        when(userRepository.findOne(eq(10L)))
                .thenReturn(user);
        when(userRepository.findOne(eq(11L)))
                .thenReturn(user2);

        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        when(historyRepository.save(historyCaptor.capture())).thenReturn(new History());

        // TESTS
        historyService.addHistory(10L, 11L, 8f, "");
        assertEquals(user, historyCaptor.getValue().getUser());
        assertEquals(user2, historyCaptor.getValue().getDecider());

        historyService.addHistory(10L, 8f, "");
        assertEquals(user, historyCaptor.getValue().getUser());
        assertNull(historyCaptor.getValue().getDecider());
    }

    @Test
    public void getRecentHistoriesTest() {
        // TEST OBJECTS
        History history = new History();
        History history2 = new History();
        History history3 = new History();
        History history4 = new History();
        History history5 = new History();

        HistoryDTO historyDto = new HistoryDTO(0, 0, null, null, null, null, null, null, 0);
        HistoryDTO historyDto2 = new HistoryDTO(0, 0, null, null, null, null, null, null, 0);
        HistoryDTO historyDto3 = new HistoryDTO(0, 0, null, null, null, null, null, null, 0);
        HistoryDTO historyDto4 = new HistoryDTO(0, 0, null, null, null, null, null, null, 0);
        HistoryDTO historyDto5 = new HistoryDTO(0, 0, null, null, null, null, null, null, 0);

        // MOCKITO
        when(historyRepository.findFirst5ByUserMailOrderByCreatedDesc(anyString()))
                .thenReturn(Arrays.asList(history, history2, history3, history4, history5));
        when(historyFactory.create(history)).thenReturn(historyDto);
        when(historyFactory.create(history2)).thenReturn(historyDto2);
        when(historyFactory.create(history3)).thenReturn(historyDto3);
        when(historyFactory.create(history4)).thenReturn(historyDto4);
        when(historyFactory.create(history5)).thenReturn(historyDto5);

        // TESTS
        List<HistoryDTO> histories = historyService.getRecentHistories("mail@example.com");
        assertEquals(Arrays.asList(historyDto, historyDto2, historyDto3, historyDto4, historyDto5), histories);
    }

    @Test
    public void getHistoriesTest() {
        // TEST OBJECTS
        History history = new History();
        History history2 = new History();

        HistoryDTO historyDto = new HistoryDTO(0, 0, null, null, null, null, null, null, 0);
        HistoryDTO historyDto2 = new HistoryDTO(0, 0, null, null, null, null, null, null, 0);

        // MOCKITO
        when(historyRepository.findByUserId(anyLong()))
                .thenReturn(Arrays.asList(history, history2));
        when(historyFactory.create(history)).thenReturn(historyDto);
        when(historyFactory.create(history2)).thenReturn(historyDto2);

        // TESTS
        List<HistoryDTO> histories = historyService.getHistories(0L);
        assertEquals(Arrays.asList(historyDto, historyDto2), histories);
    }

    @Test
    public void getHistoriesFromYear() {
        // TEST OBJECTS
        History history = new History();
        History history2 = new History();

        HistoryDTO historyDto = new HistoryDTO(0, 0, null, null, null, null, null, null, 0);
        HistoryDTO historyDto2 = new HistoryDTO(0, 0, null, null, null, null, null, null, 0);

        // MOCKITO
        when(historyRepository.findByUserIdAndCreatedBetween(anyLong(), eq(LocalDateTime.of(2016,1,1,0,0)), eq(LocalDateTime.of(2017,1,1,0,0))))
                .thenReturn(Arrays.asList(history, history2));
        when(historyFactory.create(history)).thenReturn(historyDto);
        when(historyFactory.create(history2)).thenReturn(historyDto2);

        // TESTS
        List<HistoryDTO> histories = historyService.getHistoriesFromYear(0L, 2016);
        assertEquals(Arrays.asList(historyDto, historyDto2), histories);
    }

    @Test
    public void getHistoryByIdTest() {
        // TEST OBJECTS
        History history = new History();

        // MOCKITO
        when(historyRepository.findOne(0L)).thenReturn(history);

        // TESTS
        History myHistory = historyService.getHistoryById(0L);
        assertEquals(history, myHistory);
    }

    @Test
    public void grantHolidaysPoolTest() {
        // TEST OBJECTS
        User user = new User("");
        ReflectionTestUtils.setField(user, "id", 10L);

        User user2 = new User("");
        ReflectionTestUtils.setField(user2, "id", 11L);

        History history = new History();
        History history2 = new History();
        History history3 = new History();

        HistoryDTO historyDto = new HistoryDTO(0, 15, null, null, null, null, null, null, 0);
        HistoryDTO historyDto2 = new HistoryDTO(0, 5, null, null, null, null, null, null, 4);
        HistoryDTO historyDto3 = new HistoryDTO(0, 10, null, null, null, null, null, null, 0);

        // MOCKITO
        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));
        when(historyRepository.findByUserIdAndCreatedBetween(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(history, history2, history3));
        when(historyRepository.findByUserIdAndCreatedBetween(eq(11L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(history));
        when(historyFactory.create(history)).thenReturn(historyDto);
        when(historyFactory.create(history2)).thenReturn(historyDto2);
        when(historyFactory.create(history3)).thenReturn(historyDto3);

        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        when(historyRepository.save(historyCaptor.capture())).thenReturn(new History());

        //TESTS
        historyService.grantHolidaysPool();
        assertEquals(2, historyCaptor.getAllValues().size());
        assertEquals(25f, historyCaptor.getAllValues().get(0).getHours(), 10e-15);
        assertEquals(15f, historyCaptor.getAllValues().get(1).getHours(), 10e-15);
    }

    @Test
    public void getHolidaysPoolTest() {
        // TEST OBJECTS
        User user = new User("");
        ReflectionTestUtils.setField(user, "id", 10L);

        User user2 = new User("");
        ReflectionTestUtils.setField(user2, "id", 11L);

        History history = new History();
        History history2 = new History();

        HistoryDTO historyDto = new HistoryDTO(0, 15, null, null, null, null, null, null, 0);
        HistoryDTO historyDto2 = new HistoryDTO(0, 5, null, null, null, null, null, null, 4);

        // MOCKITO
        when(userRepository.findFirstByMail(anyString())).thenReturn(user2);
        when(historyRepository.findByUserIdAndCreatedBetween(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(history, history2));
        when(historyRepository.findByUserIdAndCreatedBetween(eq(11L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(history2));
        when(historyFactory.create(history)).thenReturn(historyDto);
        when(historyFactory.create(history2)).thenReturn(historyDto2);

        // TESTS
        // by id, usual history
        float pool = historyService.getHolidaysPool(10L, null);
        assertEquals(pool, 15, 10e-15);

        // by mail, unusual history
        pool = historyService.getHolidaysPool(null, "");
        assertEquals(pool, 0, 10e-15);
    }
}