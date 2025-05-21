import React, { useEffect, useRef, useState } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import moment from 'moment';
import api from '../../api/api';
import base_profile from '../../icons/base_profile.png';
import { FaPaperPlane, FaSmile, FaPaperclip, FaMicrophone } from 'react-icons/fa';
import '../../css/ChatRoom.css';

const ChatRoom = ({ roomId, onReadAllMessages }) => {
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
            const [userRes, roomRes, messageRes] = await Promise.all([
                api.get('/users/me'),
                api.get(`/chat/rooms/${roomId}`),
                api.get(`/chat/rooms/${roomId}/messages`)
            ]);

            const currentUser = userRes.data;
            setUser(currentUser);

            setRoomInfo({
                ...roomRes.data.data,
                profileImageUrl: roomRes.data.data.representative_image_url
                    ? `http://localhost:8080${roomRes.data.data.representative_image_url}`
                    : base_profile,
                roomName: roomRes.data.data.room_name,
                roomType: roomRes.data.data.room_type
            });

            const normalizedMessages = (messageRes.data || []).map(msg => ({
                ...msg,
                isMe: String(msg.senderId) === String(currentUser.id),
                profileUrl: msg.profileUrl ? `http://localhost:8080${msg.profileUrl}` : base_profile,
            }));
            setMessages(normalizedMessages);

            connectStomp(currentUser, normalizedMessages);
        } catch (err) {
            console.error('❌ 초기 데이터 로딩 실패:', err);
        }
    };

    const connectStomp = (userData, loadedMessages) => {
        const socket = new SockJS('http://localhost:8080/ws/chat');
        const client = Stomp.over(socket);
        stompClient.current = client;

        client.connect({}, () => {
            client.subscribe(`/sub/chat/room/${roomId}`, (msg) => {
                const newMsg = JSON.parse(msg.body);

                if (newMsg.type === 'READ_ACK') {
                    setMessages(prev =>
                        prev.map(m =>
                            m.messageId === newMsg.messageId
                                ? { ...m, unreadCount: newMsg.unreadCount }
                                : m
                        )
                    );
                } else {
                    const normalizedMsg = {
                        ...newMsg,
                        isMe: String(newMsg.senderId) === String(userData.id),
                        profileUrl: newMsg.profileUrl ? `http://localhost:8080${newMsg.profileUrl}` : base_profile,
                    };
                    setMessages(prev => [...prev, normalizedMsg]);

                    if (normalizedMsg.senderId !== userData.id) {
                        client.send('/pub/chat/read', {}, JSON.stringify({
                            type: 'READ',
                            roomId,
                            messageId: normalizedMsg.messageId,
                        }));
                    }
                }

                setTimeout(scrollToBottom, 50);
            });

            // ✅ 방 입장 시 마지막 메시지 읽음 처리
            if (loadedMessages.length > 0) {
                const last = loadedMessages[loadedMessages.length - 1];
                if (!last.isMe) {
                    client.send('/pub/chat/read', {}, JSON.stringify({
                        type: 'READ',
                        roomId,
                        messageId: last.messageId,
                    }));
                }
                onReadAllMessages(roomId); // 뱃지 제거
            }
        }, (error) => {
            console.error('❌ STOMP 연결 실패:', error);
        });
    };

    const handleSend = () => {
        if (!message.trim() || !user || !stompClient.current?.connected) return;

        const payload = {
            roomId,
            type: "TEXT",
            content: message,
            storedFileName: null,
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

    if (!roomId || !user || !roomInfo) {
        return <div className="chat-room-empty">채팅방을 선택해주세요.</div>;
    }

    return (
        <div className="chat-room">
            <div className="chat-room-header">
                <img
                    src={roomInfo.profileImageUrl}
                    alt="room"
                    className="chat-room-profile"
                    onError={(e) => { e.target.src = base_profile; }}
                />
                <div className="chat-room-header-info">
                    <h4>{roomInfo.roomName}</h4>
                    <span className="status">{roomInfo.roomType === 'GROUP' ? '그룹채팅' : '1:1 채팅'}</span>
                </div>
            </div>

            <div className="chat-room-body">
                {messages.map((msg, index) => {
                    const formattedTime = moment(msg.sentAt || msg.time).format('A hh:mm');
                    return (
                        <div key={index} className={`message-wrapper ${msg.isMe ? 'right' : 'left'}`}>
                            {!msg.isMe && (
                                <div className="message-header">
                                    <img
                                        src={msg.profileUrl}
                                        alt="profile"
                                        className="message-avatar"
                                        onError={(e) => { e.target.src = base_profile; }}
                                    />
                                    <div className="message-nickname">{msg.nickname}</div>
                                </div>
                            )}
                            <div className={`message-content-group ${msg.isMe ? 'right' : 'left'}`}>
                                <div className={`message-bubble-wrapper ${msg.isMe ? 'right' : 'left'}`}>
                                    <div className={`message-bubble ${msg.isMe ? 'me' : ''}`}>
                                        {msg.type === 'FILE' ? (
                                            <a href={`http://localhost:8080${msg.content}`} target="_blank" rel="noreferrer">📎 첨부파일</a>
                                        ) : (
                                            msg.content
                                        )}
                                    </div>
                                    <div className={`message-time ${msg.isMe ? 'bottom-left' : 'left'}`}>{formattedTime}</div>

                                    {/* ✅ 읽지 않은 사람 수 표시 */}
                                    {msg.unreadCount > 0 && (
                                        <div className={`chat-unread-count ${msg.isMe ? 'right' : 'left'}`}>
                                            {msg.unreadCount}
                                        </div>
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
