import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/api'; // ✅ 공통 axios 인스턴스
import '../css/Login.css';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            // ✅ 1. 로그인 요청 (토큰은 HttpOnly 쿠키로 처리됨)
            await api.post('/auth/login', {
                username,
                password,
            });

            // ✅ 2. 로그인 성공 시 사용자 정보 요청
            const userResponse = await api.get('/users/me');
            const nickname = userResponse.data.nickname;

            // ✅ 3. 환영 메시지 출력
            alert(`로그인 성공: ${nickname}님 환영합니다!`);
            navigate('/dashboard');
        } catch (err) {
            alert('아이디 또는 비밀번호를 확인하세요.');
            console.error('로그인 에러:', err);
        }
    };

    return (
        <div className="login-main-root">
            <div className="login-main-left">
                <div className="login-title-art-special">
                    <div className="login-title-row">
                        <span className="login-title-ham">함</span>
                        <span className="login-title-rest">께</span>
                        <span className="login-title-rest">해요</span>
                    </div>
                    <div className="login-title-row login-title-camstudy-row">
                        <span className="login-title-placeholder">함께</span>
                        <span className="login-title-cam">캠</span>
                        <span className="login-title-study">스터디</span>
                    </div>
                </div>

                <form className="login-main-form" onSubmit={handleLogin}>
                    <input
                        type="text"
                        placeholder="아이디"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        className="login-main-input"
                        autoComplete="username"
                    />
                    <input
                        type="password"
                        placeholder="비밀번호"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="login-main-input"
                        autoComplete="current-password"
                    />
                    <button type="submit" className="login-main-btn">확인</button>
                </form>

                <div className="login-main-bottom" style={{ marginTop: "16px" }}>
                    <span className="login-main-link">계정이 없으신가요?</span>
                    <button
                        type="button"
                        className="login-main-admin-btn"
                        onClick={() => navigate('/register')}
                    >
                        회원가입
                    </button>
                    <button
                        type="button"
                        className="login-main-admin-btn"
                        onClick={() => alert('관리자 모드 준비중')}
                        style={{ marginLeft: "8px" }}
                    >
                        관리자 모드
                    </button>
                </div>
            </div>

            <div className="login-main-right">
                <div className="login-main-phone-group">
                    <img
                        src="/image1.jpg"
                        alt="앱 미리보기1"
                        className="login-main-phone-img phone-img-top"
                    />
                    <img
                        src="/image2.png"
                        alt="앱 미리보기2"
                        className="login-main-phone-img phone-img-bottom"
                    />
                </div>
            </div>
        </div>
    );
};

export default Login;
