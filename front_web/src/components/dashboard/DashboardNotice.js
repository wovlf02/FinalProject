import React, { useEffect, useState } from 'react';
import api from '../../api/api'; // ✅ 공통 axios 인스턴스 사용

const DashboardNotice = () => {
    const [notices, setNotices] = useState([]);

    useEffect(() => {
        const fetchNotices = async () => {
            try {
                const res = await api.get('/dashboard/notices');
                setNotices(res.data); // ✅ 공지사항 목록 세팅
            } catch (err) {
                console.error('공지사항 조회 실패:', err);
            }
        };

        fetchNotices();
    }, []);

    return (
        <div className="dashboard-card dashboard-notice-card">
            <div style={{ fontWeight: 600, marginBottom: 8 }}>공지사항</div>
            <ul className="dashboard-notice-list">
                {notices.length === 0 ? (
                    <li style={{ color: '#999' }}>공지사항이 없습니다.</li>
                ) : (
                    notices.map((n, i) => (
                        <li key={i} className={`type-${n.type}`}>
                            <span>[{n.type}]</span>
                            {n.text}
                            <span
                                style={{
                                    float: 'right',
                                    color: '#bbb',
                                    fontWeight: 400,
                                    fontSize: 13,
                                }}
                            >
                {n.date}
              </span>
                        </li>
                    ))
                )}
            </ul>
        </div>
    );
};

export default DashboardNotice;
