import React from 'react';
import { useNavigate } from 'react-router-dom';

const StudyListPage = ({ studyList }) => {
  const navigate = useNavigate();

  return (
    <div style={{
      maxWidth: 800,
      margin: '40px auto',
      background: '#fff',
      borderRadius: 14,
      boxShadow: '0 4px 24px rgba(0,0,0,0.08)',
      padding: '36px 40px 32px 40px'
    }}>
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 24
      }}>
        <h2 style={{ margin: 0, color: '#23272f' }}>스터디 전체 목록</h2>
        <button
          style={{
            background: '#2563eb',
            color: '#fff',
            border: 'none',
            borderRadius: 8,
            padding: '8px 20px',
            fontWeight: 600,
            fontSize: '1rem',
            cursor: 'pointer'
          }}
          onClick={() => navigate('/study/create')}
        >
          + 스터디 만들기
        </button>
      </div>
      {studyList.length === 0 ? (
        <div style={{ color: '#888', textAlign: 'center' }}>스터디가 없습니다.</div>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
          {studyList.map(study => (
            <li
              key={study.id}
              style={{
                background: study.color,
                marginBottom: 18,
                borderRadius: 10,
                padding: '18px 22px',
                cursor: 'pointer',
                boxShadow: '0 1px 6px rgba(0,0,0,0.06)'
              }}
              onClick={() => navigate(`/study/${study.id}`)}
            >
              <div style={{
                fontWeight: 700,
                fontSize: '1.13rem',
                marginBottom: 6
              }}>{study.name}</div>
              <span style={{
                background: study.tagColor,
                color: '#fff',
                borderRadius: 8,
                padding: '4px 12px',
                fontWeight: 600,
                marginRight: 10,
                fontSize: '0.98rem'
              }}>{study.status}</span>
              <span style={{ marginRight: 10 }}>
                {study.schedule} | {study.members}명 활동
              </span>
              {study.status === '모집중' && (
                <span style={{
                  color: '#22c55e',
                  fontWeight: 600,
                  fontSize: '0.98rem'
                }}>
                  모집중
                </span>
              )}
              {study.status === '마감' && (
                <span style={{
                  color: '#ef4444',
                  fontWeight: 600,
                  fontSize: '0.98rem'
                }}>
                  마감
                </span>
              )}
              <div style={{ color: '#555', marginTop: 4 }}>{study.info}</div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default StudyListPage;
