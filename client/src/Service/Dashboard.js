import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

export const fetchDashboardData = async () => {
    return await axios.get(`${API_URL}/dashboard`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
    });
};