import React, { useState } from 'react';
import axios from 'axios';
import ExamSolve from './ExamSolve';
import { useNavigate } from 'react-router-dom';

// Create axios instance with Authorization header
const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

const ExamView = ({ onClose }) => {
  const [subject, setSubject] = useState('수학');
  const [questionCount, setQuestionCount] = useState('10문제');
  const [timeLimit, setTimeLimit] = useState('30분');
  const [difficulty, setDifficulty] = useState('상');
  const [showExam, setShowExam] = useState(false);
  const [examQuestions, setExamQuestions] = useState([]);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [answers, setAnswers] = useState([]);
  const [examTime, setExamTime] = useState(1800);
  const navigate = useNavigate();

  const handleStartExam = async () => {
    let time = 1800;
    if (timeLimit === '60분') time = 3600;
    else if (timeLimit === '90분') time = 5400;

    try {
      const count = parseInt(questionCount.replace('문제', ''), 10);
      let diff = difficulty;
      if (difficulty === '상') diff = 'high';
      else if (difficulty === '중') diff = 'medium';
      else if (difficulty === '하') diff = 'low';
      else if (difficulty === '랜덤') diff = '';
      else diff = '';

      const url = diff
        ? `/exam/questions?subject=${subject}&count=${count}&difficulty=${diff}`
        : `/exam/questions?subject=${subject}&count=${count}`;

      const res = await api.get(url);
      if (!res.data || res.data.length === 0) {
        alert('해당 조건에 맞는 문제가 없습니다.');
        return;
      }
      setExamQuestions(res.data);
      setShowExam(true);
      setCurrentQuestion(0);
      setAnswers([]);
      setExamTime(time);
    } catch (err) {
      alert('문제 불러오기 실패');
    }
  };

  // ExamSolve에서 onFinish(answers, timeLeft)로 호출
  const handleFinishExam = (answers, timeLeft) => {
    navigate('/result', { 
      state: { 
        answers, 
        examQuestions,
        subject: subject,
        name: `${subject} 단원평가`,
        difficulty,
        totalTime: examTime, // 제한 시간(초)
        timeLeft           // 남은 시간(초)
      } 
    });
  };

  if (showExam && examQuestions.length > 0 && examQuestions[currentQuestion]) {
    const question = examQuestions[currentQuestion];
    let diff = difficulty;
    if (difficulty === '상') diff = 'high';
    else if (difficulty === '중') diff = 'medium';
    else if (difficulty === '하') diff = 'low';
    else diff = '';

    return (
      <ExamSolve
        question={question}
        currentQuestion={currentQuestion}
        totalQuestions={examQuestions.length}
        answers={answers}
        setAnswers={setAnswers}
        setCurrentQuestion={setCurrentQuestion}
        onFinish={handleFinishExam}
        initialTime={examTime} // 초기 제한 시간만 넘김
        difficulty={diff || undefined}
        subject={subject}
      />
    );
  }

  return (
    <div style={{ background: '#f7f8fa', minHeight: '100vh', padding: 0 }}>
      <div style={{ maxWidth: 900, margin: '0 auto', padding: '40px 0' }}>
        <h2 style={{ fontWeight: 700, fontSize: 28, marginBottom: 24 }}>단원 평가</h2>
        <div style={{ display: 'flex', gap: 24, marginBottom: 32 }}>
          {/* 추천 난이도 */}
          <div style={{ background: '#fff', borderRadius: 10, boxShadow: '0 1px 4px #0001', padding: 28, flex: 1 }}>
            <div style={{ fontWeight: 600, fontSize: 18, marginBottom: 8 }}>맞춤형 시험</div>
            <div style={{ color: '#888', fontSize: 14, marginBottom: 18 }}>당신의 실력에 맞는 시험 문제를 직접 설정할 수 있습니다.</div>
            <div style={{ fontWeight: 700, fontSize: 22, marginBottom: 4 }}>추천 <span style={{ fontWeight: 400, fontSize: 16, color: '#888' }}>수준</span></div>
            <div style={{ color: '#888', fontSize: 13, marginBottom: 8 }}></div>
            <ul style={{ paddingLeft: 18, marginBottom: 0, color: '#222', fontSize: 15 }}>
              <li>추천 난이도 : 중</li>
              <li>추천 풀이시간 : 30분  </li>
              <li>추천 문제수 : 10문제 </li>
            </ul>
            <div style={{ fontWeight: 600, fontSize: 17, marginBottom: 8 }}>실력을 확인해보려면 "랜덤" 을 선택해보세요 ! </div>
          </div>
          {/* 시험 설정 */}
          <div style={{ background: '#fff', borderRadius: 10, boxShadow: '0 1px 4px #0001', padding: 28, flex: 1 }}>
            <div style={{ fontWeight: 600, fontSize: 18, marginBottom: 18 }}>시험 설정</div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              <div>
                <span style={{ fontWeight: 500, marginRight: 12 }}>과목 선택</span>
                <select value={subject} onChange={e => setSubject(e.target.value)} style={{ padding: '6px 12px', borderRadius: 6, border: '1px solid #ddd' }}>
                  <option>수학</option>
                  <option>영어</option>
                  <option>국어</option>
                </select>
              </div>
              <div>
                <span style={{ fontWeight: 500, marginRight: 12 }}>문제 수</span>
                <select value={questionCount} onChange={e => setQuestionCount(e.target.value)} style={{ padding: '6px 12px', borderRadius: 6, border: '1px solid #ddd' }}>
                  <option>10문제</option>
                  <option>20문제</option>
                  <option>30문제</option>
                </select>
              </div>
              <div>
                <span style={{ fontWeight: 500, marginRight: 12 }}>제한 시간</span>
                <select value={timeLimit} onChange={e => setTimeLimit(e.target.value)} style={{ padding: '6px 12px', borderRadius: 6, border: '1px solid #ddd' }}>
                  <option>30분</option>
                  <option>60분</option>
                  <option>90분</option>
                </select>
              </div>
              <div>
                <span style={{ fontWeight: 500, marginRight: 12 }}>난이도 조정</span>
                <select value={difficulty} onChange={e => setDifficulty(e.target.value)} style={{ padding: '6px 12px', borderRadius: 6, border: '1px solid #ddd' }}>
                  <option>상</option>
                  <option>중</option>
                  <option>하</option>
                  <option>랜덤</option>
                </select>
              </div>
            </div>
          </div>
        </div>
        {/* 최근 학습 분석 + 시험 시작 */}
        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <button onClick={handleStartExam} style={{ width: '100%', background: '#111', color: '#fff', border: 'none', borderRadius: 6, padding: '14px 0', fontWeight: 600, fontSize: 17, letterSpacing: 1 }}>
            ▶ 시험 시작하기
          </button>
        </div>
      </div>
    </div>
  );
};

export default ExamView; 