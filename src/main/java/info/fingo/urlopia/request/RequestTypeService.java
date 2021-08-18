package info.fingo.urlopia.request;

import info.fingo.urlopia.request.absence.BaseRequestInput;

public interface RequestTypeService {

    Request create(Long userId, BaseRequestInput requestInput);

    void accept(Request request);

    void reject(Request request);

    void cancel(Request request);

}
