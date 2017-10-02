package info.fingo.urlopia.request.occasional.events;

import info.fingo.urlopia.request.Request;

public class OccasionalRequestCreated {

    private Request request;

    public OccasionalRequestCreated(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
