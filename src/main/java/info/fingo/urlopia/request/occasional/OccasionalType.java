package info.fingo.urlopia.request.occasional;

import info.fingo.urlopia.request.Request;

public enum OccasionalType implements Request.TypeInfo {
    // TODO: Localize it!
    // TODO: Check days before write in database
    WRONG ("Niepoprawny typ okazjonalny", 0),
    D2_BIRTH ("Narodziny dziecka", 2),
    D2_FUNERAL ("Zgon/pogrzeb osoby z najbliższej rodziny", 2),
    D2_WEDDING ("Ślub", 2),
    D1_FUNERAL ("Zgon/pogrzeb osoby bliskiej", 1),
    D1_WEDDING ("Ślub dziecka", 1);

    private String info;
    private int durationDays;

    OccasionalType(String info, int durationDays) {
        this.info = info;
        this.durationDays = durationDays;
    }

    public String getName() {
        return this.name();
    }

    public String getInfo() {
        return info;
    }

    public int getDurationDays() {
        return durationDays;
    }
}
