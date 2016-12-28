package info.fingo.urlopia.history;

import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.*;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomasz Pilarczyk
 */
@Service
@Transactional
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private HistoryFactory historyFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private HolidayService holidayService;

    public void insert(RequestDTO requestDTO) {
        float hours = -DurationCalculator.calculate(requestDTO, holidayService);
        Request request = requestRepository.findOne(requestDTO.getId());
        historyRepository.save(new History(request, hours));
    }

    public void insertReversed(RequestDTO requestDTO) {
        float hours = DurationCalculator.calculate(requestDTO, holidayService);
        Request request = requestRepository.findOne(requestDTO.getId());
        historyRepository.save(new History(request, hours));
    }

    public void insertWithComment(RequestDTO requestDTO, String comment) {
        float hours = -DurationCalculator.calculate(requestDTO, holidayService);
        Request request = requestRepository.findOne(requestDTO.getId());
        historyRepository.save(new History(request, hours, comment));
    }

    public void insertWithComment(RequestDTO requestDTO, String comment, UserDTO deciderDTO) {
        float hours = -DurationCalculator.calculate(requestDTO, holidayService);
        Request request = requestRepository.findOne(requestDTO.getId());
        User decider = userRepository.findOne(deciderDTO.getId());
        historyRepository.save(new History(request, hours, comment, decider));
    }

    public void insertReversedWithComment(RequestDTO requestDTO, String comment) {
        float hours = DurationCalculator.calculate(requestDTO, holidayService);
        Request request = requestRepository.findOne(requestDTO.getId());
        historyRepository.save(new History(request, hours, comment));
    }

    public void addHistory(long userId, long deciderId, float hours, String comment) {
        User user = userRepository.findOne(userId);
        User decider = userRepository.findOne(deciderId);
        historyRepository.save(new History(user, decider, hours, comment));
    }

    public void addHistory(long userId, float hours, String comment) {
        User user = userRepository.findOne(userId);
        historyRepository.save(new History(user, null, hours, comment));
    }

    public List<HistoryDTO> getRecentUserHistories(String userMail) {
        List<History> histories = historyRepository.findFirst5ByUserMailOrderByCreatedDesc(userMail);

        return histories.stream()
                .map(historyFactory::create)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public List<HistoryDTO> getUserHistories(long userId) {
        List<History> histories = historyRepository.findByUserId(userId);

        return histories.stream()
                .map(historyFactory::create)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public List<HistoryDTO> getUserHistoriesFromYear(long userId, int year) {
        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);  //January first of actual year
        LocalDateTime endDate = LocalDateTime.of(year + 1, 1, 1, 0, 0);

        List<History> histories = historyRepository.findByUserIdAndCreatedBetween(userId, startDate, endDate);

        return histories.stream()
                .map(history -> historyFactory.create(history))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public History getHistoryById(long id) {
        return historyRepository.findOne(id);
    }

    /* add holiday pool every 1st of January, one second after midnight */
    @Scheduled(cron = "1 0 0 1 1 *")
    public void grantHolidaysPool() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            addHistory(user.getId(),
                    getHolidaysPoolFromYear(user.getId(), null, LocalDate.now().getYear() - 1),
                    "Przepisanie puli dni z poprzedniego roku");
        }
    }

    public float getHolidaysPool(Long userId, String mail) {
        return getHolidaysPoolFromYear(userId, mail, LocalDate.now().getYear());
    }

    private float getHolidaysPoolFromYear(Long userId, String mail, int year) {
        if (userId == null) {
            userId = userRepository.findFirstByMail(mail).getId();
        }
        List<HistoryDTO> histories = getUserHistoriesFromYear(userId, year);
        float pool = 0;
        for (HistoryDTO history : histories) {
            if (history.getRequest() != null && history.getRequest().getType() == Request.Type.OCCASIONAL) {
                continue;
            }
            pool += history.getHours();
        }
        return pool;
    }
}
