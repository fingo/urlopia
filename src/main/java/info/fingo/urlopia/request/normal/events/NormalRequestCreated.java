package info.fingo.urlopia.request.normal.events;

import info.fingo.urlopia.request.Request;

public class NormalRequestCreated {

    private Request request;

    public NormalRequestCreated(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
