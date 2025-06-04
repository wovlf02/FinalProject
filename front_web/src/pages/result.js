import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';

// Create axios instance with default config
const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Add request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

function extractNumber(str) {
  if (!str) return '';
  const match = str.match(/[1-5]/);
  return match ? match[0] : str;
}

const Result = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');
  const { answers, examQuestions, subject, name, totalTime, timeLeft, difficulty: userDifficulty } = location.state || {};

  console.log('result.js - totalTime:', totalTime);
  console.log('result.js - timeLeft:', timeLeft);
  console.log('result.js - 풀이시간(초):', totalTime - timeLeft);

  // answers 배열에서 undefined를 빈 문자열로 대체
  const safeAnswers = examQuestions ? examQuestions.map((q, idx) => answers && answers[idx] !== undefined ? answers[idx] : '') : [];

  // 정답 개수 계산
  const correctCount = safeAnswers && examQuestions ? safeAnswers.filter((answer, index) => 
    extractNumber(answer) === extractNumber(examQuestions[index].answer)
  ).length : 0;

  // 점수 계산 (100점 만점)
  const score = safeAnswers && examQuestions ? Math.round((correctCount / examQuestions.length) * 100) : 0;

  // 풀이시간 계산
  const solveTime = (totalTime && timeLeft !== undefined)
    ? totalTime - timeLeft
    : 0;

  // 분:초 포맷
  const formatTime = (sec) => {
    const m = String(Math.floor(sec / 60)).padStart(2, '0');
    const s = String(sec % 60).padStart(2, '0');
    return `${m}분 ${s}초`;
  };

  // 난이도, 풀이시간, 문제 수 계산
  const difficulty = userDifficulty || '-';
  const questionCount = examQuestions ? examQuestions.length : 0;

  const details = examQuestions.map((q, idx) => ({
    problemId: q.problemId || q.id, // 문제 ID 필드명에 따라 수정
    userAnswer: safeAnswers[idx],
    correctAnswer: q.answer,
    correct: extractNumber(safeAnswers[idx]) === extractNumber(q.answer)
  }));

  const resultData = {
    userId,
    subject,
    name,
    score,
    difficulty,
    solveTime,
    questionCount,
    avg: 0,
    rank: '상위 0%',
    date: new Date().toISOString(),
    details
  };

  useEffect(() => {
    console.log('useEffect 실행됨');
    const userId = localStorage.getItem('userId');
    if (!userId || !examQuestions || examQuestions.length === 0) return;

    // axios 직접 사용
    axios.post('/api/results', resultData)
      .then((res) => {
        console.log('저장 성공:', res.data);
      })
      .catch((error) => {
        console.error('결과 저장 실패:', error);
      });
  }, []);

  if (!safeAnswers || !examQuestions) {
    return (
      <div style={{ textAlign: 'center', marginTop: 80, fontSize: 20 }}>
        잘못된 접근입니다. 시험을 먼저 풀어주세요.
      </div>
    );
  }

  return (
    <div style={{ background: '#f7f8fa', minHeight: '100vh', padding: 0 }}>
      <div style={{ maxWidth: 900, margin: '0 auto', padding: '40px 0' }}>
        <h2 style={{ fontWeight: 700, fontSize: 32, marginBottom: 36, textAlign: 'center', letterSpacing: 1 }}>시험 결과</h2>
        {/* 점수 카드 */}
        <div style={{
          background: 'linear-gradient(120deg, #f8fafc 0%, #e3f2fd 100%)',
          borderRadius: 24,
          boxShadow: '0 6px 32px #1976d222, 0 1.5px 0 #fff inset',
          padding: '56px 0 44px 0',
          marginBottom: 40,
          border: '2.5px solid #e3f2fd',
          textAlign: 'center',
          position: 'relative',
          maxWidth: 520,
          marginLeft: 'auto',
          marginRight: 'auto',
          transition: 'all 0.2s'
        }}>
          <div style={{ fontSize: 22, color: '#888', fontWeight: 600, marginBottom: 18, letterSpacing: 1 }}>🏆 최종 점수</div>
          <div style={{ fontSize: 96, fontWeight: 900, color: '#1976d2', textShadow: '0 4px 24px #1976d244, 0 1.5px 0 #fff', marginBottom: 10, letterSpacing: 2, lineHeight: 1.1, transition: 'all 0.2s' }}>{score}</div>
          <div style={{ fontSize: 22, color: '#1976d2', fontWeight: 700, letterSpacing: 1 }}>점</div>
        </div>
        {/* 부가 정보 카드 */}
        <div style={{
          display: 'flex',
          gap: 24,
          justifyContent: 'center',
          marginBottom: 40,
          flexWrap: 'wrap',
        }}>
          <div style={{
            background: 'linear-gradient(90deg, #e3f2fd 0%, #f5fafd 100%)',
            borderRadius: 16,
            boxShadow: '0 2px 12px #90caf944',
            padding: '24px 36px',
            minWidth: 180,
            textAlign: 'center',
            fontWeight: 700,
            fontSize: 20,
            color: '#1976d2',
          }}>
            문제 수<br /><span style={{ fontSize: 28, color: '#1976d2', fontWeight: 900 }}>{questionCount}</span>
          </div>
          <div style={{
            background: 'linear-gradient(90deg, #e3f2fd 0%, #f5fafd 100%)',
            borderRadius: 16,
            boxShadow: '0 2px 12px #90caf944',
            padding: '24px 36px',
            minWidth: 180,
            textAlign: 'center',
            fontWeight: 700,
            fontSize: 20,
            color: '#1976d2',
          }}>
            정답 개수<br /><span style={{ fontSize: 28, color: '#43a047', fontWeight: 900 }}>{correctCount}</span>
          </div>
          <div style={{
            background: 'linear-gradient(90deg, #e3f2fd 0%, #f5fafd 100%)',
            borderRadius: 16,
            boxShadow: '0 2px 12px #90caf944',
            padding: '24px 36px',
            minWidth: 180,
            textAlign: 'center',
            fontWeight: 700,
            fontSize: 20,
            color: '#1976d2',
          }}>
            풀이 시간<br /><span style={{ fontSize: 28, color: '#1976d2', fontWeight: 900 }}>{formatTime(solveTime)}</span>
          </div>
        </div>
        {/* 상세 결과 */}
        <div style={{ background: '#fff', borderRadius: 16, boxShadow: '0 4px 24px #0001', padding: 32 }}>
          <h3 style={{ fontWeight: 600, fontSize: 20, marginBottom: 24 }}>문제별 정답</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
            {examQuestions.map((question, index) => {
              const isCorrect = extractNumber(safeAnswers[index]) === extractNumber(question.answer);
              return (
                <div key={index} style={{
                  padding: 20,
                  borderRadius: 16,
                  background: isCorrect
                    ? 'linear-gradient(90deg, #e8f5e9 0%, #f1f8e9 100%)'
                    : 'linear-gradient(90deg, #ffeaea 0%, #ffd6d6 100%)',
                  border: `2px solid ${isCorrect ? '#b2dfdb' : '#ffbdbd'}`,
                  boxShadow: isCorrect
                    ? '0 2px 12px #b2dfdb33'
                    : '0 2px 12px #ffbdbd33',
                  marginBottom: 18,
                  transition: 'all 0.2s',
                }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                    <div style={{ fontWeight: 600, fontSize: 18 }}>문제 {index + 1}</div>
                    <div style={{ fontWeight: 600, color: isCorrect ? '#2e7d32' : '#c62828' }}>
                      {isCorrect ? '정답' : '오답'}
                    </div>
                  </div>
                  <div style={{ marginBottom: 12 }}>{question.content}</div>
                  <div style={{ color: '#666' }}>
                    <div>제출한 답: {safeAnswers[index] || '미제출'}</div>
                    <div>정답: {question.answer}</div>
                  </div>
                  {question.type === 'multiple' && (
                    <div style={{ marginTop: 12 }}>
                      <div style={{ fontWeight: 500, marginBottom: 8 }}>보기:</div>
                      <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                        {question.options.map((option, optIndex) => (
                          <div key={optIndex} style={{
                            padding: '8px 12px',
                            background: '#f5f5f5',
                            borderRadius: 6,
                            fontSize: 14
                          }}>
                            {option}
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
        {/* 버튼 */}
        <div style={{ display: 'flex', gap: 16, marginTop: 32 }}>
          <button
            onClick={() => navigate('/exam')}
            style={{
              flex: 1,
              padding: '16px 0',
              background: '#1976d2',
              color: '#fff',
              border: 'none',
              borderRadius: 8,
              fontWeight: 600,
              fontSize: 16
            }}
          >
            다시 풀기
          </button>
          <button
            onClick={() => {
              navigate('/evaluation');
              window.location.reload();
            }}
            style={{
              flex: 1,
              padding: '16px 0',
              background: '#fff',
              color: '#1976d2',
              border: '1px solid #1976d2',
              borderRadius: 8,
              fontWeight: 600,
              fontSize: 16
            }}
          >
            목록으로
          </button>
        </div>
      </div>
    </div>
  );
};

export default Result; 