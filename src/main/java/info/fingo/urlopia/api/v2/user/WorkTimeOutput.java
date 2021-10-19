package info.fingo.urlopia.api.v2.user;

import info.fingo.urlopia.history.WorkTimeResponse;

public record WorkTimeOutput(int numerator,
                             int denominator) {

    public static WorkTimeOutput fromWorkTimeResponse(WorkTimeResponse workTimeResponse){
        return new WorkTimeOutput(workTimeResponse.getWorkTimeA(),
                workTimeResponse.getWorkTimeB());
    }

    public static WorkTimeOutput fromWorkTime(float workTime) {
        return fromWorkTimeResponse(new WorkTimeResponse(workTime, 0));
    }
}
