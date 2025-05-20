import React, { useEffect, useRef, useState } from 'react';
import '../../css/ChatRoom.css';
import { FaPaperPlane, FaSmile, FaPaperclip, FaMicrophone } from 'react-icons/fa';
import api from '../../api/api';
import moment from 'moment';

const ChatRoom = ({ roomId }) => {
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const [user, setUser] = useState(null);
    const [roomInfo, setRoomInfo] = useState(null);

    const socketRef = useRef(null);
    const scrollRef = useRef(null);

    const scrollToBottom = () => {
        scrollRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    const fetchInitialData = async () => {
        try {
            const userRes = await api.get('/users/me');
            const roomRes = await api.get(`/chat/rooms/${roomId}`);
            const messageRes = await api.get(`/chat/rooms/${roomId}/messages?page=0&size=100`);

            setUser(userRes.data);
            setRoomInfo(roomRes.data?.data || {});
            setMessages(messageRes.data?.data || []);

            connectWebSocket(userRes.data, messageRes.data?.data || []);
        } catch (err) {
            console.error('❌ 초기 데이터 로딩 실패:', err);
        }
    };

    const connectWebSocket = (userData, loadedMessages) => {
        const socket = new WebSocket('ws://localhost:8080/ws/chat');
        socketRef.current = socket;

        socket.onopen = () => {
            socket.send(JSON.stringify({
                type: 'ENTER',
                roomId,
                content: `${userData.nickname}님이 입장하셨습니다.`,
                time: new Date().toISOString(),
            }));

            loadedMessages.forEach(msg => {
                if (msg.senderId !== userData.id && msg.unreadCount > 0) {
                    socket.send(JSON.stringify({
                        type: 'READ',
                        roomId,
                        messageId: msg.messageId,
                    }));
                }
            });
        };

        socket.onmessage = (e) => {
            try {
                const msg = JSON.parse(e.data);
                console.log("📥 수신된 메시지:", msg);

                if (msg.type === 'READ_ACK') {
                    setMessages(prev =>
                        prev.map(m => m.messageId === msg.messageId
                            ? { ...m, unreadCount: msg.unreadCount }
                            : m)
                    );
                } else {
                    setMessages(prev => [...prev, msg]);
                    setTimeout(scrollToBottom, 50);
                }
            } catch (err) {
                console.error('❌ 메시지 파싱 실패:', err);
            }
        };

        socket.onerror = (e) => console.error('❌ WebSocket 오류:', e);
        socket.onclose = () => console.log('🛑 WebSocket 연결 종료');
    };

    useEffect(() => {
        if (!roomId) return;
        fetchInitialData();
        return () => socketRef.current?.close();
    }, [roomId]);

    const handleSend = () => {
        if (!message.trim() || !user || !socketRef.current) return;

        const msg = {
            type: 'TEXT',
            roomId,
            senderId: user.id,
            nickname: user.nickname,
            profileUrl: user.profileImageUrl,
            content: message,
            time: new Date().toISOString(), // 백엔드는 sentAt 사용
        };

        socketRef.current.send(JSON.stringify(msg));
        setMessage('');
        setTimeout(scrollToBottom, 50);
    };

    if (!roomId || !user) {
        return <div className="chat-room-empty">채팅방을 선택해주세요.</div>;
    }

    return (
        <div className="chat-room">
            <div className="chat-room-header">
                <img
                    src={roomInfo?.profileImageUrl ? `${roomInfo.profileImageUrl}` : '/images/profile.png'}
                    alt="room"
                    className="chat-room-profile"
                />
                <div className="chat-room-header-info">
                    <h4>{roomInfo?.roomName || '채팅방'}</h4>
                    <span className="status">그룹채팅</span>
                </div>
            </div>

            <div className="chat-room-body">
                {user && messages.map((msg, index) => {
                    const isMe = String(msg.senderId) === String(user.id);
                    const isFile = msg.type === 'FILE';
                    const formattedTime = moment(msg.sentAt || msg.time).format('A hh:mm');

                    return (
                        <div key={index} className={`message-wrapper ${isMe ? 'right' : 'left'}`}>
                            {!isMe && (
                                <div className="message-header">
                                    <img
                                        src={msg.profileUrl ? `${msg.profileUrl}` : '/images/profile.png'}
                                        alt="profile"
                                        className="message-avatar"
                                    />
                                    <div className="message-nickname">{msg.nickname}</div>
                                </div>
                            )}
                            <div className={`message-content-group ${isMe ? 'right' : 'left'}`}>
                                {isMe ? (
                                    <div className="message-bubble-wrapper right">
                                        <div className="message-bubble-container">
                                            {msg.unreadCount > 0 && (
                                                <div className="chat-unread-top-left">{msg.unreadCount}</div>
                                            )}
                                            <div className="message-bubble me">
                                                {isFile ? (
                                                    <a href={msg.content} target="_blank" rel="noreferrer">📎 첨부파일</a>
                                                ) : (
                                                    msg.content
                                                )}
                                            </div>
                                            <div className="message-time-bottom-left">{formattedTime}</div>
                                        </div>
                                    </div>
                                ) : (
                                    <div className="message-bubble-wrapper left">
                                        <div className="message-bubble">
                                            {isFile ? (
                                                <a href={msg.content} target="_blank" rel="noreferrer">📎 첨부파일</a>
                                            ) : (
                                                msg.content
                                            )}
                                        </div>
                                        <div className="message-time left">{formattedTime}</div>
                                    </div>
                                )}
                            </div>
                        </div>
                    );
                })}
                <div ref={scrollRef} />
            </div>

            <div className="chat-room-input">
                <FaSmile className="input-icon" />
                <label className="input-icon">
                    <FaPaperclip />
                    <input type="file" style={{ display: 'none' }} />
                </label>
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
