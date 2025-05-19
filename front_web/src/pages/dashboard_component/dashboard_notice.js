import React from 'react';

function DashboardNotice({ notices }) {
    return (
        <div className="dashboard-card dashboard-notice-card">
            <div style={{ fontWeight: 600, marginBottom: 8 }}>공지사항</div>
            <ul className="dashboard-notice-list">
                {notices.map((n, i) => (
                    <li key={i} className={`type-${n.type}`}>
                        <span>[{n.type}]</span>
                        {n.text}
                        <span style={{
                            float: "right",
                            color: "#bbb",
                            fontWeight: 400,
                            fontSize: 13
                        }}>{n.date}</span>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default DashboardNotice;