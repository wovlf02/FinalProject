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
    const userRef = useRef(null);
    const hasEnteredRef = useRef(false); // ✅ 최초 입장 여부 추적

    const scrollToBottom = () => {
        scrollRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    const fetchInitialData = async () => {
        try {
            const userRes = await api.get('/users/me', { withCredentials: true });
            const currentUser = userRes.data?.data;
            setUser(currentUser);
            userRef.current = currentUser;

            const detailRes = await api.post('/chat/rooms/detail', { room_id: roomId }, { withCredentials: true });
            const data = detailRes.data?.data;
            const room = data.room_info;
            const msgs = data.messages;

            setRoomInfo({
                ...room,
                profileImageUrl: room.representative_image_url
                    ? `https://4868-121-127-165-110.ngrok-free.app${room.representative_image_url}`
                    : base_profile,
                roomName: room.room_name,
                roomType: room.room_type,
                participantCount: room.participant_count
            });

            const normalizedMessages = (msgs || []).map(msg => {
                const senderId = msg.senderId ?? msg.sender_id;
                return {
                    ...msg,
                    senderId,
                    isMe: String(senderId) === String(currentUser.user_id),
                    profileUrl: msg.profileUrl
                        ? `https://4868-121-127-165-110.ngrok-free.app${msg.profileUrl}`
                        : base_profile,
                    nickname: msg.nickname || '알 수 없음',
                    sentAt: msg.sentAt || msg.time,
                    unreadCount: msg.unreadCount ?? msg.unread_count ?? 0,
                };
            });

            setMessages(normalizedMessages);
            connectStomp(currentUser, normalizedMessages);
        } catch (err) {
            console.error('❌ 초기 데이터 로딩 실패:', err.response?.data || err);
        }
    };

    useEffect(() => {
        if (!roomId) return;

        setMessages([]);
        setRoomInfo(null);
        setUser(null);
        hasEnteredRef.current = false; // ✅ 방 변경 시 초기화

        if (
            stompClient.current &&
            stompClient.current.connected &&
            stompClient.current.ws?.url?.includes('/ws/chat')
        ) {
            stompClient.current.disconnect(() => {
                console.log('🛑 기존 WebSocket 연결 해제됨');
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
                    console.log('🧹 ChatRoom 언마운트: WebSocket 연결 해제');
                });
            }
        };
    }, [roomId]);

    const connectStomp = (userData, loadedMessages) => {
        const socket = new SockJS('https://4868-121-127-165-110.ngrok-free.app/ws/chat');
        const client = Stomp.over(socket);
        stompClient.current = client;

        client.connect({}, () => {
            console.log('🔗 WebSocket 연결 완료');

            // ✅ 최초 입장 시에만 ENTER 메시지 전송
            if (!hasEnteredRef.current) {
                client.send('/pub/chat/send', {}, JSON.stringify({
                    room_id: roomId,
                    sender_id: userData.user_id,
                    type: 'ENTER',
                    content: `${userData.nickname}님이 입장하셨습니다.`,
                    time: new Date().toISOString()
                }));
                hasEnteredRef.current = true;
            }

            client.subscribe(`/sub/chat/room/${roomId}`, (msg) => {
                const message = JSON.parse(msg.body);
                const senderId = message.senderId ?? message.sender_id;
                const isMe = String(senderId) === String(userRef.current?.user_id);

                const normalizedMsg = {
                    messageId: message.messageId ?? message.message_id,
                    roomId: message.roomId ?? message.room_id,
                    senderId,
                    isMe,
                    profileUrl: message.profileUrl
                        ? `http://localhost:8080${message.profileUrl}`
                        : base_profile,
                    nickname: message.nickname || '알 수 없음',
                    content: message.content,
                    type: message.type,
                    storedFileName: message.storedFileName ?? message.stored_file_name ?? null,
                    sentAt: message.sentAt || message.time || new Date(),
                    unreadCount: message.unreadCount ?? message.unread_count ?? 0,
                };

                if (message.type === 'READ_ACK') {
                    setMessages(prev =>
                        prev.map(m =>
                            m.messageId === normalizedMsg.messageId
                                ? { ...m, unreadCount: normalizedMsg.unreadCount }
                                : m
                        )
                    );
                } else {
                    setMessages(prev => {
                        const exists = normalizedMsg.messageId && prev.some(m => m.messageId === normalizedMsg.messageId);
                        return exists ? prev : [...prev, normalizedMsg];
                    });

                    if (!isMe && normalizedMsg.messageId) {
                        const readPayload = {
                            type: 'READ',
                            roomId,
                            messageId: normalizedMsg.messageId,
                        };
                        client.send('/pub/chat/read', {}, JSON.stringify(readPayload));
                    }
                }

                setTimeout(scrollToBottom, 50);
            });

            loadedMessages.forEach(msg => {
                const messageId = msg.messageId ?? msg.message_id;
                if (String(msg.senderId) !== String(userRef.current?.user_id) && messageId) {
                    const initReadPayload = {
                        type: 'READ',
                        room_id: roomId,
                        message_id: messageId,
                    };
                    client.send('/pub/chat/read', {}, JSON.stringify(initReadPayload));
                }
            });
            onReadAllMessages(roomId);
        });
    };

    const handleSend = () => {
        const currentUser = userRef.current;
        if (!message.trim() || !currentUser || !stompClient.current?.connected) {
            return;
        }

        const payload = {
            room_id: roomId,
            sender_id: currentUser.user_id,
            type: 'TEXT',
            content: message,
            storedFileName: null,
        };

        stompClient.current.send('/pub/chat/send', {}, JSON.stringify(payload));
        setMessage('');
        setTimeout(scrollToBottom, 50);
    };

    if (!roomId || !user || !roomInfo) {
        return <div className="chat-room-empty">채팅방을 선택해주세요.</div>;
    }

    return (
        <div className="chat-room">
            <div className="chat-room-header">
                <img src={roomInfo.profileImageUrl} alt="room" className="chat-room-profile" onError={(e) => { e.target.src = base_profile; }} />
                <div className="chat-room-header-info">
                    <h4>{roomInfo.roomName}</h4>
                    <span className="status">{roomInfo.roomType === 'GROUP' ? '그룹채팅' : '1:1 채팅'}</span>
                </div>
            </div>

            <div className="chat-room-body">
                {messages
                    .filter((msg) => msg.type !== 'ENTER') // 👈 필터 추가
                    .map((msg, index) => {
                        const formattedTime = moment(msg.sentAt).format('A hh:mm');
                        const isUnread = !msg.isMe && msg.unreadCount > 0;

                        return msg.isMe ? (
                            <div key={index} className="message-wrapper right">
                                <div className="message-content-group right">
                                    <div className="message-bubble-wrapper right">
                                        {msg.unreadCount > 0 && (
                                            <div className="unread-count-number top-right">{msg.unreadCount}</div>
                                        )}
                                        <div className="message-bubble me">{msg.content}</div>
                                        <div className="message-time bottom-left">{formattedTime}</div>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div key={index} className={`message-wrapper left ${isUnread ? 'unread' : ''}`}>
                                <div className="message-header">
                                    <img src={msg.profile_url } alt="profile" className="message-avatar" onError={(e) => { e.target.src = base_profile; }} />
                                    <div className="message-nickname">{msg.nickname}</div>
                                </div>
                                <div className="message-content-group left">
                                    <div className="message-bubble-wrapper left">
                                        {msg.unreadCount > 0 && (
                                            <div className="unread-count-number top-right">{msg.unreadCount}</div>
                                        )}
                                        <div className="message-bubble">{msg.content}</div>
                                        <div className="message-time bottom-left">{formattedTime}</div>
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
