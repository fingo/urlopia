package info.fingo.urlopia.request.normal.events;

import info.fingo.urlopia.request.Request;

public class NormalRequestRejected {

    private Request request;

    public NormalRequestRejected(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
