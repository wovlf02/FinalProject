import React from 'react';
import { useNavigate } from 'react-router-dom';

function PlanMenu() {
  const navigate = useNavigate();
  return (
    <div style={{
      minHeight: "100vh",
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
      background: "linear-gradient(120deg, #f7fafd 60%, #e0e7ff 100%)"
    }}>
      <div style={{
        background: "#fff",
        borderRadius: 18,
        boxShadow: "0 4px 24px #2563eb22",
        padding: "56px 48px 48px 48px",
        minWidth: 370,
        maxWidth: 460,
        textAlign: "center"
      }}>
        <h2 style={{
          marginBottom: 38,
          fontWeight: 800,
          fontSize: "2.1rem",
          color: "#222",
          letterSpacing: "-0.01em"
        }}>
          학습 계획
        </h2>
        <div style={{display:"flex", flexDirection:"column", gap: 26}}>
          <button
            style={{
              padding: "18px 0",
              fontSize: "1.18rem",
              fontWeight: 700,
              background: "linear-gradient(90deg, #2563eb 0%, #1e40af 100%)",
              color: "#fff",
              border: "none",
              borderRadius: 12,
              cursor: "pointer",
              boxShadow: "0 2px 12px #2563eb22",
              transition: "background 0.13s, box-shadow 0.13s",
              letterSpacing: "0.02em"
            }}
            onClick={() => navigate('/plan/create')}
          >
            📝 AI로 학습 계획 생성하기
          </button>
          <button
            style={{
              padding: "18px 0",
              fontSize: "1.18rem",
              fontWeight: 700,
              background: "#fff",
              color: "#2563eb",
              border: "2.5px solid #2563eb",
              borderRadius: 12,
              cursor: "pointer",
              boxShadow: "0 2px 12px #2563eb11",
              transition: "background 0.13s, box-shadow 0.13s",
              letterSpacing: "0.02em"
            }}
            onClick={() => navigate('/plan/list')}
          >
            📚 내 학습 계획 보기
          </button>
        </div>
      </div>
    </div>
  );
}

export default PlanMenu;
