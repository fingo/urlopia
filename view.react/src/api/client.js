import axios from "axios";

const URL_PREFIX =
    process.env.NODE_ENV === "development" ? "http://localhost:8080" : "";

export const axiosClient = axios.create({
    baseURL: `${URL_PREFIX}/api/v2`,
});
