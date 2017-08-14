package info.fingo.urlopia.mail;

import info.fingo.urlopia.events.OccasionalInfoEvent;
import info.fingo.urlopia.events.RequestAcceptedEvent;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component
public class EmailStorage {

    @Value("${mail.storage.vacations.ec}")
    private String ecVacationsStorage;

    @Autowired
    private EmailTemplateLoader emailTemplateLoader;

    @Autowired
    private EmailSender emailSender;

    @EventListener(value = RequestAcceptedEvent.class, condition = "#event.request.requester.EC")
    public void storeVacationsEC(RequestAcceptedEvent event) {
        RequestDTO request = event.getRequest();
        Mail mail = createNewVocationsMail(request);
        storeVocations(mail);
    }

    private Mail createNewVocationsMail(RequestDTO request) {
        EmailTemplate template = emailTemplateLoader.load("requestAccepted")
                .addProperty("requester", request.getRequester().getName())
                .addProperty("startDate", request.getStartDate().toString())
                .addProperty("endDate", request.getEndDate().toString());
        return new Mail(template);
    }

    @EventListener(value = OccasionalInfoEvent.class, condition = "#event.request.requester.EC")
    public void storeOccasionalVacationsEC(OccasionalInfoEvent event) {
        RequestDTO request = event.getRequest();
        Mail mail = createNewOccasionalVocationsMail(request);
        storeVocations(mail);
    }

    private Mail createNewOccasionalVocationsMail(RequestDTO request) {
        EmailTemplate template = emailTemplateLoader.load("occasionalInfo")
                .addProperty("requester", request.getRequester().getName())
                .addProperty("type", ((Request.OccasionalType) request.getTypeInfo()).getIndex())
                .addProperty("startDate", request.getStartDate())
                .addProperty("endDate", request.getEndDate());
        return new Mail(template);
    }

    private void storeVocations(Mail mail) {
        mail.setRecipientAddress(ecVacationsStorage);
        emailSender.send(mail);
    }
}
