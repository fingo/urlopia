package info.fingo.urlopia.request.normal.events;

import info.fingo.urlopia.request.Request;

public class NormalRequestAccepted {

    private Request request;

    public NormalRequestAccepted(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
