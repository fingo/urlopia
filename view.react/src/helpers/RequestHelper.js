import axios from 'axios';

import {USER_DATA_KEY} from "../constants/session.keystorage";
import {mapCodeToMessage} from "./ErrorCodeMapperHelper";

export const sendGetRequest = (url) => {
    return axios
        .get(url, {
            headers: getAuthHeader(),
        })
        .then(response => {
            return response.data;
        })
        .catch(error => handleError(error));
}

export const sendPostRequest = (url, body) => {
    return axios
        .post(url,
            body, {
                headers: getAuthHeader()
            })
        .then(response => {
            return response.data;
        })
        .catch(error => handleError(error))
}

const getAuthHeader = () => {
    const user = JSON.parse(sessionStorage.getItem(USER_DATA_KEY));
    if (user && user.token) {
        return { 'authorization': user.token };
    } else {
        return {};
    }
}

const NO_RESPONSE_CODE = 'NO_RESPONSE';
const UNKNOWN_ERROR_CODE = 'UNKNOWN_ERROR'
const handleError = (error) => {
    let errorMessage;
    if (error.response) {
        let code = error.response.message;
        if(error.response.data.message) {
            code = error.response.data.message;
        }
        errorMessage = mapCodeToMessage(code);
    } else if (error.request) {
        errorMessage = mapCodeToMessage(NO_RESPONSE_CODE);
    } else {
        errorMessage =  mapCodeToMessage(UNKNOWN_ERROR_CODE);
    }

    throw new Error(errorMessage);
}
