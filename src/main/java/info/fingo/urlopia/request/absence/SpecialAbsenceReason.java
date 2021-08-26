package info.fingo.urlopia.request.absence;

public enum SpecialAbsenceReason {
    ADDITIONAL_CARE_ALLOWANCE_PANDEMIC("Dodatkowy zasiłek opiekuńczy podczas pandemii"),
    BLOOD_DONATION_PANDEMIC("Oddawanie krwi podczas pandemii"),
    BLOOD_DONATION("Oddawanie krwi"),
    DELEGATION("Delegacja"),
    UNPAID_LEAVE("Urlop bezpłatny"),
    PARENTAL_LEAVE("Urlop rodzicielski"),
    MATERNITY_LEAVE("Urlop macierzyński"),
    PATERNITY_LEAVE("Urlop ojcowski"),
    SICK_LEAVE_EMPLOYEE("Zwolnienie lekarskie na pracownika"),
    SICK_LEAVE_CHILD("Zwolnienie lekarskie na dziecko"),
    SICK_LEAVE_FAMILY("Zwolnienie lekarskie na członka rodziny"),
    UNEXCUSED("Nieobecność nieusprawiedliwiona"),
    CHILDCARE("Urlop wychowawczy"),
    CHILDCARE_FOR_14_YEARS_OLD("Opieka nad dzieckiem do lat 14"),
    WRONG("Nieznany powód nieobecności"),
    OTHER("Inny powód nieobecności");

    private final String translatedReason;

    SpecialAbsenceReason(String translatedReason) {
        this.translatedReason = translatedReason;
    }

    public String getTranslatedReason() {
        return translatedReason;
    }
}

