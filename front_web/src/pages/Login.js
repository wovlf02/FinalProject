import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from "../utils/axios";
import '../css/Login.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      console.log("입력된 아이디:", username);
      console.log("입력된 비밀번호:", password);

      const response = await api.post("/api/auth/login", {
        username,
        password,
      });

      console.log('로그인 성공:', response.data);

      // ✅ accessToken, refreshToken, username 등을 localStorage에 저장
      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);
      localStorage.setItem('username', response.data.username);
      localStorage.setItem('email', response.data.email);
      localStorage.setItem('name', response.data.name);

      alert(`로그인 성공: ${response.data.name}님 환영합니다!`);
      navigate('/dashboard');
    } catch (error) {
      if (error.response) {
        console.error('로그인 실패 - 응답 데이터:', error.response.data);
        console.error('로그인 실패 - 상태 코드:', error.response.status);
      } else if (error.request) {
        console.error('로그인 실패 - 요청이 전송되었으나 응답이 없음:', error.request);
      } else {
        console.error('로그인 실패 - 요청 설정 중 에러 발생:', error.message);
      }
      alert('아이디 또는 비밀번호를 확인하세요.');
    }
  };

  return (
    <div className="login-container">
      <h1>로그인</h1>
      <div className="login-form">
        <input
          type="text"
          placeholder="아이디"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button onClick={handleLogin}>로그인</button>
      </div>
      <div className="register-link">
        <p>계정이 없으신가요?</p>
        <button onClick={() => navigate('/register')}>회원가입</button>
      </div>
    </div>
  );
};

export default Login;
