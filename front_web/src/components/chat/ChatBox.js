// src/components/chat/ChatBox.js
import React, { useEffect, useRef, useState } from 'react';
import '../../css/Chat.css';

const ChatBox = ({ messages, onSend }) => {
    const [message, setMessage] = useState('');
    const chatEndRef = useRef(null);

    // 스크롤을 자동으로 맨 아래로 내림
    useEffect(() => {
        if (chatEndRef.current) {
            chatEndRef.current.scrollIntoView({ behavior: 'smooth' });
        }
    }, [messages]);

    const handleSend = () => {
        const trimmed = message.trim();
        if (!trimmed) return;
        onSend(trimmed);
        setMessage('');
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') handleSend();
    };

    return (
        <div className="chat-box">
            <div className="chat-messages">
                {messages.map((msg, index) => (
                    <div key={index} className="chat-message">
                        <span className="chat-user">{msg.sender}:</span>
                        <span className="chat-text">{msg.content}</span>
                    </div>
                ))}
                <div ref={chatEndRef}></div>
            </div>

            <div className="chat-input-area">
                <input
                    type="text"
                    placeholder="메시지를 입력하세요..."
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    onKeyDown={handleKeyDown}
                />
                <button onClick={handleSend}>전송</button>
            </div>
        </div>
    );
};

export default ChatBox;
