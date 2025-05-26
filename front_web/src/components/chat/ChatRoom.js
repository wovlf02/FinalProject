import React, { useEffect, useRef, useState } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import moment from 'moment';
import api from '../../api/api';
import base_profile from '../../icons/base_profile.png';
import { FaPaperPlane, FaSmile, FaPaperclip, FaMicrophone, FaSignOutAlt } from 'react-icons/fa';
import '../../css/ChatRoom.css';

const getProfileUrl = (url) => {
  if (!url || url === "" || url === "프로필 사진이 없습니다") return base_profile;
  if (url.startsWith('C:\\FinalProject')) {
    const webUrl = url.replace('C:\\FinalProject', '').replace(/\\/g, '/');
    return `http://localhost:8080${webUrl}`;
  }
  if (url.startsWith('http')) return url;
  if (url.startsWith('/uploads')) return `http://localhost:8080${url}`;
  return base_profile;
};

const ChatRoom = ({ roomId, onReadAllMessages }) => {
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState([]);
  const [user, setUser] = useState(null);
  const [roomInfo, setRoomInfo] = useState(null);
  const [participants, setParticipants] = useState([]);
  const scrollRef = useRef(null);
  const stompClient = useRef(null);
  const userRef = useRef(null);
  const hasEnteredRef = useRef(false);

  useEffect(() => {
    api.get('/users/me', { withCredentials: true }).then(res => {
      setUser(res.data?.data);
      userRef.current = res.data?.data;
    });
  }, []);

  useEffect(() => {
    if (!roomId) return;
    setMessages([]);
    setRoomInfo(null);
    setParticipants([]);
    hasEnteredRef.current = false;
    if (
      stompClient.current &&
      stompClient.current.connected &&
      stompClient.current.ws?.url?.includes('/ws/chat')
    ) {
      stompClient.current.disconnect(() => {});
    }
    fetchInitialData();
    return () => {
      if (
        stompClient.current &&
        stompClient.current.connected &&
        stompClient.current.ws?.url?.includes('/ws/chat')
      ) {
        stompClient.current.disconnect(() => {});
      }
    };
    // eslint-disable-next-line
  }, [roomId]);

  const fetchInitialData = async () => {
    try {
      const detailRes = await api.post('/chat/rooms/detail', { room_id: roomId }, { withCredentials: true });
      const data = detailRes.data?.data;
      const room = data.room_info;
      const msgs = data.messages;
      setRoomInfo(room);
      setParticipants(room.participants || []);
      const normalizedMessages = (msgs || []).map(msg => {
        const senderId = msg.sender_id;
        let profileUrl = msg.profile_url || '';
        profileUrl = getProfileUrl(profileUrl);
        return {
          ...msg,
          senderId,
          isMe: userRef.current && String(senderId) === String(userRef.current.user_id),
          profileUrl,
          nickname: msg.nickname || '알 수 없음',
          sentAt: msg.sent_at || msg.time,
          unreadCount: msg.unread_count ?? 0,
        };
      });
      setMessages(normalizedMessages);
      connectStomp(userRef.current, normalizedMessages);
      if (onReadAllMessages) onReadAllMessages(roomId);
    } catch (err) {
      setMessages([]);
      setRoomInfo(null);
      setParticipants([]);
    }
  };

  const connectStomp = (userData, loadedMessages) => {
    const socket = new SockJS('http://localhost:8080/ws/chat');
    const client = Stomp.over(socket);
    stompClient.current = client;
    client.connect({}, () => {
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
        const senderId = message.sender_id;
        const isMe = userRef.current && String(senderId) === String(userRef.current.user_id);
        let profileUrl = message.profile_url || '';
        profileUrl = getProfileUrl(profileUrl);
        const normalizedMsg = {
          messageId: message.message_id,
          roomId: message.room_id,
          senderId,
          isMe,
          profileUrl,
          nickname: message.nickname || '알 수 없음',
          content: message.content,
          type: message.type,
          storedFileName: message.stored_file_name ?? null,
          sentAt: message.sent_at || message.time || new Date(),
          unreadCount: message.unread_count ?? 0,
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
        const messageId = msg.message_id;
        if (userRef.current && String(msg.sender_id) !== String(userRef.current.user_id) && messageId) {
          const initReadPayload = {
            type: 'READ',
            room_id: roomId,
            message_id: messageId,
          };
          client.send('/pub/chat/read', {}, JSON.stringify(initReadPayload));
        }
      });
    });
  };

  const scrollToBottom = () => {
    scrollRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

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

  const handleLeaveRoom = async () => {
    if (!window.confirm('정말 이 채팅방에서 나가시겠습니까?')) return;
    try {
      await api.delete(`/chat/rooms/${roomId}`);
      // 나가기 후 처리
    } catch (e) {
      alert('채팅방 나가기 실패');
    }
  };

  if (!roomInfo || !user) return null;

  const others = participants.filter(p => String(p.user_id) !== String(user.user_id));
  const displayProfile = others.length === 1
    ? getProfileUrl(others[0]?.profile_image_url)
    : getProfileUrl(roomInfo.representative_image_url);

  return (
    <div className="chat-room modern-chat">
      <div className="modern-chat-header">
        <img
          src={displayProfile}
          alt="채팅방 대표/상대방 프로필"
          className="modern-chat-profile"
          onError={e => { e.currentTarget.src = base_profile; }}
        />
        <div className="modern-chat-header-info">
          <h4>
            {roomInfo.room_name}
            <span className="modern-chat-count">({participants.length}명)</span>
          </h4>
        </div>
        <button
          className="modern-chat-leave-btn"
          onClick={handleLeaveRoom}
          title="채팅방 나가기"
        >
          <FaSignOutAlt style={{ marginRight: 4 }} /> 나가기
        </button>
      </div>
      <div className="modern-chat-body">
        {messages
          .filter(msg => msg.type !== 'ENTER')
          .map((msg, index) => {
            const formattedTime = moment(msg.sentAt).format('A hh:mm');
            const isUnread = !msg.isMe && msg.unreadCount > 0;
            return msg.isMe ? (
              <div key={index} className="modern-message-wrapper right">
                <div className="modern-message-bubble-group right">
                  <div className="modern-message-bubble me">
                    {msg.content}
                    <span className="modern-message-time">{formattedTime}</span>
                  </div>
                  {msg.unreadCount > 0 && (
                    <span className="modern-message-badge">{msg.unreadCount}</span>
                  )}
                </div>
              </div>
            ) : (
              <div key={index} className={`modern-message-wrapper left ${isUnread ? 'unread' : ''}`}>
                <div className="modern-message-header">
                  <img
                    src={msg.profileUrl}
                    alt="profile"
                    className="modern-message-avatar"
                    onError={e => { e.currentTarget.src = base_profile; }}
                  />
                  <span className="modern-message-nickname">{msg.nickname}</span>
                </div>
                <div className="modern-message-bubble-group left">
                  <div className="modern-message-bubble">
                    {msg.content}
                    <span className="modern-message-time">{formattedTime}</span>
                  </div>
                  {msg.unreadCount > 0 && (
                    <span className="modern-message-badge">{msg.unreadCount}</span>
                  )}
                </div>
              </div>
            );
          })}
        <div ref={scrollRef} />
      </div>
      <div className="modern-chat-input">
        <FaSmile className="input-icon" />
        <label className="input-icon">
          <FaPaperclip />
          <input type="file" style={{ display: 'none' }} />
        </label>
        <FaMicrophone className="input-icon" />
        <input
          type="text"
          placeholder="메시지를 입력하세요..."
          value={message}
          onChange={e => setMessage(e.target.value)}
          onKeyDown={e => { if (e.key === 'Enter') handleSend(); }}
        />
        <button onClick={handleSend} className="modern-send-btn">
          <FaPaperPlane />
        </button>
      </div>
    </div>
  );
};

export default ChatRoom;
