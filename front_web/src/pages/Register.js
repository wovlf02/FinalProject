import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../css/Register.css';

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const navigate = useNavigate();

  const handleRegister = async () => {
    try {
      const response = await axios.post('http://localhost:8080/api/auth/register', {
        username,
        password,
        email,
        name,
        phone,
      });

      if (response.status === 200) {
        alert('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.');
        navigate('/login');
      }
    } catch (error) {
      alert('회원가입 실패: ' + (error.response?.data?.message || '오류가 발생했습니다.'));
    }
  };

  return (
    <div className="register-container">
      <h1>회원가입</h1>
      <input type="text" placeholder="아이디" value={username} onChange={(e) => setUsername(e.target.value)} />
      <input type="password" placeholder="비밀번호" value={password} onChange={(e) => setPassword(e.target.value)} />
      <input type="email" placeholder="이메일" value={email} onChange={(e) => setEmail(e.target.value)} />
      <input type="text" placeholder="이름" value={name} onChange={(e) => setName(e.target.value)} />
      <input type="text" placeholder="전화번호" value={phone} onChange={(e) => setPhone(e.target.value)} />
      <button onClick={handleRegister}>회원가입</button>
    </div>
  );
};

export default Register;