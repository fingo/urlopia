import axios from "axios";

const URL_PREFIX =
    import.meta.env.MODE === "development" ? "http://localhost:8080" : "";

export const axiosClient = axios.create({
    baseURL: `${URL_PREFIX}/api/v2`,
});
