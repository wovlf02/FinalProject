import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import api from '../api/api';
import '../css/Login.css';

let stompClientGlobal = null;

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            // ✅ 세션 기반 로그인
            const res = await api.post('/auth/login', { username, password });

            if (res.status === 200) {
                // ✅ 로그인 후 세션 기반 사용자 정보 요청
                const userRes = await api.get('/users/me');
                const user = userRes.data?.data;
                console.log('user:', user);

                if (!user || !user.user_id) {
                    alert('사용자 정보를 불러올 수 없습니다.');
                    return;
                }

                const nickname = user.nickname || '사용자';
                const userId = user.user_id;

                alert(`${nickname}님, 환영합니다!`);

                // ✅ WebSocket 연결
                connectWebSocket(userId);

                // ✅ 대시보드 이동
                navigate('/dashboard');
            } else {
                alert('로그인 실패: 서버 응답 오류');
            }
        } catch (err) {
            alert('아이디 또는 비밀번호를 확인하세요.');
            console.error('로그인 에러:', err);
        }
    };

    const connectWebSocket = (userId) => {
        if (stompClientGlobal && stompClientGlobal.connected) {
            console.log('🔄 이미 WebSocket에 연결됨');
            return;
        }

        // ✅ 최신 signaling 서버 ngrok 주소 사용
        const socket = new SockJS('https://a26f-2001-2d8-699b-7a4b-f170-d29-bd64-7e6e.ngrok-free.app/ws');
        const client = new Client({
            webSocketFactory: () => socket,
            connectHeaders: { userId: String(userId) },
            debug: (str) => console.log('[STOMP]', str),
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('✅ WebSocket 연결 성공 (userId:', userId, ')');
            },
            onStompError: (frame) => {
                console.error('❌ STOMP 프로토콜 에러:', frame);
            },
            onWebSocketError: (error) => {
                console.error('❌ WebSocket 연결 실패:', error);
            }
        });

        client.activate();
        stompClientGlobal = client;
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
                    <button type="button" className="login-main-admin-btn" onClick={() => navigate('/register')}>
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
                    <img src="/image1.jpg" alt="앱 미리보기1" className="login-main-phone-img phone-img-top" />
                    <img src="/image2.png" alt="앱 미리보기2" className="login-main-phone-img phone-img-bottom" />
                </div>
            </div>
        </div>
    );
};

export default Login;
