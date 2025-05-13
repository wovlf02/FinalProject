import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import '../css/UnitEvaluationStart.css';

const UnitEvaluationStart = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { unitName, subject, level } = location.state || {};

  return (
    <div className="unit-eval-start-container">
      <h2 className="unit-eval-title">단원평가 시작</h2>
      <p><b>단원명:</b> {unitName}</p>
      <p><b>과목:</b> {subject}</p>
      <p><b>난이도:</b> {level}</p>
      <hr className="unit-eval-divider" />
      <p className="unit-eval-desc">여기에 실제 평가 문제가 표시됩니다.</p>
      <button
        className="unit-eval-main-btn"
        onClick={() => navigate('/')}
      >
        메인으로 돌아가기
      </button>
    </div>
  );
};

export default UnitEvaluationStart;
