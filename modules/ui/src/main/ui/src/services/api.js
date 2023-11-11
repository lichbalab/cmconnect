
import axios from 'axios';

const API_BASE_URL = 'http://your-api-url.com/api';

export const getCertificates = () => {
    return axios.get(`${API_BASE_URL}/certificates`);
};

export const deleteCertificate = (id) => {
    return axios.delete(`${API_BASE_URL}/certificates/${id}`);
};
