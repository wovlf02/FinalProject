import axios from 'axios';
import EncryptedStorage from 'react-native-encrypted-storage';

// Base URL 설정
const BASE_URL = 'http://192.168.78.59:8080/api';

// Axios 인스턴스 생성
const api = axios.create({
    baseURL: BASE_URL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// 요청 인터셉터: 모든 요청에 Authorization 헤더 추가
api.interceptors.request.use(
    async (config) => {
        if (!config.url.includes('/auth')) {
            const token = await EncryptedStorage.getItem('accessToken');
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        }
        return config;
    },
    error => Promise.reject(error)
);

// 파일 업로드 함수 (경로에 /api를 붙이지 않음)
api.upload = async (url, file, params = {}) => {
    const token = await EncryptedStorage.getItem('accessToken');

    const formData = new FormData();
    formData.append('file', {
        uri: file.uri,
        type: file.type,
        name: file.name,
    });

    return api.post(url, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
            ...(token && { Authorization: `Bearer ${token}` }),
        },
        params,
    });
};

export default api;
