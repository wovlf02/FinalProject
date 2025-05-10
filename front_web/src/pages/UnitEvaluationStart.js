import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const UnitEvaluationStart = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { unitName, subject, level } = location.state || {};

  return (
    <div style={{ padding: '2rem', textAlign: 'center' }}>
      <h2>단원평가 시작</h2>
      <p><b>단원명:</b> {unitName}</p>
      <p><b>과목:</b> {subject}</p>
      <p><b>난이도:</b> {level}</p>
      <hr style={{ margin: '2rem 0' }} />
      <p>여기에 실제 평가 문제가 표시됩니다.</p>
      <button
        onClick={() => navigate('/')}
        style={{
          padding: '10px 20px',
          background: '#4CAF50',
          color: 'white',
          border: 'none',
          borderRadius: '5px',
          cursor: 'pointer'
        }}
      >
        메인으로 돌아가기
      </button>
    </div>
  );
};

export default UnitEvaluationStart;
