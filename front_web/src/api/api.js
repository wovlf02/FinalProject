import axios from 'axios';

// ✅ 백엔드 기본 API URL
const BASE_URL = 'http://192.168.0.2:8080/api';

const api = axios.create({
    baseURL: BASE_URL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// ✅ 요청 인터셉터: access token 자동 첨부
api.interceptors.request.use(
    (config) => {
        const accessToken = sessionStorage.getItem('accessToken');
        if (accessToken && !config.url.includes('/auth')) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// ✅ 응답 인터셉터: access token 만료 시 자동 재발급
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        const refreshToken = sessionStorage.getItem('refreshToken');

        // access token 만료 + 재시도 안 한 요청이면 재발급 시도
        if (
            error.response?.status === 401 &&
            !originalRequest._retry &&
            refreshToken
        ) {
            originalRequest._retry = true;
            try {
                const res = await axios.post(`${BASE_URL}/auth/reissue`, {
                    accessToken: sessionStorage.getItem('accessToken'),
                    refreshToken: refreshToken,
                });

                // 새로운 access/refresh token 저장
                sessionStorage.setItem('accessToken', res.data.accessToken);
                sessionStorage.setItem('refreshToken', res.data.refreshToken);

                // 요청 헤더 갱신 후 재요청
                originalRequest.headers.Authorization = `Bearer ${res.data.accessToken}`;
                return api(originalRequest);
            } catch (reissueErr) {
                console.error('토큰 재발급 실패:', reissueErr);
                sessionStorage.clear(); // 실패 시 세션 비움
                window.location.href = '/login'; // 로그인 페이지로 이동
            }
        }

        return Promise.reject(error);
    }
);

// ✅ 파일 업로드용 메서드
api.upload = async (url, file, data = {}) => {
    const formData = new FormData();
    formData.append('file', file);
    Object.entries(data).forEach(([key, value]) => {
        formData.append(key, value);
    });

    const token = sessionStorage.getItem('accessToken');
    return api.post(url, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
            ...(token && { Authorization: `Bearer ${token}` }),
        },
    });
};

export default api;
