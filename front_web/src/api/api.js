import axios from 'axios';

// ✅ 백엔드 기본 API URL
const BASE_URL = 'http://localhost:8080/api';

// ✅ Axios 인스턴스 생성 (withCredentials 필수)
const api = axios.create({
    baseURL: BASE_URL,
    timeout: 10000,
    withCredentials: true, // ✅ 쿠키 자동 포함
    headers: {
        'Content-Type': 'application/json',
    },
});

// ✅ 응답 인터셉터: 인증 실패 시 리디렉션 처리만
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            console.warn('인증 실패: 로그인 페이지로 이동');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

// ✅ 파일 업로드용 메서드 (추가 파라미터 포함 가능)
api.upload = async (url, file, data = {}) => {
    const formData = new FormData();
    formData.append('file', file);
    Object.entries(data).forEach(([key, value]) => {
        formData.append(key, value);
    });

    return api.post(url, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
};

export default api;
