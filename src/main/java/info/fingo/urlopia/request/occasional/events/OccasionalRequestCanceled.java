package info.fingo.urlopia.request.occasional.events;

import info.fingo.urlopia.request.Request;

public class OccasionalRequestCanceled {

    private Request request;

    public OccasionalRequestCanceled(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
