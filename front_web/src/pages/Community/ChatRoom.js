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
            type: 'received',
            content: 'user1님, 오늘 회의는 몇 시에 시작하나요?',
            time: '오후 2:30',
        },
        {
            id: 2,
            sender: 'user1',
            senderName: 'user1',
            senderImage: user1,
            type: 'sent',
            content: '3시에 시작해요. 회의 링크도 공유드릴게요!',
            time: '오후 2:35',
        },
        {
            id: 3,
            sender: 'user3',
            senderName: 'user3',
            senderImage: user3,
            type: 'received',
            content: 'user1님, 자료는 어디서 확인하나요?',
            time: '오후 3:30',
        },
        {
            id: 4,
            sender: 'user1',
            senderName: 'user1',
            senderImage: user1,
            type: 'sent',
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
            type: 'sent',
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
            {/* 상단 상대방 정보 */}
            <div className="chat-room-header">
                <img src={user2} alt="user2" />
                <div className="chat-room-header-info">
                    <h4>user2 & user3</h4>
                    <span className="status">그룹채팅</span>
                </div>
            </div>

            {/* 메시지 영역 */}
            <div className="chat-room-body">
                {messages.map((msg) => {
                    const isMe = msg.sender === currentUser;
                    return (
                        <div key={msg.id} className={`message-row ${isMe ? 'sent' : 'received'}`}>
                            {!isMe && <img src={msg.senderImage} className="message-avatar" alt={msg.sender} />}
                            <div className="message-bubble">
                                <div className="message-meta">
                                    <span className="message-nickname">{msg.senderName}</span>
                                </div>
                                <div className="message-content">{msg.content}</div>
                                <div className="message-time">{msg.time}</div>
                            </div>
                            {isMe && <img src={msg.senderImage} className="message-avatar" alt="me" />}
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
