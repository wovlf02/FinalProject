import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from "../utils/axios";
import '../css/Login.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault(); // 폼 제출로 인한 새로고침 방지

    try {
      console.log("입력된 아이디:", username);
      console.log("입력된 비밀번호:", password);

      const res = await api.post("/api/auth/login", { username, password });
      const { accessToken, refreshToken, username: uname, email, name } = res.data.data;

      // 로컬스토리지에 저장
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('username', uname);
      localStorage.setItem('email', email);
      localStorage.setItem('name', name);

      alert(`${name}님 환영합니다!`);
      navigate('/dashboard');
    } catch (error) {
      if (error.response) {
        console.error('로그인 실패 - 응답 데이터:', error.response.data);
      } else if (error.request) {
        console.error('로그인 실패 - 요청 전송됨, 응답 없음:', error.request);
      } else {
        console.error('로그인 실패 - 설정 오류:', error.message);
      }
      alert('아이디 또는 비밀번호를 확인해주세요.');
    }
  };

  return (
    <div className="login-main-root">
      <div className="login-main-left">
        <div className="login-main-title">
          <span className="login-main-title-bold">함께해요</span><br />
          <span className="login-main-title-purple">캠</span>
          <span className="login-main-title-bold">스터디</span>
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
          <button type="submit" className="login-main-btn">로그인</button>
        </form>

        <div className="login-main-bottom">
          <span className="login-main-link">아이디 또는 비밀번호를 잊어버렸나요?</span>
          <button
            type="button"
            className="login-main-admin-btn"
            onClick={() => alert('관리자 모드 준비중입니다.')}
          >
            관리자 모드
          </button>
        </div>

        <div className="login-main-bottom" style={{ marginTop: "16px" }}>
          <span className="login-main-link">계정이 없으신가요?</span>
          <button
            type="button"
            className="login-main-admin-btn"
            onClick={() => navigate('/register')}
          >
            회원가입
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
