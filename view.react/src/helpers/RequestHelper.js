import axios from 'axios';

import {USER_DATA_KEY} from "../constants/session.keystorage";
import {mapCodeToMessage} from "./errors/ErrorCodeMapper";
import {pushErrorNotification} from "./notifications/Notifications";

export const URL_PREFIX = process.env.NODE_ENV === 'development' ? "http://localhost:8080" : '';

export const sendGetRequest = (url) => {
    return axios
        .get(URL_PREFIX + url,{
            headers: getAuthHeader()
        })
        .then(response => {
            return response.data;
        })
        .catch(error => handleError(error));
}

export const sendPostRequest = (url, body) => {
    return axios
        .post(URL_PREFIX + url,
            body, {
                headers: getAuthHeader()
            })
        .then(response => {
            return response.data;
        })
        .catch(error => handleError(error))
}

export const sendPatchRequest = (url, body) => {
    return axios
        .patch(URL_PREFIX + url,
            body, {
                headers: getAuthHeader()
            })
        .then(response => {
            return response.data;
        })
        .catch(error => handleError(error))
}

export const sendPutRequest = (url, body) => {
    return axios
        .put(URL_PREFIX + url,
            body, {
                headers: getAuthHeader()
            })
        .then(response => {
            return response.data;
        })
        .catch(error => handleError(error))
}

export const getXlsxFromResponse = (url, fileName) => {
    return axios.get(URL_PREFIX + url, {
        responseType: "blob",
        headers: getAuthHeader(),
    }).then(response => {
        const objectURL = window.URL.createObjectURL(
            new Blob([response.data], {
                type: response.headers["content-type"],
            })
        );

        const link = document.createElement("a");
        link.href = objectURL;
        link.setAttribute("download", `${fileName}.xlsx`);
        document.body.appendChild(link);
        link.click();
    });
}

export const getPdfFromResponse = (url, fileName) => {
    return axios.get(URL_PREFIX + url, {
        responseType: 'blob',
        headers: getAuthHeader(),
    }).then(response => {
        const objURL = window.URL.createObjectURL(
            new Blob([response.data], {
                type: response.headers['content-type'],
            })
        );

        const link = document.createElement("a");
        link.href = objURL;
        link.setAttribute("download", `${fileName}.pdf`);
        document.body.appendChild(link);
        link.click();
    })
}

export const getZipFromResponse = (url, fileName) => {
    return axios.get(URL_PREFIX + url, {
        responseType: "blob",
        headers: getAuthHeader(),
    }).then(response => {
        const oURL = window.URL.createObjectURL(
            new Blob([response.data], {
                type: response.headers["content-type"],
            })
        );

        const link = document.createElement("a");
        link.href = oURL;
        link.setAttribute("download", `${fileName}.zip`);
        document.body.appendChild(link);
        link.click();
    });
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
        if (error.response.data.message) {
            code = error.response.data.message;
        }
        errorMessage = mapCodeToMessage(code);
    } else if (error.request) {
        errorMessage = mapCodeToMessage(NO_RESPONSE_CODE);
    } else {
        errorMessage = mapCodeToMessage(UNKNOWN_ERROR_CODE);
    }

    pushErrorNotification(errorMessage)

    throw new Error(errorMessage);
}
