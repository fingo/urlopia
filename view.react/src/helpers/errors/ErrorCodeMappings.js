const presenceConfirmationErrorCodeMappings = {
    "YOU_CANNOT_CONFIRM_A_PRESENCE_IN_A_DAY_YOU_ARE_ON_VACATION": "Nie możesz potwierdzić obecności w dniu, w którym jesteś na urlopie",
    "YOU_CANNOT_CONFIRM_A_PRESENCE_IN_A_NON_WORKING_DAY": "Nie możesz potwierdzić obecności w dniu nieroboczym",
    "YOU_CANNOT_CONFIRM_A_PRESENCE_IN_A_FUTURE_DATE": "Nie możesz potwierdzić swojej obecności w przyszłości",
    "YOU_CAN_ONLY_CONFIRM_A_PRESENCE_UP_TO_2_WEEKS_PAST": "Możesz potwierdzić obecność tylko dwa tygodnie wstecz",
    "YOU_CANNOT_CONFIRM_A_PRESENCE_IN_A_DAY_YOU_ARE_NOT_WORKING": "Nie możesz potwierdzić obecności w dniu w którym nie pracujesz",
}

const holidaysErrorCodeMappings = {
    "HOLIDAYS_ARE_NOT_IN_SPECIFIED_TIME_PERIOD": "Nie możesz zapisać święta jeśli odbywa się ono poza podanym przez ciebie przedziałem czasowym",
}

const loginErrorCodeMappings = {
    "INCORRECT_PASSWORD_OR_EMAIL": "Błędny email lub hasło",
}

const requestsErrorCodeMappings = {
    "END_DATE_IS_BEFORE_START_DATE": "Data końcowa nie powinna być przed datą początkową",
    "NOT_ENOUGH_DAYS": "Nie posiadasz wystarczającej liczby dni urlopowych",
    "LEADER_NOT_FOUND": "Nie została znaleziona osoba wymagana do akceptacji wniosku",
    "REQUEST_OVERLAPPING": "Wybrany termin pokrywa się z terminem jednej z wcześniej utworzonych okoliczności",
}

const reportsErrorCodeMappings = {
    "UNABLE_TO_GENERATE_EVIDENCE_REPORT_FOR_ALL_USERS": "Generowanie raportów nie powiodło się",
}

const historyLogsErrorCodeMappings = {
    "UNABLE_TO_DELETE_GIVEN_HISTORY_LOG": "Próba usunięcia wybranego wpisu w historii nie powiodła się",
}

export const ERROR_CODE_MAPPINGS = {
    "NO_RESPONSE": "Brak odpowiedzi",
    ...presenceConfirmationErrorCodeMappings,
    ...holidaysErrorCodeMappings,
    ...loginErrorCodeMappings,
    ...requestsErrorCodeMappings,
    ...reportsErrorCodeMappings,
    ...historyLogsErrorCodeMappings
}

export const ERROR_CODE_PREFIX_MAPPINGS = {
    "THERE_IS_NO_USER_WITH": "Nie znaleziono użytkownika",
    "THERE_IS_NO_ACCEPTANCE_WITH": "Nie znaleziono akceptacji wniosku",
    "UNABLE_TO_GENERATE_REPORT_WITH": "Generowanie wybranego raportu nie powiodło się",
}