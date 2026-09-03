import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

const getAuthHeaders = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
});

export const createRazorpayOrder = async (data) => {
    return await axios.post(
        `${API_URL}/payments/create-order`,
        data,
        getAuthHeaders()
    );
};

export const verifyPayment = async (paymentData) => {
    return await axios.post(
        `${API_URL}/payments/verify`,
        paymentData,
        getAuthHeaders()
    );
};