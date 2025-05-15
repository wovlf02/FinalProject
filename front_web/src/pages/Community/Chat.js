import React, { useState } from 'react';
import '../../css/Chat.css';

const dummyChatRooms = [
    {
        id: 1,
        type: 'DIRECT',
        name: '김민지',
        profileImage: '/images/user1.jpg',
        lastMessage: '오늘 자료 확인해봤어?',
        lastTime: '15:24',
        unreadCount: 2,
        isPinned: true,
    },
    {
        id: 2,
        type: 'DIRECT',
        name: '이준호',
        profileImage: '/images/user2.jpg',
        lastMessage: '내일 회의 몇 시였지?',
        lastTime: '13:10',
        unreadCount: 0,
        isPinned: false,
    },
    {
        id: 3,
        type: 'GROUP',
        name: '스터디 팀 A',
        profileImage: '/images/group1.jpg',
        lastMessage: '오늘 몇 시에 시작할까요?',
        lastTime: '11:12',
        unreadCount: 3,
        isPinned: true,
    },
    // ... 더미 데이터 추가
];

const Chat = () => {
    const [category, setCategory] = useState('ALL');

    const filteredRooms = dummyChatRooms.filter((room) => {
        if (category === 'ALL') return true;
        return room.type === category;
    });

    return (
        <div className="chatroom-container">
            <div className="chatroom-header">
                <h2>채팅</h2>
                <button className="create-btn">+ 채팅방 생성</button>
            </div>

            <div className="chatroom-tabs">
                <button onClick={() => setCategory('ALL')} className={category === 'ALL' ? 'active' : ''}>전체</button>
                <button onClick={() => setCategory('DIRECT')} className={category === 'DIRECT' ? 'active' : ''}>1:1</button>
                <button onClick={() => setCategory('GROUP')} className={category === 'GROUP' ? 'active' : ''}>그룹</button>
            </div>

            <div className="chatroom-list">
                {filteredRooms.map((room) => (
                    <div key={room.id} className="chatroom-card">
                        <img className="avatar" src={room.profileImage} alt="profile" />
                        <div className="chatroom-info">
                            <div className="chatroom-top">
                                <span className="chatroom-name">{room.name}</span>
                                <span className="chatroom-time">{room.lastTime}</span>
                            </div>
                            <div className="chatroom-bottom">
                                <span className="chatroom-msg">{room.lastMessage}</span>
                                {room.unreadCount > 0 && (
                                    <span className="chatroom-unread">{room.unreadCount}</span>
                                )}
                            </div>
                        </div>
                        {room.isPinned && <span className="pin-icon">📌</span>}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Chat;
