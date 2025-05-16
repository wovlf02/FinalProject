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
  const [grade, setGrade] = useState('');
  const [studyHabit, setStudyHabit] = useState('');
  const [nickname, setNickname] = useState('');
  const [subjects, setSubjects] = useState('');
  const [profileImage, setProfileImage] = useState(null);

  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();

    if (
      !username ||
      !password ||
      !email ||
      !name ||
      !nickname ||
      !grade ||
      !subjects ||
      !studyHabit
    ) {
      alert('모든 필수 항목을 입력해 주세요.');
      return;
    }

    const phoneOnly = phone.replace(/[^0-9]/g, '');

    const formData = new FormData();

    // ✅ JSON 형태로 request라는 이름으로 추가
    const requestData = {
      username,
      password,
      email,
      name,
      phone: phoneOnly,
      grade,
      studyHabit,
      nickname,
      subjects: subjects.split(',').map((s) => s.trim()),
    };

    formData.append(
      'request',
      new Blob([JSON.stringify(requestData)], { type: 'application/json' })
    );

    // 프로필 이미지가 있다면 추가
    if (profileImage) {
      formData.append('profileImage', profileImage);
    }

    try {
      const response = await axios.post(
        'http://localhost:8080/api/auth/register',
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      );

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
      <h1 className="register-title">회원가입</h1>
      <form className="register-form" onSubmit={handleRegister}>
        <div className="register-row">
          <label>프로필 이미지</label>
          <input
            type="file"
            accept="image/*"
            onChange={(e) => setProfileImage(e.target.files[0])}
          />
        </div>

        <div className="register-row">
          <label>아이디<span>*</span></label>
          <input type="text" value={username} onChange={e => setUsername(e.target.value)} placeholder="아이디" />
        </div>
        <div className="register-row">
          <label>비밀번호<span>*</span></label>
          <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="비밀번호(8자 이상)" />
        </div>
        <div className="register-row">
          <label>이메일<span>*</span></label>
          <input type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="이메일" />
        </div>
        <div className="register-row">
          <label>이름<span>*</span></label>
          <input type="text" value={name} onChange={e => setName(e.target.value)} placeholder="이름" />
        </div>
        <div className="register-row">
          <label>전화번호</label>
          <input type="text" value={phone} onChange={e => setPhone(e.target.value.replace(/[^0-9]/g, ''))} placeholder="숫자만 입력" maxLength={15} />
        </div>
        <div className="register-row">
          <label>학년<span>*</span></label>
          <input type="number" value={grade} onChange={e => setGrade(e.target.value)} placeholder="학년" min={1} max={3} />
        </div>
        <div className="register-row">
          <label>학습 습관<span>*</span></label>
          <input type="text" value={studyHabit} onChange={e => setStudyHabit(e.target.value)} placeholder="예: 새벽형, 야행성" />
        </div>
        <div className="register-row">
          <label>닉네임<span>*</span></label>
          <input type="text" value={nickname} onChange={e => setNickname(e.target.value)} placeholder="닉네임" />
        </div>
        <div className="register-row">
          <label>과목<span>*</span></label>
          <input type="text" value={subjects} onChange={e => setSubjects(e.target.value)} placeholder="쉼표로 구분 (예: 수학, 영어)" />
        </div>
        <button className="register-btn" type="submit">회원가입</button>
      </form>
    </div>
  );
};

export default Register;
