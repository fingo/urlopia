package info.fingo.urlopia.history;

public class WorkTimeResponse {

    private float a;
    private float res;
    private float pool;
    private float workTime;
    private int days;
    private double hours;
    private int workTimeA;
    private int workTimeB;

    public WorkTimeResponse(float workTime,
                            float pool) {
        this.workTime = workTime;
        this.pool = pool;
        this.days = (int) Math.floor(pool / workTime);
        this.hours = Math.round((pool % workTime) * 100.0) / 100.0;
        countWorkTime();
        this.workTimeA = Math.round(res);
        this.workTimeB = Math.round(a);
    }

    public void countWorkTime() {
        var i = workTime / 8;
        a = 0;
        do {
            a++;
            res = i * a;
        } while (Math.floor(res) != res);
    }


    public float getPool() {
        return pool;
    }

    public void setPool(float pool) {
        this.pool = pool;
    }

    public float getWorkTime() {
        return workTime;
    }

    public void setWorkTime(float workTime) {
        this.workTime = workTime;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public int getWorkTimeA() {
        return workTimeA;
    }

    public void setWorkTimeA(int workTimeA) {
        this.workTimeA = workTimeA;
    }

    public int getWorkTimeB() {
        return workTimeB;
    }

    public void setWorkTimeB(int workTimeB) {
        this.workTimeB = workTimeB;
    }
}
