package info.fingo.urlopia.reports;

public interface Report<E> {

    String templateName();

    String mimeType();

    String fileName(E model);

}
