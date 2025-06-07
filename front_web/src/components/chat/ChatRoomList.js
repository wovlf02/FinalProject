import React, { useEffect, useState } from 'react';
import '../../css/ChatRoomList.css';
import { FaPlus, FaTrash } from 'react-icons/fa';
import api from '../../api/api';
import base_profile from '../../icons/base_profile.png';

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

const ChatRoomList = ({ selectedRoomId, setSelectedRoomId, onOpenCreateModal, onSelectRoom }) => {
    const [chatRooms, setChatRooms] = useState([]);
    const [roomSearch, setRoomSearch] = useState('');
    const [myUserId, setMyUserId] = useState(null);

    useEffect(() => {
        api.get('/users/me', { withCredentials: true }).then(res => {
            setMyUserId(res.data?.data?.user_id);
        });
    }, []);

    useEffect(() => {
        fetchChatRooms();
    }, []);

    const fetchChatRooms = async () => {
        try {
            const res = await api.get('/chat/rooms/my');
            const rooms = res.data?.data || [];
            const mappedRooms = rooms.map(room => ({
                roomId: room.room_id,
                roomName: room.room_name,
                roomType: room.room_type,
                profileImageUrl: room.profile_image_url,
                participantCount: room.participant_count,
                unreadCount: room.unread_count,
                lastMessage: room.last_message,
                lastMessageAt: room.last_message_at,
                participants: room.participants,
            }));
            setChatRooms(mappedRooms);
            if (mappedRooms.length > 0 && !selectedRoomId) {
                const defaultRoomId = mappedRooms[0].roomId;
                setSelectedRoomId(defaultRoomId);
                if (onSelectRoom) onSelectRoom(defaultRoomId);
            }
        } catch (err) {
            setChatRooms([]);
        }
    };

    const handleRoomClick = (roomId) => {
        if (onSelectRoom) onSelectRoom(roomId);
        setChatRooms(prevRooms =>
            prevRooms.map(room =>
                room.roomId === roomId
                    ? { ...room, unreadCount: 0 }
                    : room
            )
        );
    };

    const handleDeleteRoom = async (roomId) => {
        if (!window.confirm('정말 이 채팅방을 삭제하시겠습니까?')) return;
        try {
            await api.delete(`/chat/rooms/${roomId}`);
            setChatRooms(prev => prev.filter(room => room.roomId !== roomId));
            if (selectedRoomId === roomId) setSelectedRoomId(null);
        } catch (err) {
            alert('채팅방 삭제 실패');
        }
    };

    const getRoomProfileAndName = (room) => {
        if (!room.participants || !myUserId) {
            return {
                name: room.roomName,
                profile: getProfileUrl(room.profileImageUrl),
                count: room.participantCount
            };
        }
        const others = room.participants.filter(p => String(p.user_id) !== String(myUserId));
        if (room.roomType === 'DIRECT' && others.length === 1) {
            return {
                name: others[0].nickname,
                profile: getProfileUrl(others[0].profile_image_url),
                count: room.participants.length
            };
        }
        return {
            name: room.roomName,
            profile: getProfileUrl(room.profileImageUrl),
            count: room.participants.length
        };
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
        getRoomProfileAndName(room).name.toLowerCase().includes(roomSearch.toLowerCase())
    );

    return (
        <div className="chat-room-list-panel modern">
            <div className="chat-room-header-row top">
                <h4>Messages</h4>
                <button className="chat-create-btn" onClick={onOpenCreateModal}>
                    <FaPlus />
                </button>
            </div>
            <div className="chat-room-search-row">
                <input
                    type="text"
                    className="chat-room-search-input"
                    placeholder="채팅방/대화상대 검색"
                    value={roomSearch}
                    onChange={(e) => setRoomSearch(e.target.value)}
                />
            </div>
            {filteredRooms.length === 0 ? (
                <div className="friend-empty">채팅방이 없습니다.</div>
            ) : (
                filteredRooms.map(room => {
                    const { name, profile, count } = getRoomProfileAndName(room);
                    return (
                        <div
                            key={room.roomId}
                            className={`chat-room-item modern-card ${room.roomId === selectedRoomId ? 'selected' : ''}`}
                            onClick={() => handleRoomClick(room.roomId)}
                        >
                            <img
                                src={profile}
                                alt={name}
                                className="modern-profile"
                                onError={(e) => { e.target.src = base_profile; }}
                            />
                            <div className="modern-info">
                                <div className="modern-top">
                                    <span className="modern-name">
                                        {name}
                                        <span className="modern-count">({count}명)</span>
                                    </span>
                                    <span className="modern-time">
                                        {room.lastMessageAt ? room.lastMessageAt.slice(11, 16) : ''}
                                    </span>
                                    <button
                                        className="modern-delete-btn"
                                        title="채팅방 삭제"
                                        onClick={e => {
                                            e.stopPropagation();
                                            handleDeleteRoom(room.roomId);
                                        }}
                                    >
                                        <FaTrash />
                                    </button>
                                </div>
                                <div className="modern-bottom">
                                    <span className="modern-message">
                                        {getPreviewMessage(room.lastMessage)}
                                    </span>
                                    {room.unreadCount > 0 && (
                                        <span className="modern-badge">{room.unreadCount}</span>
                                    )}
                                </div>
                            </div>
                        </div>
                    );
                })
            )}
        </div>
    );
};

export default ChatRoomList;
