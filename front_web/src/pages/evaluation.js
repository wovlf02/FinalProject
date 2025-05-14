import axios from 'axios';
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ExamSolve from './ExamSolve';

const testStatus = [
  { title: '수학 중간고사', dDay: 7, progress: 75, remain: 2 },
  { title: '영어 기말고사', dDay: 14, progress: 45, remain: 4 },
  { title: '과학 수행평가', dDay: 3, progress: 90, remain: 1 },
  { title: '국어 기말고사', dDay: 21, progress: 30, remain: 5 },
];

const recentResults = [
  { subject: '수학', name: '2학기 중간고사', score: 95, avg: 82, rank: '상위 15%' },
  { subject: '영어', name: '2학기 중간고사', score: 88, avg: 79, rank: '상위 25%' },
  { subject: '과학', name: '1차 수행평가', score: 92, avg: 85, rank: '상위 20%' },
];

const Evaluation = () => {
  const [showCustomExam, setShowCustomExam] = useState(false);
  const [subject, setSubject] = useState('수학');
  const [questionCount, setQuestionCount] = useState('10문제');
  const [timeLimit, setTimeLimit] = useState('30분');
  const [difficulty, setDifficulty] = useState('추천 난이도');
  const [showExam, setShowExam] = useState(false);
  const [examQuestions, setExamQuestions] = useState([]);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [answers, setAnswers] = useState([]);
  const [examTime, setExamTime] = useState(1800);
  const navigate = useNavigate();

  const handleStartExam = () => {
    const count = parseInt(questionCount.replace('문제', ''), 10);
    let time = 1800;
    if (timeLimit === '60분') time = 3600;
    else if (timeLimit === '90분') time = 5400;

    // 난이도 값 변환 (API에서 영어로 받는다면 변환 필요)
    let diff = difficulty;
    if (difficulty === '상') diff = 'high';
    else if (difficulty === '중') diff = 'medium';
    else if (difficulty === '하') diff = 'low';
    else diff = ''; // 추천 난이도 등 기타 값은 빈 문자열

    axios.get(`/api/exam/questions?subject=${subject}&count=${count}${diff ? `&difficulty=${diff}` : ''}`)
      .then(res => {
        if (!res.data || res.data.length === 0) {
          alert('해당 조건에 맞는 문제가 없습니다.');
          return;
        }
        setExamQuestions(res.data);
        setShowExam(true);
        setCurrentQuestion(0);
        setAnswers([]);
        setShowCustomExam(false);
        setExamTime(time);
        if (res.data && res.data.length > 0) {
          console.log('첫 번째 문제 imagePath:', res.data[0].imagePath);
        }
      })
      .catch(err => {
        console.log('문제 불러오기 실패:', err);
        if (err.response) {
          console.log('서버 응답:', err.response);
        } else if (err.request) {
          console.log('요청만 감:', err.request);
        } else {
          console.log('에러 메시지:', err.message);
        }
        alert('문제 불러오기 실패');
      });
  };

  if (showCustomExam) {
    return (
      <div style={{ background: '#f7f8fa', minHeight: '100vh', padding: 0 }}>
        <div style={{ maxWidth: 900, margin: '0 auto', padding: '40px 0' }}>
          <h2 style={{ fontWeight: 700, fontSize: 28, marginBottom: 24 }}>단원 평가</h2>
          <div style={{ color: 'red', marginBottom: 16 }}>
            첫 번째 문제 imagePath: {examQuestions[0] && examQuestions[0].imagePath}
          </div>
          <div style={{ display: 'flex', gap: 24, marginBottom: 32 }}>
            {/* 추천 난이도 */}
            <div style={{ background: '#fff', borderRadius: 10, boxShadow: '0 1px 4px #0001', padding: 28, flex: 1 }}>
              <div style={{ fontWeight: 600, fontSize: 18, marginBottom: 8 }}>맞춤형 시험</div>
              <div style={{ color: '#888', fontSize: 14, marginBottom: 18 }}>AI가 분석한 당신의 실력에 맞는 문제들입니다.</div>
              <div style={{ fontWeight: 700, fontSize: 22, marginBottom: 4 }}>중상 <span style={{ fontWeight: 400, fontSize: 16, color: '#888' }}>수준</span></div>
              <div style={{ color: '#888', fontSize: 13, marginBottom: 8 }}>상위 25% 수준</div>
              <ul style={{ paddingLeft: 18, marginBottom: 0, color: '#222', fontSize: 15 }}>
                <li>최근 평균 점수: 91점</li>
                <li>취약 영역: 기하와 벡터</li>
                <li>강점 영역: 미적분</li>
              </ul>
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
                  </select>
                </div>
              </div>
            </div>
          </div>
          {/* 최근 학습 분석 + 시험 시작 */}
          <div style={{ background: '#fff', borderRadius: 10, boxShadow: '0 1px 4px #0001', padding: 28, marginBottom: 24 }}>
            <div style={{ display: 'flex', gap: 32, marginBottom: 18 }}>
              <div style={{ flex: 1 }}>
                <div style={{ fontWeight: 600, fontSize: 16, marginBottom: 6 }}>92%</div>
                <div style={{ color: '#888', fontSize: 14 }}>정답률</div>
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontWeight: 600, fontSize: 16, marginBottom: 6 }}>45분</div>
                <div style={{ color: '#888', fontSize: 14 }}>평균 풀이 시간</div>
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontWeight: 600, fontSize: 16, marginBottom: 6 }}>85%</div>
                <div style={{ color: '#888', fontSize: 14 }}>이해도</div>
              </div>
            </div>
            <button onClick={handleStartExam} style={{ width: '100%', background: '#111', color: '#fff', border: 'none', borderRadius: 6, padding: '14px 0', fontWeight: 600, fontSize: 17, letterSpacing: 1 }}>
              ▶ 시험 시작하기
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (showExam && examQuestions.length > 0) {
    const question = examQuestions[currentQuestion];
    // 시험 종료 처리 함수
    const handleFinishExam = () => {
      navigate('/result', { state: { answers, examQuestions } });
    };
    // 난이도 변환 로직과 동일하게 difficulty 값을 계산해서 넘김
    let diff = difficulty;
    if (difficulty === '상') diff = 'high';
    else if (difficulty === '중') diff = 'medium';
    else if (difficulty === '하') diff = 'low';
    else diff = '';
    let difficultyLabel = '';
    if (difficulty === 'high') difficultyLabel = '상';
    else if (difficulty === 'medium') difficultyLabel = '중';
    else if (difficulty === 'low') difficultyLabel = '하';
    // 혹시라도 undefined/null이면 빈 문자열
    difficultyLabel = difficultyLabel || '';
    console.log('ExamSolve 난이도 props:', difficulty);
    return (
      <ExamSolve
        question={question}
        currentQuestion={currentQuestion}
        totalQuestions={examQuestions.length}
        answers={answers}
        setAnswers={setAnswers}
        setCurrentQuestion={setCurrentQuestion}
        onFinish={handleFinishExam}
        timeLeft={examTime}
        setTimeLeft={setExamTime}
        difficulty={diff || undefined}
      />
    );
  }

  return (
    <div style={{ background: '#f7f8fa', minHeight: '100vh', padding: 0 }}>
      <div style={{ maxWidth: 1200, margin: '0 auto', padding: '40px 0' }}>
        <h2 style={{ fontWeight: 700, fontSize: 28, marginBottom: 24 }}>단원 평가</h2>
        {/* 상단 버튼 */}
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 24 }}>
          <button onClick={() => setShowCustomExam(true)} style={{ background: '#111', color: '#fff', border: 'none', borderRadius: 6, padding: '10px 24px', fontWeight: 600, fontSize: 16 }}>+ 새 평가 추가</button>
          <div style={{ display: 'flex', gap: 12 }}>
            <button style={{ border: '1px solid #ddd', background: '#fff', borderRadius: 6, padding: '10px 18px', fontWeight: 500 }}>일정</button>
            <button style={{ border: '1px solid #ddd', background: '#fff', borderRadius: 6, padding: '10px 18px', fontWeight: 500 }}>시험 보기</button>
            <button style={{ border: '1px solid #ddd', background: '#fff', borderRadius: 6, padding: '10px 18px', fontWeight: 500 }}>학습 계획</button>
            <button style={{ border: '1px solid #ddd', background: '#fff', borderRadius: 6, padding: '10px 18px', fontWeight: 500 }}>AI 피드백</button>
          </div>
        </div>
        {/* 시험 준비 현황 */}
        <div style={{ marginBottom: 32 }}>
          <h3 style={{ fontWeight: 600, fontSize: 18, marginBottom: 16 }}>시험 준비 현황</h3>
          <div style={{ display: 'flex', gap: 16 }}>
            {testStatus.map((test, idx) => (
              <div key={idx} style={{ background: '#fff', borderRadius: 10, boxShadow: '0 1px 4px #0001', padding: 20, minWidth: 220, flex: 1 }}>
                <div style={{ fontWeight: 600, fontSize: 16 }}>{test.title}</div>
                <div style={{ color: '#888', fontSize: 14, margin: '6px 0 2px' }}>D-{test.dDay}</div>
                <div style={{ fontSize: 14, marginBottom: 8 }}>학습 진도: <b>{test.progress}%</b></div>
                <div style={{ background: '#eee', borderRadius: 4, height: 8, marginBottom: 8 }}>
                  <div style={{ width: `${test.progress}%`, background: '#111', height: '100%', borderRadius: 4 }} />
                </div>
                <div style={{ fontSize: 13, color: '#666', marginBottom: 8 }}>남은 단원: {test.remain}개</div>
                <button style={{ border: '1px solid #111', background: '#fff', borderRadius: 6, padding: '6px 14px', fontWeight: 500, fontSize: 13 }}>상세보기</button>
              </div>
            ))}
          </div>
        </div>
        {/* 최근 평가 결과 + 성적 분석 */}
        <div style={{ display: 'flex', gap: 24 }}>
          <div style={{ background: '#fff', borderRadius: 10, boxShadow: '0 1px 4px #0001', padding: 24, flex: 2 }}>
            <h3 style={{ fontWeight: 600, fontSize: 18, marginBottom: 16 }}>최근 평가 결과</h3>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 15 }}>
              <thead>
                <tr style={{ background: '#f7f8fa', textAlign: 'left' }}>
                  <th style={{ padding: '8px 10px' }}>과목</th>
                  <th style={{ padding: '8px 10px' }}>평가명</th>
                  <th style={{ padding: '8px 10px' }}>점수</th>
                  <th style={{ padding: '8px 10px' }}>평균</th>
                  <th style={{ padding: '8px 10px' }}>석차</th>
                </tr>
              </thead>
              <tbody>
                {recentResults.map((row, idx) => (
                  <tr key={idx} style={{ borderBottom: '1px solid #eee' }}>
                    <td style={{ padding: '8px 10px' }}>{row.subject}</td>
                    <td style={{ padding: '8px 10px' }}>{row.name}</td>
                    <td style={{ padding: '8px 10px' }}>{row.score}점</td>
                    <td style={{ padding: '8px 10px' }}>{row.avg}점</td>
                    <td style={{ padding: '8px 10px' }}>{row.rank}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div style={{ background: '#fff', borderRadius: 10, boxShadow: '0 1px 4px #0001', padding: 24, flex: 1, minWidth: 280 }}>
            <h3 style={{ fontWeight: 600, fontSize: 18, marginBottom: 16 }}>성적 분석</h3>
            {/* 실제 그래프 대신 자리 표시 */}
            <div style={{ height: 180, display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#888', border: '1px dashed #bbb', borderRadius: 8 }}>
              그래프 자리
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Evaluation;
