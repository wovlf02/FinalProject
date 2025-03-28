import axios from 'axios';
import bcrypt from 'bcryptjs';

// ✅ 백엔드 API 기본 URL
const API_BASE_URL = "http://192.168.34.24:8080/api";

// ✅ Axios 인스턴스 생성
const apiInstance = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000, // 타임아웃 10초
});

// ✅ 요청 인터셉터: Access Token 자동 추가
apiInstance.interceptors.request.use(
    async (config) => {
        try {
            // Access Token은 Redis에서 가져와야 함

        }
    }
)