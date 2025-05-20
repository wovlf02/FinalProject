import React, { useState, useEffect } from 'react';
import '../../css/Chat.css';
import ChatRoom from './ChatRoom';
import { FaPlus } from 'react-icons/fa';
import CreateGroupModal from './CreateGroupModal';
import ChatFriendList from '../../components/chat/ChatFriendList';
import api from '../../api/api';
import base_profile from '../../icons/base_profile.png';

const Chat = () => {
    const [chatRooms, setChatRooms] = useState([]);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [friends, setFriends] = useState([]);
    const [friendSearch, setFriendSearch] = useState('');
    const [roomSearch, setRoomSearch] = useState('');
    const [selectedRoomId, setSelectedRoomId] = useState(null); // ✅ 상태 관리 추가

    useEffect(() => {
        fetchChatRooms();
        fetchFriends();
    }, []);

    const fetchChatRooms = async () => {
        try {
            const res = await api.get('/chat/rooms');
            const rooms = Array.isArray(res.data?.data) ? res.data.data : [];
            setChatRooms(rooms);
            if (rooms.length > 0 && !selectedRoomId) {
                setSelectedRoomId(rooms[0].roomId); // ✅ 첫 채팅방 자동 선택
            }
        } catch (err) {
            console.error('❌ 채팅방 목록 불러오기 실패:', err);
            setChatRooms([]);
        }
    };

    const fetchFriends = async () => {
        try {
            const res = await api.get('/friends');
            const mapped = res.data?.friends.map(f => ({
                id: f.userId,
                name: f.nickname,
                email: f.email,
                avatar: f.profileImageUrl || base_profile,
            })) || [];
            setFriends(mapped);
        } catch (err) {
            console.error('❌ 친구 목록 불러오기 실패:', err);
        }
    };

    const handleRoomClick = (roomId) => {
        setSelectedRoomId(roomId); // ✅ 페이지 이동 X, 상태 변경으로
    };

    const getPreviewMessage = (msg) => {
        if (!msg) return '(아직 메시지 없음)';
        const lowered = msg.toLowerCase();
        if (
            lowered.startsWith('/uploads') ||
            lowered.endsWith('.jpg') ||
            lowered.endsWith('.png') ||
            lowered.endsWith('.pdf')
        ) return '[파일]';
        return msg;
    };

    const filteredRooms = chatRooms.filter(room =>
        room?.roomName?.toLowerCase().includes(roomSearch.toLowerCase())
    );

    return (
        <div className="chat-container">
            <div className="chat-main">
                {/* 친구 목록 */}
                <div className="chat-friend-list-panel">
                    <div className="chat-friend-header">
                        <h4>친구 목록</h4>
                        <input
                            type="text"
                            className="friend-search-input"
                            placeholder="닉네임 또는 이메일 검색"
                            value={friendSearch}
                            onChange={(e) => setFriendSearch(e.target.value)}
                        />
                    </div>
                    <ChatFriendList searchKeyword={friendSearch} />
                </div>

                {/* 채팅방 리스트 */}
                <div className="chat-room-list-panel">
                    <div className="chat-room-header-row top">
                        <h4>Messages</h4>
                        <button className="chat-create-btn" onClick={() => setShowCreateModal(true)}>
                            <FaPlus />
                        </button>
                    </div>
                    <div className="chat-room-search-row">
                        <input
                            type="text"
                            className="chat-room-search-input"
                            placeholder="채팅방 이름 검색"
                            value={roomSearch}
                            onChange={(e) => setRoomSearch(e.target.value)}
                        />
                    </div>

                    {filteredRooms.length === 0 ? (
                        <div className="friend-empty">채팅방이 없습니다.</div>
                    ) : (
                        filteredRooms
                            .sort((a, b) => (b.pinned ? 1 : 0) - (a.pinned ? 1 : 0))
                            .map(room => (
                                <div
                                    key={room.roomId}
                                    className="chat-room-item"
                                    onClick={() => handleRoomClick(room.roomId)}
                                >
                                    <img
                                        src={
                                            room.profileImageUrl
                                                ? `
                                                ${room.profileImageUrl}`
                                                : '/images/profile.png'
                                        }
                                        alt={room.roomName}
                                    />
                                    <div className="chat-room-info">
                                        <div className="chat-room-top">
                                            <span className="chat-room-name">{room.roomName}</span>
                                            <span className="chat-room-time">
                                                {room.lastMessageAt ? room.lastMessageAt.slice(11, 16) : ''}
                                            </span>
                                        </div>
                                        <div className="chat-room-bottom">
                                            <span className="chat-room-message">
                                                {getPreviewMessage(room.lastMessage)}
                                            </span>
                                            {room.unreadCount > 0 && (
                                                <span className="chat-room-badge">{room.unreadCount}</span>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            ))
                    )}
                </div>

                {/* 채팅창 */}
                <ChatRoom roomId={selectedRoomId} />
            </div>

            {/* 그룹채팅 생성 모달 */}
            {showCreateModal && (
                <CreateGroupModal
                    friends={friends}
                    onClose={() => setShowCreateModal(false)}
                    onCreate={(room) => {
                        if (room && room.roomId && room.roomName) {
                            setChatRooms(prev => [...prev, room]);
                            setSelectedRoomId(room.roomId); // ✅ 생성 직후 자동 진입
                        }
                        setShowCreateModal(false);
                    }}
                />
            )}
        </div>
    );
};

export default Chat;
