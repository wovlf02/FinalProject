import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';

const Exam = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const totalTime = location.state?.totalTime || 1800; // 기본 30분(초)
  const subject = location.state?.subject || '';
  const questionCount = location.state?.questionCount || '10문제';
  const difficulty = location.state?.difficulty || '상';

  const [timeLeft, setTimeLeft] = useState(totalTime);
  const [answers, setAnswers] = useState([]);
  const [examQuestions, setExamQuestions] = useState([]);

  // 시험 문제 로드
  useEffect(() => {
    // 실제 API에 맞게 수정
    axios.get(`/api/exam/questions?subject=${subject}&count=${parseInt(questionCount)}&difficulty=${difficulty}`)
      .then(res => setExamQuestions(res.data))
      .catch(() => setExamQuestions([]));
  }, [subject, questionCount, difficulty]);

  // 타이머 감소
  useEffect(() => {
    if (timeLeft <= 0) return;
    const timer = setInterval(() => setTimeLeft(t => t - 1), 1000);
    return () => clearInterval(timer);
  }, [timeLeft]);

  // 답변 변경 핸들러
  const handleAnswerChange = (index, value) => {
    const newAnswers = [...answers];
    newAnswers[index] = value;
    setAnswers(newAnswers);
  };

  // 시험 종료 핸들러
  const handleSubmit = () => {
    navigate('/result', {
      state: {
        answers,
        examQuestions,
        subject,
        totalTime,
        timeLeft,
        difficulty
      }
    });
  };

  // 타이머 표시 (분:초)
  const formatTime = (sec) => {
    const m = String(Math.floor(sec / 60)).padStart(2, '0');
    const s = String(sec % 60).padStart(2, '0');
    return `${m}:${s}`;
  };

  return (
    <div>
      <div>
        <span>{subject}</span>
        <span>{formatTime(timeLeft)}</span>
      </div>
      {examQuestions.map((question, index) => (
        <div key={index}>
          <h3>문제 {index + 1}</h3>
          <p>{question.content}</p>
          <input
            type="text"
            value={answers[index] || ''}
            onChange={(e) => handleAnswerChange(index, e.target.value)}
          />
        </div>
      ))}
      <button onClick={handleSubmit}>제출하기</button>
    </div>
  );
};

export default Exam; 