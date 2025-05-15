import React from 'react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer
} from 'recharts';
import '../css/Statistics.css';

const studyData = [
  { month: '7월', time: 20 },
  { month: '8월', time: 35 },
  { month: '9월', time: 50 },
  { month: '10월', time: 40 },
  { month: '11월', time: 70 },
  { month: '12월', time: 0 },
];

const subjectScores = [
  { subject: '국어', score: 80 },
  { subject: '수학', score: 75 },
  { subject: '영어', score: 92 },
  { subject: '과학', score: 67 },
];

function Statistics() {
  return (
    <div className="statistics-container">
      <div className="statistics-header">
        <h2>통계</h2>
        <div className="statistics-profile">
        </div>
      </div>
      <div className="statistics-grid">
        {/* 월별 학습 시간 */}
        <div className="statistics-card">
          <div className="statistics-card-title">월별 학습 시간</div>
          <ResponsiveContainer width="100%" height={180}>
            <BarChart data={studyData}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="month" />
              <YAxis hide />
              <Tooltip />
              <Bar dataKey="time" fill="#2563eb" radius={[6, 6, 0, 0]} barSize={32} />
            </BarChart>
          </ResponsiveContainer>
        </div>
        {/* 과목별 성취도 */}
        <div className="statistics-card">
          <div className="statistics-card-title">과목별 성취도</div>
          <div style={{ marginTop: 16 }}>
            {subjectScores.map((s) => (
              <div key={s.subject} className="statistics-bar-row">
                <span className="statistics-bar-label">{s.subject}</span>
                <div className="statistics-bar-bg">
                  <div className="statistics-bar-fill" style={{ width: `${s.score}%` }} />
                </div>
                <span className="statistics-bar-value">{s.score}%</span>
              </div>
            ))}
          </div>
        </div>
        {/* 학습 목표 달성률 */}
        <div className="statistics-card">
          <div className="statistics-card-title">학습 목표 달성률</div>
          <div style={{ margin: "24px 0 8px 0", fontWeight: 600, color: "#2563eb" }}>
            진행중 <span style={{ marginLeft: 16, color: "#222" }}>85%</span>
          </div>
          <div className="statistics-bar-bg" style={{ height: 12 }}>
            <div className="statistics-bar-fill" style={{ width: "85%", height: 12, background: "#2563eb" }} />
          </div>
          <div style={{ fontSize: 13, color: "#888", marginTop: 8 }}>
            목표: 전국 상위 10% 달성
          </div>
        </div>
        {/* 취약 영역 분석 */}
        <div className="statistics-card">
          <div className="statistics-card-title">취약 영역 분석</div>
          <div className="statistics-weak-box">
            <div>
              <b>수학 · 미적분</b>
              <div style={{ fontSize: 13, color: "#888" }}>최근 3회 평균 정답률: 65%</div>
              <span className="statistics-badge-red">집중 필요</span>
            </div>
            <div style={{ marginTop: 18 }}>
              <b>영어 · 어휘</b>
              <div style={{ fontSize: 13, color: "#888" }}>최근 3회 평균 정답률: 72%</div>
              <span className="statistics-badge-yellow">보통 필요</span>
            </div>
          </div>
        </div>
        {/* 학습 패턴 분석 */}
        <div className="statistics-card">
          <div className="statistics-card-title">학습 패턴 분석</div>
          <div className="statistics-pattern-row">
            <span>평균 학습 시작 시간</span>
            <span>오전 8:30</span>
          </div>
          <div className="statistics-pattern-row">
            <span>일일 평균 학습시간</span>
            <span>5시간 30분</span>
          </div>
          <div className="statistics-pattern-row">
            <span>최다 학습 과목</span>
            <span>수학 (2시간/일)</span>
          </div>
          <div className="statistics-pattern-row">
            <span>학습 집중도 최고 시간대</span>
            <span>오전 10시 - 12시</span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Statistics;
