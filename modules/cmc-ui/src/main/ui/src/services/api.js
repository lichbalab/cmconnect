
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

export const getCertificates = () => {
    return axios.get(`${API_BASE_URL}/certificates`);
};

export const deleteCertificate = (id) => {
    return axios.delete(`${API_BASE_URL}/certificates/${id}`);
};

export const getCertificate = (id) => {
    return axios.get(`${API_BASE_URL}/certificates/${id}`);
};

export const uploadCertificate = (file, alias) => {
    try {
        const formData = new FormData();
        formData.append('file', file); // Adjust the 'file' key according to your API requirements

        return axios.post(`${API_BASE_URL}/certificates/upload?alias=${encodeURIComponent(alias)}`, formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
          // Additional logic after successful upload
    } catch (error) {
      console.error('Error uploading file:', error);
      // Handle errors here
    }
};