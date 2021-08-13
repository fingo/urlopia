package info.fingo.urlopia.request;

public interface RequestTypeService {

    Request create(Long userId, RequestInput requestInput);

    void accept(Request request);

    void reject(Request request);

    void cancel(Request request);

}
