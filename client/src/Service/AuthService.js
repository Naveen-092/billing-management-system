import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

export const login = async (data) => {
    return await axios.post(`${API_URL}/login`, data);
};