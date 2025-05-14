import React, { useEffect } from 'react';

const ExamSolve = ({
  question, // 현재 문제 객체
  currentQuestion, // 현재 문제 인덱스 (0부터 시작)
  totalQuestions, // 전체 문제 수
  answers, // 사용자가 선택한 답 배열
  setAnswers, // 답 변경 함수
  setCurrentQuestion, // 문제 이동 함수
  onFinish, // 시험 종료 함수
  timeLeft, // 남은 시간 (초)
  setTimeLeft, // 추가: 상위에서 내려받는 setTimeLeft
  difficulty // 추가: 상위에서 내려받는 난이도
}) => {
  // 시간 포맷
  const minutes = String(Math.floor(timeLeft / 60)).padStart(2, '0');
  const seconds = String(timeLeft % 60).padStart(2, '0');

  // 난이도 한글 변환
  let difficultyLabel = '';
  if (difficulty === 'high') difficultyLabel = '상';
  else if (difficulty === 'medium') difficultyLabel = '중';
  else if (difficulty === 'low') difficultyLabel = '하';
  else if (difficulty === undefined) difficultyLabel = '추천';

  // 타이머 useEffect
  useEffect(() => {
    if (timeLeft <= 0) {
      onFinish();
      return;
    }
    const timer = setInterval(() => {
      setTimeLeft(prev => prev - 1);
    }, 1000);
    return () => clearInterval(timer);
  }, [timeLeft, setTimeLeft, onFinish]);

  return (
    <div style={{
      maxWidth: 600,
      margin: '40px auto',
      background: '#fff',
      borderRadius: 16,
      boxShadow: '0 4px 24px #0002',
      padding: 40,
      fontFamily: 'inherit'
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h2 style={{ fontWeight: 700, fontSize: 24, margin: 0 }}>
          단원 평가 시험{difficultyLabel && ` (${difficultyLabel})`}
        </h2>
        <div style={{ color: '#333', fontWeight: 500, fontSize: 16 }}>
          남은 시간: {minutes}:{seconds}
        </div>
      </div>
      <div style={{ marginBottom: 18, fontWeight: 600, fontSize: 18 }}>
        문제 {currentQuestion + 1} / {totalQuestions}
      </div>
      <div style={{ textAlign: 'center', margin: '32px 0' }}>
        <img
          src={`http://localhost:8080/${question.imagePath}`}
          alt="문제 이미지"
          style={{ maxWidth: '100%', borderRadius: 8, boxShadow: '0 2px 8px #0001', marginBottom: 24 }}
        />
      </div>
      {/* 정답률 표시 */}
      <div style={{ textAlign: 'center', marginBottom: 12, color: '#888', fontSize: 15 }}>
        정답률: {question.correctRate !== undefined && question.correctRate !== null ? `${question.correctRate}%` : '정보 없음'}
      </div>
      <div style={{ marginBottom: 24, fontSize: 18, fontWeight: 600, textAlign: 'center' }}>정답을 선택하세요</div>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 16 }}>
        {[1, 2, 3, 4].map(idx => (
          <button
            key={idx}
            onClick={() => {
              const newAnswers = [...answers];
              newAnswers[currentQuestion] = idx;
              setAnswers(newAnswers);
            }}
            style={{
              width: 320,
              padding: '16px 0',
              background: answers[currentQuestion] === idx ? '#007AFF' : '#f7f8fa',
              color: answers[currentQuestion] === idx ? '#fff' : '#222',
              border: answers[currentQuestion] === idx ? '2px solid #007AFF' : '2px solid #ddd',
              borderRadius: 10,
              fontWeight: 700,
              fontSize: 20,
              cursor: 'pointer',
              transition: '0.2s',
              boxShadow: answers[currentQuestion] === idx ? '0 2px 8px #007aff33' : 'none'
            }}
          >
            {idx}번
          </button>
        ))}
      </div>
      <div style={{ marginTop: 40, display: 'flex', justifyContent: 'space-between' }}>
        <button
          onClick={() => setCurrentQuestion(currentQuestion - 1)}
          disabled={currentQuestion === 0}
          style={{
            padding: '10px 32px',
            borderRadius: 8,
            border: 'none',
            background: '#eee',
            color: '#888',
            fontWeight: 600,
            fontSize: 16,
            cursor: currentQuestion === 0 ? 'not-allowed' : 'pointer'
          }}
        >
          이전
        </button>
        <button
          onClick={onFinish}
          style={{
            padding: '10px 32px',
            borderRadius: 8,
            border: 'none',
            background: '#ff4d4f',
            color: '#fff',
            fontWeight: 700,
            fontSize: 16,
            marginLeft: 12
          }}
        >
          시험 제출
        </button>
        <button
          onClick={() => setCurrentQuestion(currentQuestion + 1)}
          disabled={currentQuestion === totalQuestions - 1}
          style={{
            padding: '10px 32px',
            borderRadius: 8,
            border: 'none',
            background: '#111',
            color: '#fff',
            fontWeight: 600,
            fontSize: 16,
            marginLeft: 12,
            cursor: currentQuestion === totalQuestions - 1 ? 'not-allowed' : 'pointer'
          }}
        >
          다음
        </button>
      </div>
    </div>
  );
};

export default ExamSolve;
