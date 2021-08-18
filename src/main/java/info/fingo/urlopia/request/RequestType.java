package info.fingo.urlopia.request;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;

public enum RequestType {

    NORMAL,
    OCCASIONAL,
    SPECIAL;

    private RequestTypeService service;

    public RequestTypeService getService() {
        return service;
    }

    public void setService(RequestTypeService service) {
        this.service = service;
    }

    @Component
    public static class RequestTypeServiceInjector {

        private final BeanFactory beanFactory;

        @Autowired
        public RequestTypeServiceInjector(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @PostConstruct
        public void postConstruct() {
            for (RequestType type : EnumSet.allOf(RequestType.class)) {
                String beanName = String.format("%sRequestService", type.name().toLowerCase());
                RequestTypeService service = (RequestTypeService) beanFactory.getBean(beanName);
                type.setService(service);
            }
        }
    }
}
