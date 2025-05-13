import React from 'react';
import '../css/MyPage.css';

const user = {
  name: '홍길동',
  email: 'honggildong@example.com',
  joined: '2024-05-01',
};

const MyPage = () => {
  const handleLogout = () => {
    alert('로그아웃 되었습니다!');
  };

  const handleChangePassword = () => {
    alert('비밀번호 변경 기능은 추후 지원됩니다.');
  };

  return (
    <div className="mypage-container">
      <div className="mypage-content">
        <h2 className="mypage-title">마이페이지</h2>
        <div className="mypage-info-card">
          <div className="mypage-row">
            <span className="mypage-label">이름</span>
            <span>{user.name}</span>
          </div>
          <div className="mypage-row">
            <span className="mypage-label">이메일</span>
            <span>{user.email}</span>
          </div>
          <div className="mypage-row">
            <span className="mypage-label">가입일</span>
            <span>{user.joined}</span>
          </div>
        </div>
        <div className="mypage-btn-row">
          <button className="mypage-btn" onClick={handleChangePassword}>비밀번호 변경</button>
          <button className="mypage-btn logout" onClick={handleLogout}>로그아웃</button>
        </div>
      </div>
    </div>
  );
};

export default MyPage;
