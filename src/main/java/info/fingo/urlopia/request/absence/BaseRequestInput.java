package info.fingo.urlopia.request.absence;

import info.fingo.urlopia.request.RequestType;

import java.time.LocalDate;

public interface BaseRequestInput {


    RequestType getType();

    LocalDate getStartDate();

    LocalDate getEndDate();
}
