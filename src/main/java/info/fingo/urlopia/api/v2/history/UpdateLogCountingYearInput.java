package info.fingo.urlopia.api.v2.history;

public record UpdateLogCountingYearInput(Long historyLogId,
                                         Boolean countForNextYear) {
}
