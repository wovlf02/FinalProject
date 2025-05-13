import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import '../css/UnitEvaluationStart.css';

const NUM_QUESTIONS = 20;

const makeQuestions = () =>
  Array(NUM_QUESTIONS)
    .fill(null)
    .map((_, i) => ({
      id: i + 1,
      text: `문제 ${i + 1}: 예시 문항입니다.`,
      answer: null, // 'O' 또는 'X'
    }));

const UnitEvaluationStart = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { unitName, subject, level } = location.state || {};

  const [questions, setQuestions] = useState(makeQuestions());
  const [currentIdx, setCurrentIdx] = useState(0);

  const handleAnswer = (idx, answer) => {
    setQuestions(qs =>
      qs.map((q, i) => (i === idx ? { ...q, answer } : q))
    );
    // 다음 문제로 자동 이동(마지막 문제면 그대로)
    if (idx < questions.length - 1) setCurrentIdx(idx + 1);
  };

  return (
    <div className="quiz-layout">
      {/* 왼쪽 사이드바 */}
      <aside className="quiz-sidebar">
        <div className="quiz-sidebar-title">문제 목록</div>
        <ul className="quiz-sidebar-list">
          {questions.map((q, idx) => (
            <li
              key={q.id}
              className={`quiz-sidebar-item${idx === currentIdx ? ' active' : ''}`}
              onClick={() => setCurrentIdx(idx)}
            >
              <span>{q.id}</span>
              <span className={`quiz-sidebar-answer ${q.answer === 'O' ? 'o' : q.answer === 'X' ? 'x' : ''}`}>
                {q.answer || ''}
              </span>
            </li>
          ))}
        </ul>
      </aside>
      {/* 오른쪽 퀴즈 본문 */}
      <main className="quiz-main">
        <h2 className="unit-eval-title">단원평가: {unitName || '단원명 미지정'}</h2>
        <div className="unit-eval-meta">
          <span><b>과목:</b> {subject || '-'}</span>
          <span><b>난이도:</b> {level || '-'}</span>
        </div>
        <hr className="unit-eval-divider" />
        <div className="quiz-question-section">
          <div className="quiz-question-num">문제 {questions[currentIdx].id}</div>
          <div className="quiz-question-text">{questions[currentIdx].text}</div>
          <div className="quiz-answer-buttons">
            <button
              className={`quiz-answer-btn o${questions[currentIdx].answer === 'O' ? ' selected' : ''}`}
              onClick={() => handleAnswer(currentIdx, 'O')}
            >
              O
            </button>
            <button
              className={`quiz-answer-btn x${questions[currentIdx].answer === 'X' ? ' selected' : ''}`}
              onClick={() => handleAnswer(currentIdx, 'X')}
            >
              X
            </button>
          </div>
          <div className="quiz-nav-btns">
            <button
              className="quiz-nav-btn"
              onClick={() => setCurrentIdx(idx => Math.max(0, idx - 1))}
              disabled={currentIdx === 0}
            >
              이전
            </button>
            <button
              className="quiz-nav-btn"
              onClick={() => setCurrentIdx(idx => Math.min(questions.length - 1, idx + 1))}
              disabled={currentIdx === questions.length - 1}
            >
              다음
            </button>
          </div>
        </div>
        <button
          className="unit-eval-main-btn"
          style={{ marginTop: 32 }}
          onClick={() => navigate('/dashboard')}
        >
          메인으로 돌아가기
        </button>
      </main>
    </div>
  );
};

export default UnitEvaluationStart;
