package info.fingo.urlopia.history;

import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestDTO;
import info.fingo.urlopia.request.RequestRepository;
import info.fingo.urlopia.request.acceptance.AcceptanceService;
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
        User requester = new User(null);
        Request request = new Request(requester, null, null, null, null, null);

        UserDTO requesterDTO = new UserDTO(0, null);
        requesterDTO.setWorkTime(8f);

        RequestDTO requestDTO = new RequestDTO(10, null, null, requesterDTO, LocalDate.now(), LocalDate.now(), null, null, Request.Status.ACCEPTED);

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
        User requester = new User(null);
        Request request = new Request(requester, null, null, null, null, null);

        UserDTO requesterDTO = new UserDTO(0, null);
        requesterDTO.setWorkTime(8f);

        RequestDTO requestDTO = new RequestDTO(10, null, null, requesterDTO, LocalDate.now(), LocalDate.now(), null, null, Request.Status.ACCEPTED);

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

        HistoryDTO historyDto = new HistoryDTO(0, 0, 0,  null, null, null, null, null, null);
        HistoryDTO historyDto2 = new HistoryDTO(0, 0, 0,  null, null, null, null, null, null);
        HistoryDTO historyDto3 = new HistoryDTO(0, 0, 0, null, null, null, null, null, null);
        HistoryDTO historyDto4 = new HistoryDTO(0, 0, 0, null, null, null, null, null, null);
        HistoryDTO historyDto5 = new HistoryDTO(0, 0, 0, null, null, null, null, null, null);

        // MOCKITO
        when(historyRepository.findFirst5ByUserMailOrderByCreatedDesc(anyString()))
                .thenReturn(Arrays.asList(history, history2, history3, history4, history5));
        when(historyFactory.create(history)).thenReturn(historyDto);
        when(historyFactory.create(history2)).thenReturn(historyDto2);
        when(historyFactory.create(history3)).thenReturn(historyDto3);
        when(historyFactory.create(history4)).thenReturn(historyDto4);
        when(historyFactory.create(history5)).thenReturn(historyDto5);

        // TESTS
        List<HistoryDTO> histories = historyService.getRecentUserHistories("mail@example.com");
        assertEquals(Arrays.asList(historyDto, historyDto2, historyDto3, historyDto4, historyDto5), histories);
    }

    @Test
    public void getHistoriesTest() {
        // TEST OBJECTS
        History history = new History();
        History history2 = new History();

        HistoryDTO historyDto = new HistoryDTO(0, 0, 0, null, null, null, null, null, null);
        HistoryDTO historyDto2 = new HistoryDTO(0, 0, 0, null, null, null, null, null, null);

        // MOCKITO
        when(historyRepository.findByUserId(anyLong()))
                .thenReturn(Arrays.asList(history, history2));
        when(historyFactory.create(history)).thenReturn(historyDto);
        when(historyFactory.create(history2)).thenReturn(historyDto2);

        // TESTS
        List<HistoryDTO> histories = historyService.getUserHistories(0L);
        assertEquals(Arrays.asList(historyDto, historyDto2), histories);
    }

    @Test
    public void getHistoriesFromYear() {
        // TEST OBJECTS
        History history = new History();
        History history2 = new History();

        HistoryDTO historyDto = new HistoryDTO(0, 0, 0, null, null, null, null, null, null);
        HistoryDTO historyDto2 = new HistoryDTO(0, 0, 0, null, null, null, null, null, null);

        // MOCKITO
        when(historyRepository.findByUserIdAndCreatedBetween(anyLong(), eq(LocalDateTime.of(2016,1,1,0,0)), eq(LocalDateTime.of(2017,1,1,0,0))))
                .thenReturn(Arrays.asList(history, history2));
        when(historyFactory.create(history)).thenReturn(historyDto);
        when(historyFactory.create(history2)).thenReturn(historyDto2);

        // TESTS
        List<HistoryDTO> histories = historyService.getUserHistoriesFromYear(0L, 2016);
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

        RequestDTO requestDTO = new RequestDTO(0, null, null, null, null, null, Request.Type.NORMAL, null, null);
        RequestDTO requestDTO2 = new RequestDTO(0, null, null, null, null, null, Request.Type.OCCASIONAL, null, null);

        HistoryDTO historyDto = new HistoryDTO(0, 15, 0, null, null, null, null, null, null);
        HistoryDTO historyDto2 = new HistoryDTO(0, 5, 0, null, null, requestDTO2, null, null, null);
        HistoryDTO historyDto3 = new HistoryDTO(0, 10, 0, null, null, requestDTO, null, null, null);

        // MOCKITO
        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));
        when(userRepository.findOne(eq(10L))).thenReturn(user);
        when(userRepository.findOne(eq(11L))).thenReturn(user2);
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

        RequestDTO requestDTO = new RequestDTO(0, null, null, null, null, null, Request.Type.NORMAL, null, null);
        RequestDTO requestDTO2 = new RequestDTO(0, null, null, null, null, null, Request.Type.OCCASIONAL, null, null);

        HistoryDTO historyDto = new HistoryDTO(0, 15, 0, null, null, requestDTO, null, null, null);
        HistoryDTO historyDto2 = new HistoryDTO(0, 5, 0, null, null, requestDTO2, null, null, null);

        // MOCKITO
        when(userRepository.findFirstByMail(anyString())).thenReturn(user2);
        when(historyRepository.findByUserIdAndCreatedBetween(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(history, history2));
        when(historyRepository.findByUserIdAndCreatedBetween(eq(11L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(history2));
        when(historyFactory.create(history)).thenReturn(historyDto);
        when(historyFactory.create(history2)).thenReturn(historyDto2);

        // TESTS
        // by id, normal and occasional history
        float pool = historyService.getHolidaysPool(10L, null);
        assertEquals(15, pool, 10e-15);

        // by mail, occasional history
        pool = historyService.getHolidaysPool(null, "");
        assertEquals(0, pool, 10e-15);
    }
}