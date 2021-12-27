import {getCurrentUser} from "../../api/services/session.service";
import {ERROR_CODE_MAPPINGS, ERROR_CODE_PREFIX_MAPPINGS} from "./ErrorCodeMappings";

const getNotEnoughDaysMessage = () => {
    const {ec: isUserEC} = getCurrentUser()
    const suffix = isUserEC ? "urlopowych" : "przerwy"
    return `Nie posiadasz wystarczającej liczby dni ${suffix}`
}

export const mapCodeToMessage = (errorCode) => {
    const notEnoughDaysErrorCode = "NOT_ENOUGH_DAYS"
    let message = "Wystąpił nieoczekiwany błąd"

    if (errorCode) {
        for (const [codePrefix, msg] of Object.entries(ERROR_CODE_PREFIX_MAPPINGS)) {
            if (errorCode.startsWith(codePrefix)) {
                message = msg;
                break
            }
        }

        for (const [code, msg] of Object.entries(ERROR_CODE_MAPPINGS)) {
            if (errorCode === code) {
                if (errorCode === notEnoughDaysErrorCode){
                    message = getNotEnoughDaysMessage()
                    break;
                }
                message = msg
                break
            }
        }
    }

    return message
}
