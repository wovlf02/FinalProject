import axios from 'axios';
import { API_BASE_URL_8080} from './apiUrl'; // <- .env 대신 명시적 import

// ✅ 백엔드 API 기본 URL
// ✅ Axios 인스턴스 생성 (withCredentials 필수)
const api = axios.create({
    baseURL: `${API_BASE_URL_8080}/api`,
    timeout: 10000,
    withCredentials: true, // ✅ 쿠키 자동 포함
    headers: {
        'Content-Type': 'application/json',
    },
});

// ✅ 응답 인터셉터: 인증 실패 시 리디렉션 처리
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

// ✅ 단일 파일 또는 복수 파일 업로드 지원 메서드
api.upload = async (url, files, extraData = {}) => {
    const formData = new FormData();

    // 파일 배열로 처리 (단일 파일도 배열로 변환)
    const fileArray = Array.isArray(files) ? files : [files];
    fileArray.forEach((file) => {
        formData.append('file', file);
    });

    // 추가 데이터 함께 전송
    Object.entries(extraData).forEach(([key, value]) => {
        formData.append(key, value);
    });

    return api.post(url, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
};

export default api;
