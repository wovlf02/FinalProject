import axios from 'axios';

// Base Url 설정
const BASE_URL = 'http://10.0.2.2:8080/api';

// Axios 인스턴스 생성
const api = axios.create({
    baseURL: BASE_URL,
    timeout: 10000, // 10초 타임아웃
    headers: {
        'Content-Type': 'application/json',
    },
});

export default api;