import React from 'react';

const DashboardGrowth = () => {
  const growth = [
    { subject: "수학", rate: 12 },
    { subject: "영어", rate: 8 },
    { subject: "국어", rate: 15 },
    { subject: "과학", rate: 5 },
  ];

  return (
    <div className="dashboard-card dashboard-growth-card">
      <div style={{ fontWeight: 600, marginBottom: 8 }}>주간 성장률</div>
      {growth.map((g, i) => (
        <div key={i} style={{ marginBottom: 10 }}>
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <span>{g.subject}</span>
            <span style={{ color: "#2563eb", fontWeight: 600 }}>+{g.rate}%</span>
          </div>
          <div className="dashboard-growth-bar-bg">
            <div className="dashboard-growth-bar" style={{ width: `${g.rate * 2}%` }} />
          </div>
        </div>
      ))}
    </div>
  );
};

export default DashboardGrowth;