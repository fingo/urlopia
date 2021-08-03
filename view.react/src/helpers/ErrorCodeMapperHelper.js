const UNKNOWN_ERROR_MESSAGE = "Wystąpił nieoczekiwany błąd";

const map = new Map(
    [[500, 'Niepoprawne hasło lub email'],
            [700, 'Brak odpowiedzi'],
            [800, 'Wystąpił nieoczekiwany błąd']]);

export const mapCodeToMessage = (code) => {
    if (map.has(code)) {
        return map.get(code);
    }
    else {
        return UNKNOWN_ERROR_MESSAGE;
    }
}