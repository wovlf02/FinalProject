import React, { useEffect, useRef, useState } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import moment from 'moment';
import api from '../../api/api';
import base_profile from '../../icons/base_profile.png';
import { FaPaperPlane, FaSmile, FaPaperclip, FaMicrophone } from 'react-icons/fa';
import '../../css/ChatRoom.css';

const ChatRoom = ({ roomId }) => {
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const [user, setUser] = useState(null);
    const [roomInfo, setRoomInfo] = useState(null);
    const scrollRef = useRef(null);
    const stompClient = useRef(null);

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

            connectStomp(userRes.data);
        } catch (err) {
            console.error('❌ 초기 데이터 로딩 실패:', err);
        }
    };

    const connectStomp = (userData) => {
        const socket = new SockJS('http://localhost:8080/ws/chat');
        const client = Stomp.over(socket);
        stompClient.current = client;

        client.connect({}, () => {
            client.subscribe(`/sub/chat/room/${roomId}`, (msg) => {
                const message = JSON.parse(msg.body);
                console.log('📥 받은 메시지:', message);
                setMessages(prev => [...prev, message]);
                setTimeout(scrollToBottom, 50);
            });

            // 입장 메시지 (type: ENTER)
            client.send('/pub/chat/send', {}, JSON.stringify({
                roomId,
                type: 'ENTER',
                content: `${userData.nickname}님이 입장하셨습니다.`,
            }));
        }, (error) => {
            console.error('❌ STOMP 연결 실패:', error);
        });
    };

    const handleSend = () => {
        if (!message.trim() || !user || !stompClient.current?.connected) return;

        const payload = {
            roomId,
            type: 'TEXT',
            content: message,
        };

        stompClient.current.send('/pub/chat/send', {}, JSON.stringify(payload));
        setMessage('');
        setTimeout(scrollToBottom, 50);
    };

    useEffect(() => {
        if (!roomId) return;
        fetchInitialData();

        return () => {
            stompClient.current?.disconnect(() => {
                console.log('🛑 STOMP 연결 해제됨');
            });
        };
    }, [roomId]);

    if (!roomId || !user) {
        return <div className="chat-room-empty">채팅방을 선택해주세요.</div>;
    }

    return (
        <div className="chat-room">
            <div className="chat-room-header">
                <img
                    src={roomInfo?.profileImageUrl || base_profile}
                    alt="room"
                    className="chat-room-profile"
                />
                <div className="chat-room-header-info">
                    <h4>{roomInfo?.roomName || '채팅방'}</h4>
                    <span className="status">그룹채팅</span>
                </div>
            </div>

            <div className="chat-room-body">
                {messages.map((msg, index) => {
                    const isMe = String(msg.senderId) === String(user.id);
                    const isFile = msg.type === 'FILE';
                    const formattedTime = moment(msg.sentAt || msg.time).format('A hh:mm');

                    return (
                        <div key={index} className={`message-wrapper ${isMe ? 'right' : 'left'}`}>
                            {!isMe && (
                                <div className="message-header">
                                    <img
                                        src={msg.profileUrl || base_profile}
                                        alt="profile"
                                        className="message-avatar"
                                    />
                                    <div className="message-nickname">{msg.nickname}</div>
                                </div>
                            )}
                            <div className={`message-content-group ${isMe ? 'right' : 'left'}`}>
                                <div className={`message-bubble-wrapper ${isMe ? 'right' : 'left'}`}>
                                    <div className={`message-bubble ${isMe ? 'me' : ''}`}>
                                        {isFile ? (
                                            <a href={msg.content} target="_blank" rel="noreferrer">📎 첨부파일</a>
                                        ) : (
                                            msg.content
                                        )}
                                    </div>
                                    <div className={`message-time ${isMe ? 'bottom-left' : 'left'}`}>{formattedTime}</div>
                                    {isMe && msg.unreadCount > 0 && (
                                        <div className="chat-unread-top-left">{msg.unreadCount}</div>
                                    )}
                                </div>
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
