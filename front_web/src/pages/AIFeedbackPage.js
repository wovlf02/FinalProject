import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const AIFeedbackPage = () => {
  const [results, setResults] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const userId = localStorage.getItem('userId');
    if (userId) {
      fetch(`/api/results?userId=${userId}`)
        .then(res => res.json())
        .then(data => setResults(Array.isArray(data) ? data : []));
    }
  }, []);

  return (
    <div style={{ background: '#f7f8fa', minHeight: '100vh', padding: 0 }}>
      <div style={{ maxWidth: 900, margin: '0 auto', padding: '40px 0' }}>
        <h2 style={{ fontWeight: 700, fontSize: 28, marginBottom: 24 }}>AI 피드백</h2>
        <div style={{ color: '#888', marginBottom: 32, fontSize: 16 }}>
          최근 평가 결과를 선택하면 AI 피드백 상세를 볼 수 있습니다.
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {results.length === 0 && (
            <div style={{ color: '#aaa', textAlign: 'center', marginTop: 40 }}>
              아직 평가 결과가 없습니다.
            </div>
          )}
          {results.map((row, idx) => (
            <div
              key={idx}
              onClick={() => navigate(`/ai-feedback/${row.id}`)}
              style={{
                background: '#fff',
                borderRadius: 14,
                boxShadow: '0 2px 12px #0001',
                padding: 28,
                display: 'flex',
                alignItems: 'center',
                cursor: 'pointer',
                transition: 'box-shadow 0.2s, transform 0.2s',
                border: '1.5px solid #eee',
                gap: 32,
                position: 'relative',
                ...(row.subject === '수학' && { borderLeft: '6px solid #1976d2' }),
                ...(row.subject === '영어' && { borderLeft: '6px solid #ff9800' }),
                ...(row.subject === '국어' && { borderLeft: '6px solid #43a047' }),
                ...(row.subject !== '수학' && row.subject !== '영어' && row.subject !== '국어' && { borderLeft: '6px solid #bbb' }),
              }}
              onMouseOver={e => e.currentTarget.style.boxShadow = '0 4px 24px #0002'}
              onMouseOut={e => e.currentTarget.style.boxShadow = '0 2px 12px #0001'}
            >
              <div style={{ flex: 1 }}>
                <div style={{ fontWeight: 700, fontSize: 20, marginBottom: 6 }}>{row.subject} 단원평가</div>
                <div style={{ color: '#888', fontSize: 15, marginBottom: 8 }}>
                  {new Date(row.date).toLocaleString()}
                </div>
                <div style={{ display: 'flex', gap: 18, fontSize: 16 }}>
                  <span>점수 <b style={{ color: '#1976d2' }}>{row.score}점</b></span>
                  <span>난이도 <b>{row.difficulty || '-'}</b></span>
                  <span>풀이시간 <b>
                    {row.solveTime && row.solveTime > 0
                      ? `${Math.floor(row.solveTime / 60)}분 ${row.solveTime % 60}초`
                      : '-'}
                  </b></span>
                </div>
              </div>
              <div style={{
                fontWeight: 600,
                color: '#1976d2',
                fontSize: 17,
                marginLeft: 16
              }}>
                상세보기 &gt;
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default AIFeedbackPage;
