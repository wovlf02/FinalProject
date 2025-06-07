// src/pages/community/NoticePage.js
import React, { useEffect, useState } from 'react';
import '../../css/Notice.css';
import api from '../../api/api';

const Notice = () => {
    const [notices, setNotices] = useState([]);

    useEffect(() => {
        fetchNotices();
    }, []);

    const fetchNotices = async () => {
        try {
            const res = await api.get('/community/notices');
            const mapped = (res.data || []).map((n) => ({
                id: n.id,
                title: n.title,
                content: n.content,
                date: n.created_at.slice(0, 10).replace(/-/g, '.'), // yyyy.MM.dd
                views: n.views,
            }));
            console.log("공지사항 목록", res)
            setNotices(mapped);
        } catch (err) {
            console.error('❌ 공지사항 목록 조회 실패:', err);
        }
    };

    return (
        <div className="notice-page">
            <h2 className="notice-title">📢 공지사항</h2>
            <div className="notice-list">
                {notices.map((notice) => (
                    <div key={notice.id} className="notice-card">
                        <div className="notice-card-title-row">
                            <h4 className="notice-card-title">{notice.title}</h4>
                            <span className="notice-card-views">👁 {notice.views}</span>
                        </div>
                        <div className="notice-card-date">{notice.date}</div>
                        <p className="notice-card-content">{notice.content}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Notice;

