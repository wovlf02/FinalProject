import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/Login.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = () => {
    // 로그인 로직 (예: API 호출)
    if (username === 'test' && password === 'password') {
      alert('로그인 성공');
      navigate('/dashboard'); // 로그인 성공 시 대시보드로 이동
    } else {
      alert('아이디 또는 비밀번호를 확인하세요.');
    }
  };

  return (
    <div className="login-container">
      <h1>로그인</h1>
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
  );
};

export default Login;
