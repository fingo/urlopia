package info.fingo.urlopia.api.v2.user;

import info.fingo.urlopia.config.authentication.UserIdInterceptor;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.history.HistoryLogInput;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.request.normal.NormalRequestService;
import info.fingo.urlopia.user.UserExcerptProjection;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping(path = "/api/v2/users")
@RequiredArgsConstructor
public class UserControllerV2 {
   private final UserService userService;
   private final NormalRequestService normalRequestService;
   private final HistoryLogService historyLogService;



   @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
   @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
   public List<UserOutput> getAll(
           @RequestParam(name = "filter", defaultValue = "") String[] filters,
           Sort sort) {
      var filter = Filter.from(filters);
      var users = userService.get(filter, sort);
      return mapUserProjectionListToUserOutputList(users);
   }

   @RolesAllowed("ROLES_WORKER")
   @GetMapping(path = "/me/pending-days", produces = MediaType.APPLICATION_JSON_VALUE)
   public PendingDaysOutput getPendingDays(HttpServletRequest httpRequest) {
      var authenticatedId = (Long) httpRequest.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);
      return normalRequestService.getPendingRequestsTimeV2(authenticatedId);
   }

   @RolesAllowed("ROLES_WORKER")
   @GetMapping(value = "/me/vacation-days", produces = MediaType.APPLICATION_JSON_VALUE)
   public VacationDaysOutput getRemainingDays(HttpServletRequest httpRequest) {
      var authenticatedId = (Long) httpRequest.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);
      var remainingDaysInfo = historyLogService.countRemainingDays(authenticatedId);
      return VacationDaysOutput.fromWorkTimeResponse(remainingDaysInfo);
   }

   @RolesAllowed("ROLES_ADMIN")
   @PutMapping("/{userId}/work-time")
   public WorkTimeOutput setWorkTime(@PathVariable Long userId,
                                           @RequestBody WorkTimeInput workTimeInput) {
      userService.setWorkTime(userId, workTimeInput.value());
      return getWorkTime(userId);
   }

   @RolesAllowed("ROLES_ADMIN")
   @GetMapping("/{userId}/work-time")
   public WorkTimeOutput getWorkTime(@PathVariable Long userId){
      var remainingDaysInfo = historyLogService.countRemainingDays(userId);
      return WorkTimeOutput.fromWorkTimeResponse(remainingDaysInfo);
   }

   @RolesAllowed("ROLES_ADMIN")
   @GetMapping("/{userId}/vacation-days")
   public VacationDaysOutput getVacationDays(@PathVariable Long userId){
      var remainingDaysInfo = historyLogService.countRemainingDays(userId);
      return VacationDaysOutput.fromWorkTimeResponse(remainingDaysInfo);
   }

   @RolesAllowed("ROLES_ADMIN")
   @PutMapping(value = "/{userId}/vacation-days", consumes = MediaType.APPLICATION_JSON_VALUE)
   public VacationDaysOutput addVacationHours(@PathVariable Long userId,
                                              @RequestBody HistoryLogInput historyLog,
                                              HttpServletRequest httpRequest) {
      var authenticatedUserId = (Long) httpRequest.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);
      historyLogService.create(historyLog, userId, authenticatedUserId);
      return getVacationDays(userId);
   }


   private List<UserOutput>mapUserProjectionListToUserOutputList(List<UserExcerptProjection> users){
      return users.stream()
              .map(UserOutput::fromUserExcerptProjection)
              .toList();
   }


}