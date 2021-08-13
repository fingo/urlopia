export const mapCodeToMessage = (code) => {
    switch(true) {
        case code === "INCORRECT_PASSWORD_OR_EMAIL":
            code = "Błędny email lub hasło";
            break;
        case code.toString().startsWith("THERE_IS_NO_USER_WITH"):
            code = "Nie znaleziono użytkownika";
            break;
        case code === "NO_RESPONSE":
            code = "Brak odpowiedzi";
            break;
        case code === "HOLIDAYS_ARE_NOT_IN_SPECIFIED_TIME_PERIOD":
            code = "Nie możesz zapisać święta jeśli odbywa się ono poza podanym przez ciebie przedziałem czasowym"
            break;
        default:
            code = "Wystąpił nieoczekiwany błąd";
    }
    return code;
}