import React from 'react';
import '../../css/Chat.css';
import ChatRoom from './ChatRoom';
import {FaBell, FaChevronDown} from 'react-icons/fa';
import searchIcon from '../../icons/search.png'; // ✅ 아이콘 이미지 import
import user1 from '../../icons/user1.png';
import user2 from '../../icons/user2.png';
import user3 from '../../icons/user3.png';

const Chat = () => {
    const onlineUsers = [
        {id: 1, name: '유저1', avatar: user1},
        {id: 2, name: '유저2', avatar: user2},
    ];

    const chatRooms = [
        {
            id: 1,
            name: 'user1',
            avatar: user1,
            lastMessage: '오늘 자료 확인해봤어?',
            time: '5분 전',
            unread: 2,
        },
        {
            id: 2,
            name: 'user2',
            avatar: user2,
            lastMessage: '회의 일정 확인해줘.',
            time: '10분 전',
            unread: 0,
        },
        {
            id: 3,
            name: 'user3',
            avatar: user3,
            lastMessage: '오늘 몇 시에 시작할까요?',
            time: '1시간 전',
            unread: 3,
        },
    ];

    return (
        <div className="chat-container">
            <div className="chat-topbar">
                <div className="chat-search-wrapper">
                    <img src={searchIcon} className="chat-search-icon" alt="search"/>
                    <input className="chat-search" placeholder="검색"/>
                </div>
                <FaBell className="chat-icon"/>
                <div className="chat-profile">
                    <img src={user3} className="chat-avatar" alt="user3"/>
                    <span className="chat-name">홍길동</span>
                    <FaChevronDown className="chat-icon small"/>
                </div>
            </div>

            {/* 본문 영역 */}
            <div className="chat-main">
                {/* 좌측 사이드바 */}
                <div className="chat-sidebar">
                    <div className="online-now">
                        <div className="online-header">
                            <h4>Online Now</h4>
                            <span className="online-count">{onlineUsers.length}</span>
                        </div>
                        <div className="online-list">
                            {onlineUsers.map(user => (
                                <div key={user.id} className="online-user">
                                    <img src={user.avatar} alt={user.name}/>
                                    <span className="green-dot"/>
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="chat-room-list">
                        <h4>Messages</h4>
                        {chatRooms.map(room => (
                            <div key={room.id} className="chat-room-item wide">
                                <img src={room.avatar} alt={room.name}/>
                                <div className="chat-room-info">
                                    <div className="chat-room-top">
                                        <span className="chat-room-name">{room.name}</span>
                                        <span className="chat-room-time">{room.time}</span>
                                    </div>
                                    <div className="chat-room-bottom">
                                        <span className="chat-room-message">{room.lastMessage}</span>
                                        {room.unread > 0 && (
                                            <span className="chat-room-badge">{room.unread}</span>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* 우측 채팅 */}
                <ChatRoom/>
            </div>
        </div>
    );

};

export default Chat;
