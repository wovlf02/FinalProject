// src/pages/community/NoticePage.js
import React from 'react';
import '../../css/Notice.css';

const notices = [
    {
        id: 1,
        title: '📢 커뮤니티 이용 안내',
        content: '다른 사용자에게 예의를 지켜주세요.',
        date: '2025.05.12',
        pinned: true,
    },
    {
        id: 2,
        title: '✅ 채팅 기능 업데이트',
        content: '채팅방 고정 및 알림 설정 기능이 추가되었습니다.',
        date: '2025.05.10',
        pinned: false,
    },
    {
        id: 3,
        title: '📌 시스템 점검 예정 안내',
        content: '5월 20일 새벽 2시부터 시스템 점검이 예정되어 있습니다.',
        date: '2025.05.08',
        pinned: false,
    },
];

const Notice = () => {
    const sortedNotices = [...notices].sort((a, b) => {
        if (a.pinned && !b.pinned) return -1;
        if (!a.pinned && b.pinned) return 1;
        return new Date(b.date) - new Date(a.date);
    });

    return (
        <div className="notice-page">
            <h2 className="notice-title">공지사항</h2>
            <div className="notice-list">
                {sortedNotices.map((notice) => (
                    <div key={notice.id} className={`notice-card ${notice.pinned ? 'pinned' : ''}`}>
                        <h4 className="notice-card-title">{notice.title}</h4>
                        <p className="notice-card-content">{notice.content}</p>
                        <div className="notice-card-date">{notice.date}</div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Notice;
