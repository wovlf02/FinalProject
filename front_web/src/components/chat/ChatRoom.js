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
            const userRes = await api.get('/users/me', { withCredentials: true });
            const currentUser = userRes.data;
            setUser(currentUser);

            const detailRes = await api.post('/chat/rooms/detail', { room_id: roomId }, { withCredentials: true });
            const data = detailRes.data?.data;
            const room = data.room_info;
            const msgs = data.messages;

            setRoomInfo({
                ...room,
                profileImageUrl: room.representative_image_url
                    ? `http://localhost:8080${room.representative_image_url}`
                    : base_profile,
                roomName: room.room_name,
                roomType: room.room_type,
                participantCount: room.participant_count
            });

            const normalizedMessages = (msgs || []).map(msg => ({
                ...msg,
                isMe: String(msg.senderId) === String(currentUser.id),
                profileUrl: msg.profileUrl
                    ? `http://localhost:8080${msg.profileUrl}`
                    : base_profile,
            }));
            setMessages(normalizedMessages);

            connectStomp(currentUser, normalizedMessages);
        } catch (err) {
            console.error('❌ 초기 데이터 로딩 실패:', err.response?.data || err);
        }
    };

    const connectStomp = (userData, loadedMessages) => {
        const socket = new SockJS('http://localhost:8080/ws/chat', null, {
            transports: ['websocket', 'xhr-streaming', 'xhr-polling'],
            withCredentials: true, // ✅ 세션 공유 필수
        });

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
                        profileUrl: newMsg.profileUrl
                            ? `http://localhost:8080${newMsg.profileUrl}`
                            : base_profile,
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

            if (loadedMessages.length > 0) {
                const last = loadedMessages[loadedMessages.length - 1];
                if (!last.isMe) {
                    client.send('/pub/chat/read', {}, JSON.stringify({
                        type: 'READ',
                        roomId,
                        messageId: last.messageId,
                    }));
                }
                onReadAllMessages(roomId);
            }
        }, (error) => {
            console.error('❌ STOMP 연결 실패:', error);
        });
    };

    const handleSend = () => {
        if (!message.trim() || !user || !stompClient.current?.connected) return;

        const payload = {
            room_id: roomId,
            type: "TEXT",
            content: message,
            storedFileName: null,
        };

        stompClient.current.send('/pub/chat/send', {}, JSON.stringify(payload));
        setMessage('');
        setTimeout(scrollToBottom, 50);
    };

    useEffect(() => {
        if (!roomId) {
            console.log('❗ roomId가 비어 있습니다. 채팅방이 선택되지 않았습니다.');
            return;
        }

        console.log('📥 ChatRoom 진입 - 선택된 roomId:', roomId);

        setMessages([]);
        setRoomInfo(null);
        setUser(null);

        if (
            stompClient.current &&
            stompClient.current.connected &&
            stompClient.current.ws?.url?.includes('/ws/chat')
        ) {
            stompClient.current.disconnect(() => {
                console.log('🛑 기존 /ws/chat 연결 해제');
            });
        }

        fetchInitialData();

        return () => {
            if (
                stompClient.current &&
                stompClient.current.connected &&
                stompClient.current.ws?.url?.includes('/ws/chat')
            ) {
                stompClient.current.disconnect(() => {
                    console.log('🧹 ChatRoom unmount - /ws/chat 연결 해제');
                });
            }
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
                                    <div className={`message-time ${msg.isMe ? 'bottom-left' : 'left'}`}>
                                        {formattedTime}
                                    </div>
                                    {msg.unreadCount !== undefined && (
                                        <div className={`unread-count-number ${msg.isMe ? 'right' : 'left'}`}>
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
