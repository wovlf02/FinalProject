import React, { useState } from 'react';
import '../css/UnitEvaluationPlan.css';

const subjects = ['수학', '국어', '영어', '과학', '사회', '기타'];
const weeks = [1, 2, 3, 4];

function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

// Day별 더미 학습 계획 생성 함수
function generatePlan(subject, week) {
  const baseScore = getRandomInt(60, 90);
  const targetScore = baseScore + getRandomInt(5, 15);
  const totalTime = (week * getRandomInt(2, 4) + getRandomInt(2, 4)).toFixed(1);

  // Day별 계획
  const days = Array.from({ length: week * 2 }, (_, i) => ({
    day: i + 1,
    title: [
      '기초 개념 학습',
      '공식 암기 및 응용',
      '실전 문제풀이',
      '복습/테스트'
    ][i % 4],
    tasks: [
      '핵심 개념 정리',
      '중요 공식 암기',
      '문제 풀이 연습',
      '오답노트 작성',
      '실전 모의고사',
      '리뷰 및 피드백'
    ].slice(0, getRandomInt(2, 4)),
    time: getRandomInt(2, 4)
  }));

  return {
    baseScore,
    targetScore,
    totalTime,
    days
  };
}

const UnitEvaluationPlan = () => {
  const [subject, setSubject] = useState('수학');
  const [week, setWeek] = useState(2);
  const [plan, setPlan] = useState(null);
  const [progress, setProgress] = useState([]);

  // 계획 생성 버튼 클릭
  const handleGenerate = () => {
    const newPlan = generatePlan(subject, week);
    setPlan(newPlan);
    setProgress(Array(newPlan.days.length).fill(false));
  };

  // 진도 체크박스
  const handleCheck = (idx) => {
    setProgress(prev => prev.map((v, i) => (i === idx ? !v : v)));
  };

  return (
    <div className="plan-container">
      <h2>학습 계획</h2>
      <div className="plan-ai-bar">
        <div>
          <select value={subject} onChange={e => setSubject(e.target.value)}>
            {subjects.map(s => <option key={s}>{s}</option>)}
          </select>
          <select value={week} onChange={e => setWeek(Number(e.target.value))}>
            {weeks.map(w => <option key={w} value={w}>{w}주</option>)}
          </select>
          <button className="plan-generate-btn" onClick={handleGenerate}>
            계획 생성하기
          </button>
        </div>
      </div>
      {plan && (
        <>
          <div className="plan-summary-row">
            <div className="plan-summary-card">
              <div className="plan-summary-label">현재점수</div>
              <div className="plan-summary-value">{plan.baseScore}점</div>
            </div>
            <div className="plan-summary-card">
              <div className="plan-summary-label">목표점수</div>
              <div className="plan-summary-value">{plan.targetScore}점</div>
            </div>
            <div className="plan-summary-card">
              <div className="plan-summary-label">필요학습시간</div>
              <div className="plan-summary-value">{plan.totalTime}시간</div>
            </div>
          </div>
          <div className="plan-days-list">
            {plan.days.map((day, idx) => (
              <div className="plan-day-card" key={idx}>
                <div className="plan-day-header">
                  <b>Day {day.day} - {day.title}</b>
                  <span>{day.time}시간</span>
                </div>
                <ul>
                  {day.tasks.map((task, i) => <li key={i}>{task}</li>)}
                </ul>
              </div>
            ))}
          </div>
          <div className="plan-progress-section">
            <div className="plan-progress-title">학습 진도 체크</div>
            <ul className="plan-progress-list">
              {plan.days.map((day, idx) => (
                <li key={idx}>
                  <label>
                    <input
                      type="checkbox"
                      checked={progress[idx]}
                      onChange={() => handleCheck(idx)}
                    />
                    Day {day.day} 학습 완료
                  </label>
                </li>
              ))}
            </ul>
          </div>
        </>
      )}
    </div>
  );
};

export default UnitEvaluationPlan;
