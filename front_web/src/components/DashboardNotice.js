import React from 'react';

const DashboardNotice = () => {
  const notices = [
    { type: "중요", text: "2026학년도 9월 모의고사 일정 안내", date: "2025.09.15" },
    { type: "공지", text: "추석 연휴 학습실 운영 안내", date: "2025.09.14" },
    { type: "일반", text: "9월 학부모 상담 신청 안내", date: "2025.09.13" },
  ];

  return (
    <div className="dashboard-card dashboard-notice-card">
      <div style={{ fontWeight: 600, marginBottom: 8 }}>공지사항</div>
      <ul className="dashboard-notice-list">
        {notices.map((n, i) => (
          <li key={i} className={`type-${n.type}`}>
            <span>[{n.type}]</span>
            {n.text}
            <span style={{ float: "right", color: "#bbb", fontWeight: 400, fontSize: 13 }}>{n.date}</span>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default DashboardNotice;