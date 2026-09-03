import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

export const addItem = async (item) => {
    return await axios.post(`${API_URL}/admin/items`, item, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
    });
};

export const deleteItem = async (itemId) => {
    return await axios.delete(`${API_URL}/admin/items/${itemId}`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
    });
};

export const fetchItems = async () => {
    return await axios.get(`${API_URL}/items`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
    });
};