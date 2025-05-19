import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../css/Login.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      // 쿠키 없이 토큰 직접 받기 때문에 withCredentials는 빼세요
      const response = await axios.post(
        'http://localhost:8080/api/auth/login',
        { username, password }
      );

      console.log('로그인 성공, 서버 응답 데이터:', response.data);

      // 서버가 accessToken, refreshToken, username, name 포함했다고 가정
      const data = response.data;
      if (data.accessToken) {
        localStorage.setItem('accessToken', data.accessToken);
        localStorage.setItem('refreshToken', data.refreshToken);
        localStorage.setItem('username', data.username);
        localStorage.setItem('name', data.name);
        localStorage.setItem('accessToken', data.accessToken);
        

        // 로그인 성공 시 투두리스트 초기화 또는 새로 불러오기 신호를 부모에 전달하거나 리다이렉트


        alert(`${data.name}님, 로그인 성공!`);
        navigate('/dashboard');
      } else {
        alert('로그인 실패: 서버에서 토큰이 전달되지 않았습니다.');
      }
    } catch (error) {
      console.error('로그인 실패:', error);
      alert('아이디 또는 비밀번호를 확인하세요.');
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
            required
          />
          <input
            type="password"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="login-main-input"
            autoComplete="current-password"
            required
          />
          <button type="submit" className="login-main-btn">확인</button>
        </form>

        <div className="login-main-bottom" style={{ marginTop: '16px' }}>
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
            style={{ marginLeft: '8px' }}
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
