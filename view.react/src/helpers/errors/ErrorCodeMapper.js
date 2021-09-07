import {ERROR_CODE_MAPPINGS, ERROR_CODE_PREFIX_MAPPINGS} from "./ErrorCodeMappings";

export const mapCodeToMessage = (errorCode) => {
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
                message = msg
                break
            }
        }
    }

    return message
}