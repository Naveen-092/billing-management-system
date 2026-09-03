import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

export const addCategory = async (category) => {
    return await axios.post(
        `${API_URL}/admin/categories`,
        category,
        {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
        }
    );
};

export const deleteCategory = async (categoryId) => {
    return await axios.delete(
        `${API_URL}/admin/categories/${categoryId}`,
        {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
        }
    );
};

export const fetchCategories = async () => {
    return await axios.get(`${API_URL}/categories`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
    });
};