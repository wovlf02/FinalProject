import React from 'react';
import { useNavigate } from 'react-router-dom';

const StudyStart = () => {
  const navigate = useNavigate();

  return (
    <div>
      <h1>학습 모드 선택</h1>
      <div style={{ display: 'flex', justifyContent: 'space-around' }}>
        <div>
          <h2>개인 학습</h2>
          <p>혼자서 자유롭게 학습하고 진도를 관리할 수 있습니다.</p>
          <button onClick={() => navigate('/personal-study')}>시작하기</button>
        </div>
        <div>
          <h2>팀 학습</h2>
          <p>친구들과 함께 학습하고 경쟁할 수 있습니다.</p>
          <button onClick={() => navigate('/teamStudy')}>시작하기</button>
        </div>
      </div>
    </div>
  );
};

export default StudyStart;
