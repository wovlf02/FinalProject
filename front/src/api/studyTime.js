import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

export const getStudyTime = async () => {
    try {
        const response = await axios.get(`${API_URL}/dashboard/study-time/get`, {
            withCredentials: true
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching study time:', error);
        throw error;
    }
};

export const updateStudyTime = async (studyTimeData) => {
    try {
        const response = await axios.post(`${API_URL}/dashboard/study-time/update`, studyTimeData, {
            withCredentials: true
        });
        return response.data;
    } catch (error) {
        console.error('Error updating study time:', error);
        throw error;
    }
}; 