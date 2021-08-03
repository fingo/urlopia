import axios from 'axios';

import {mapCodeToMessage} from "./ErrorCodeMapperHelper";
import {USER_DATA_KEY} from "../constants/session.keystorage";

export const sendGetRequest = (url) => {
    return axios
        .get(url,{
            headers: getAuthHeader()
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
    if (user && user.jwt) {
        return { Authorization: 'Bearer ' + user.jwt };
    } else {
        return {};
    }
}

const NO_RESPONSE_CODE = 700;
const UNKNOWN_ERROR_CODE = 800;

const handleError = (error) => {
    let errorMessage;
    if (error.response) {
        let code = error.response.status;
        if(error.response.data.code) {
            code = error.response.data.code;
        }
        errorMessage = mapCodeToMessage(code);
    } else if (error.request) {
        errorMessage = mapCodeToMessage(NO_RESPONSE_CODE);
    } else {
        errorMessage =  mapCodeToMessage(UNKNOWN_ERROR_CODE);
    }

    throw new Error(errorMessage);
}
