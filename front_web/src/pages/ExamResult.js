import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const ExamResult = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { answers = [], examQuestions = [] } = location.state || {};

  // 점수 계산
  let correctCount = 0;
  examQuestions.forEach((q, idx) => {
    if (answers[idx] && q.answer && Number(answers[idx]) === Number(q.answer)) {
      correctCount++;
    }
  });
  const total = examQuestions.length;
  const score = total > 0 ? Math.round((correctCount / total) * 100) : 0;

  return (
    <div style={{ background: '#f7f8fa', minHeight: '100vh', padding: 0 }}>
      <div style={{ maxWidth: 600, margin: '40px auto', background: '#fff', borderRadius: 16, boxShadow: '0 4px 24px #0002', padding: 40, fontFamily: 'inherit' }}>
        <h2 style={{ fontWeight: 700, fontSize: 28, marginBottom: 24, textAlign: 'center' }}>시험 결과</h2>
        <div style={{ fontSize: 22, fontWeight: 600, marginBottom: 18, textAlign: 'center' }}>점수: <span style={{ color: '#007AFF' }}>{score}점</span></div>
        <div style={{ display: 'flex', justifyContent: 'center', gap: 32, marginBottom: 32 }}>
          <div style={{ fontSize: 18 }}>정답: <b style={{ color: '#2ecc40' }}>{correctCount}</b>개</div>
          <div style={{ fontSize: 18 }}>오답: <b style={{ color: '#ff4d4f' }}>{total - correctCount}</b>개</div>
        </div>
        <button
          onClick={() => navigate('/')}
          style={{ width: '100%', background: '#111', color: '#fff', border: 'none', borderRadius: 6, padding: '14px 0', fontWeight: 600, fontSize: 17, letterSpacing: 1, marginBottom: 16 }}
        >
          홈으로 돌아가기
        </button>
        <button
          onClick={() => navigate('/evaluation')}
          style={{ width: '100%', background: '#007AFF', color: '#fff', border: 'none', borderRadius: 6, padding: '14px 0', fontWeight: 600, fontSize: 17, letterSpacing: 1 }}
        >
          다시 평가하기
        </button>
      </div>
    </div>
  );
};

export default ExamResult; 