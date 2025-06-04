import axios from 'axios';
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

// Create axios instance with default config
const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  withCredentials: true
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

const testStatus = [
  { title: '수학 수능', examDate: '2025-11-13' },
  { title: '영어 수능', examDate: '2025-11-13' },
  { title: '국어 수능', examDate: '2025-11-13' },
];

function getDDay(examDate) {
  const today = new Date();
  const exam = new Date(examDate);
  const diff = Math.ceil((exam - today) / (1000 * 60 * 60 * 24));
  return diff >= 0 ? `D-${diff}` : `종료`;
}

const Evaluation = () => {
  const [testStatus, setTestStatus] = useState([
    { title: '수학 수능', examDate: '2025-11-13' },
    { title: '영어 수능', examDate: '2025-11-13' },
    { title: '국어 수능', examDate: '2025-11-13' },
  ]);
  const [unitMap, setUnitMap] = useState({});
  const [recentResults, setRecentResults] = useState([]);
  const [selectedSubject, setSelectedSubject] = useState('수학');
  const subjects = ['국어', '영어', '수학'];
  const navigate = useNavigate();

  const handleShowDetail = (exam) => {
    navigate('/select-unit', { state: { exam } });
  };

  const handleDeleteAllResults = async () => {
    const userId = localStorage.getItem('userId');
    if (!userId) return;
    if (!window.confirm('정말 모든 평가 결과를 삭제하시겠습니까?')) return;
    try {
      await api.delete(`/results`, { params: { userId } });
      setRecentResults([]);
      alert('모든 평가 결과가 삭제되었습니다.');
    } catch (error) {
      console.error('Delete error:', error);
      alert('삭제에 실패했습니다.');
    }
  };

  useEffect(() => {
    // 과목별로 단원명 받아오기
    const subjects = ['수학', '영어', '국어'];
    Promise.all(subjects.map(subj =>
      api.get(`/units?subject=${subj}`).then(res => [subj, res.data])
    )).then(results => {
      const map = {};
      results.forEach(([subj, units]) => { map[subj] = units; });
      setUnitMap(map);
    });

    // 최근 평가 결과 받아오기
    const userId = localStorage.getItem('userId');
    if (userId) {
      api.get(`/results?userId=${userId}`)
        .then(res => setRecentResults(Array.isArray(res.data) ? res.data : []))
        .catch(() => setRecentResults([]));
    }
  }, []);

  // recentResults는 [{subject, score, date, ...}] 형태라고 가정
  const filteredResults = recentResults
    .filter(r => r.subject === selectedSubject)
    .map(r => ({
      ...r,
      dateLabel: new Date(r.date).toLocaleDateString().slice(5), // MM-DD
    }))
    .sort((a, b) => new Date(a.date) - new Date(b.date)); // 날짜 오름차순 정렬

  return (
    <div style={{ background: '#f7f8fa', minHeight: '100vh', padding: 0 }}>
      <div style={{ maxWidth: 1200, margin: '0 auto', padding: '40px 0' }}>
        <h2 style={{ fontWeight: 700, fontSize: 28, marginBottom: 24 }}>단원 평가</h2>
        {/* 상단 버튼 */}
        <div style={{ display: 'flex', justifyContent: 'flex-start', gap: 12, marginBottom: 24 }}>
          <button onClick={() => navigate('/exam-view')} style={{ background: '#111', color: '#fff', border: 'none', borderRadius: 6, padding: '10px 24px', fontWeight: 600, fontSize: 16 }}>시험보기</button>
          <button
            type="button"
            onClick={() => navigate('/ai-feedback')}
            style={{
              background: '#ff9800',
              color: '#fff',
              border: 'none',
              borderRadius: 6,
              padding: '10px 24px',
              fontWeight: 600,
              fontSize: 16
            }}
          >
            AI 피드백
          </button>
        </div>
        {/* 시험 준비 현황 */}
        <div style={{ marginBottom: 32 }}>
          <h3 style={{ fontWeight: 600, fontSize: 18, marginBottom: 16 }}>시험 준비 현황</h3>
          <div style={{ display: 'flex', gap: 16 }}>
            {testStatus.map((test, idx) => {
              const dDay = getDDay(test.examDate);
              const subject = test.title.split(' ')[0];
              const totalUnits = unitMap[subject]?.length || 0;
              const completed = JSON.parse(localStorage.getItem('completedUnits') || '{}');
              const completedUnits = completed[subject]?.length || 0;
              const remain = Math.max(totalUnits - completedUnits, 0);
              const progress = totalUnits === 0 ? 0 : Math.round((completedUnits / totalUnits) * 100);

              return (
              <div key={idx} style={{ background: '#fff', borderRadius: 10, boxShadow: '0 1px 4px #0001', padding: 20, minWidth: 220, flex: 1 }}>
                <div style={{ fontWeight: 600, fontSize: 16 }}>{test.title}</div>
                  <div style={{ color: '#888', fontSize: 14, margin: '6px 0 2px' }}>{dDay}</div>
                  <div style={{ fontSize: 14, marginBottom: 8 }}>학습 진도: <b>{progress}%</b></div>
                <div style={{ background: '#eee', borderRadius: 4, height: 8, marginBottom: 8 }}>
                    <div style={{ width: `${progress}%`, background: '#111', height: '100%', borderRadius: 4 }} />
                  </div>
                  <div style={{ fontSize: 13, color: '#666', marginBottom: 8 }}>남은 단원: {remain}개</div>
                  <button onClick={() => handleShowDetail(test)} style={{ border: '1px solid #111', background: '#fff', borderRadius: 6, padding: '6px 14px', fontWeight: 500, fontSize: 13 }}>상세보기</button>
                </div>
              );
            })}
          </div>
        </div>
        {/* 최근 평가 결과 + 성적 분석 */}
        <div style={{ display: 'flex', gap: 24 }}>
          <div style={{ background: '#fff', borderRadius: 10, boxShadow: '0 1px 4px #0001', padding: 24, flex: 6 }}>
            <h3 style={{ fontWeight: 600, fontSize: 18, marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              최근 평가 결과
              <button onClick={handleDeleteAllResults} style={{ fontSize: 13, background: '#f55', color: '#fff', border: 'none', borderRadius: 6, padding: '6px 14px', marginLeft: 8, cursor: 'pointer' }}>
                전체 삭제
              </button>
            </h3>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 15 }}>
              <thead>
                <tr style={{ background: '#f7f8fa', textAlign: 'left' }}>
                  <th style={{ padding: '8px 10px' }}>과목</th>
                  <th style={{ padding: '8px 10px' }}>점수</th>
                  <th style={{ padding: '8px 10px' }}>난이도</th>
                  <th style={{ padding: '8px 10px' }}>풀이시간</th>
                </tr>
              </thead>
              <tbody>
                {Array.isArray(recentResults) && recentResults.map((row, idx) => (
                  <tr key={idx} style={{ borderBottom: '1px solid #eee' }}>
                    <td style={{ padding: '8px 10px' }}>{row.subject}</td>
                    <td style={{ padding: '8px 10px' }}>{row.score}점</td>
                    <td style={{ padding: '8px 10px' }}>{row.difficulty || '-'}</td>
                    <td style={{ padding: '8px 10px' }}>
                      {row.solveTime && row.solveTime > 0
                        ? `${Math.floor(row.solveTime / 60)}분 ${row.solveTime % 60}초`
                        : '-'}
                    </td>
                  </tr>
                ))}
                {recentResults.length === 0 && (
                  <tr>
                    <td colSpan="4" style={{ padding: '20px 10px', textAlign: 'center', color: '#888' }}>
                      아직 평가 결과가 없습니다.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
          <div style={{ background: '#fff', borderRadius: 16, boxShadow: '0 4px 24px #0001', padding: 32, flex: 4 }}>
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: 16 }}>
              <span style={{ fontWeight: 600, fontSize: 18, marginRight: 16 }}>성적 분석</span>
              <select value={selectedSubject} onChange={e => setSelectedSubject(e.target.value)} style={{ padding: '6px 12px', borderRadius: 6, border: '1px solid #ddd' }}>
                {subjects.map(subj => <option key={subj}>{subj}</option>)}
              </select>
            </div>
            {filteredResults.length > 0 ? (
              <ResponsiveContainer width="100%" height={220}>
                <LineChart data={filteredResults} style={{ background: '#fff', borderRadius: 12 }}>
                  <CartesianGrid stroke="#eee" strokeDasharray="3 3" />
                  <XAxis dataKey="dateLabel" stroke="#222" />
                  <YAxis domain={[0, 100]} stroke="#222" />
                  <Tooltip
                    contentStyle={{ background: '#fff', border: '1px solid #111', color: '#111', borderRadius: 8 }}
                    itemStyle={{ color: '#111' }}
                    labelStyle={{ color: '#111' }}
                  />
                  <Line
                    type="monotone"
                    dataKey="score"
                    stroke="#111"
                    strokeWidth={3}
                    dot={{
                      r: 5,
                      fill: '#fff',
                      stroke: '#111',
                      strokeWidth: 4,
                      filter: 'drop-shadow(0 2px 6px #1113)'
                    }}
                    activeDot={{
                      r: 7,
                      fill: '#111',
                      stroke: '#fff',
                      strokeWidth: 5,
                      filter: 'drop-shadow(0 4px 12px #1116)'
                    }}
                  />
                </LineChart>
              </ResponsiveContainer>
            ) : (
              <div style={{ color: '#aaa', textAlign: 'center', marginTop: 40 }}>해당 과목의 시험 기록이 없습니다.</div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Evaluation;
