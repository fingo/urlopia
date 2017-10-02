package info.fingo.urlopia.request.normal.events;

import info.fingo.urlopia.request.Request;

public class NormalRequestCanceled {

    private Request request;

    public NormalRequestCanceled(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
