import React, { useState, useEffect, useRef } from 'react';
import '../../css/ChatRoom.css';
import { FaPaperPlane, FaSmile, FaPaperclip, FaMicrophone } from 'react-icons/fa';
import user1 from '../../icons/user1.png';
import user2 from '../../icons/user2.png';
import user3 from '../../icons/user3.png';

const ChatRoom = () => {
    const [message, setMessage] = useState('');
    const currentUser = 'user1'; // 로그인 유저 ID
    const scrollRef = useRef(null);

    const [messages, setMessages] = useState([
        {
            id: 1,
            sender: 'user2',
            senderName: 'user2',
            senderImage: user2,
            content: 'user1님, 오늘 회의는 몇 시에 시작하나요?',
            time: '오후 2:30',
        },
        {
            id: 2,
            sender: 'user1',
            senderName: 'user1',
            senderImage: user1,
            content: '3시에 시작해요. 회의 링크도 공유드릴게요!',
            time: '오후 2:35',
        },
        {
            id: 3,
            sender: 'user3',
            senderName: 'user3',
            senderImage: user3,
            content: 'user1님, 자료는 어디서 확인하나요?',
            time: '오후 3:30',
        },
        {
            id: 4,
            sender: 'user1',
            senderName: 'user1',
            senderImage: user1,
            content: '구글 드라이브에 올렸습니다. 확인 부탁드려요!',
            time: '오후 4:30',
        },
    ]);

    const handleSend = () => {
        if (!message.trim()) return;

        const newMessage = {
            id: messages.length + 1,
            sender: currentUser,
            senderName: 'user1',
            senderImage: user1,
            content: message,
            time: '방금 전',
        };

        setMessages([...messages, newMessage]);
        setMessage('');
    };

    useEffect(() => {
        scrollRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    return (
        <div className="chat-room">
            {/* 상단 헤더 */}
            <div className="chat-room-header">
                <img src={user2} alt="user2" />
                <div className="chat-room-header-info">
                    <h4>user2 & user3</h4>
                    <span className="status">그룹채팅</span>
                </div>
            </div>

            {/* 채팅 메시지 영역 */}
            <div className="chat-room-body">
                {messages.map((msg) => {
                    const isMe = msg.sender === currentUser;

                    return (
                        <div key={msg.id} className={`message-wrapper ${isMe ? 'right' : 'left'}`}>
                            <div className="message-content-wrap">
                                {/* 닉네임 + 프로필 라인 (윗줄) */}
                                <div className={`message-meta ${isMe ? 'right' : 'left'}`}>
                                    {!isMe && <img src={msg.senderImage} className="message-avatar" alt={msg.sender} />}
                                    <span className="message-nickname">{msg.senderName}</span>
                                    {isMe && <img src={msg.senderImage} className="message-avatar" alt="me" />}
                                </div>

                                {/* 말풍선 라인 (아랫줄) */}
                                <div className="message-bubble">
                                    <div className="message-content">{msg.content}</div>
                                    <div className="message-time">{msg.time}</div>
                                </div>
                            </div>
                        </div>
                    );
                })}
                <div ref={scrollRef} />
            </div>


            {/* 입력창 */}
            <div className="chat-room-input">
                <FaSmile className="input-icon" />
                <FaPaperclip className="input-icon" />
                <FaMicrophone className="input-icon" />
                <input
                    type="text"
                    placeholder="메시지를 입력하세요"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                />
                <button onClick={handleSend} className="send-btn">
                    <FaPaperPlane />
                </button>
            </div>
        </div>
    );
};

export default ChatRoom;
