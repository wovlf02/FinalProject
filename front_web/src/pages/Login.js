// import React, { useState } from 'react';
// import { useNavigate } from 'react-router-dom';
// import axios from 'axios';
// import '../css/Login.css';

// const Login = () => {
//   const [username, setUsername] = useState('');
//   const [password, setPassword] = useState('');
//   const navigate = useNavigate();

//   const handleLogin = async () => {
//     try {
//       console.log("입력된 아이디:", username);
//       console.log("입력된 비밀번호:", password);

//       const response = await axios.post("http://localhost:8080/api/auth/login", {
//         username,
//         password,
//       });

//       console.log('로그인 성공:', response.data);

//       // ✅ accessToken, refreshToken, username 등을 localStorage에 저장
//       localStorage.setItem('accessToken', response.data.accessToken);
//       localStorage.setItem('refreshToken', response.data.refreshToken);
//       localStorage.setItem('username', response.data.username);
//       localStorage.setItem('email', response.data.email);
//       localStorage.setItem('name', response.data.name);

//       alert(`로그인 성공: ${response.data.name}님 환영합니다!`);
//       navigate('/dashboard');
//     } catch (error) {
//       if (error.response) {
//         console.error('로그인 실패 - 응답 데이터:', error.response.data);
//         console.error('로그인 실패 - 상태 코드:', error.response.status);
//       } else if (error.request) {
//         console.error('로그인 실패 - 요청이 전송되었으나 응답이 없음:', error.request);
//       } else {
//         console.error('로그인 실패 - 요청 설정 중 에러 발생:', error.message);
//       }
//       alert('아이디 또는 비밀번호를 확인하세요.');
//     }
//   };

//   return (
//     <div className="login-container">
//       <h1>로그인</h1>
//       <div className="login-form">
//         <input
//           type="text"
//           placeholder="아이디"
//           value={username}
//           onChange={(e) => setUsername(e.target.value)}
//         />
//         <input
//           type="password"
//           placeholder="비밀번호"
//           value={password}
//           onChange={(e) => setPassword(e.target.value)}
//         />
//         <button onClick={handleLogin}>로그인</button>
//       </div>
//       <div className="register-link">
//         <p>계정이 없으신가요?</p>
//         <button onClick={() => navigate('/register')}>회원가입</button>
//       </div>
//     </div>
//   );
// };

// export default Login;

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// import axios from 'axios'; // 실제 로그인 연동 시 사용
import '../css/Login.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    // 실제 로그인 연동 시 아래 주석 해제
    /*
    try {
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        username,
        password,
      });
      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);
      localStorage.setItem('username', response.data.username);
      localStorage.setItem('email', response.data.email);
      localStorage.setItem('name', response.data.name);
      alert(`로그인 성공: ${response.data.name}님 환영합니다!`);
      navigate('/dashboard');
    } catch {
      alert('아이디 또는 비밀번호를 확인하세요.');
    }
    */
    // 시연용
    alert('로그인 성공! (시연용)');
    navigate('/dashboard');
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
        <div className="login-main-bottom">
          <span className="login-main-link">
            아이디 또는 비밀번호를 잊어버렸나요?
          </span>
          <button
            className="login-main-admin-btn"
            type="button"
            onClick={() => alert('관리자 모드 준비중')}
          >
            관리자 모드
          </button>
        </div>
        <button
          className="login-main-register-btn"
          type="button"
          onClick={() => navigate('/register')}
        >
          회원가입
        </button>
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
