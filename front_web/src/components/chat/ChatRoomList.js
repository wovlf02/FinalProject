import React, { useEffect, useState } from 'react';
import '../../css/Chat.css';
import { FaPlus } from 'react-icons/fa';
import api from '../../api/api';
import base_profile from '../../icons/base_profile.png';
import peopleIcon from '../../icons/people.png';

const ChatRoomList = ({ selectedRoomId, setSelectedRoomId, onOpenCreateModal, onSelectRoom }) => {
    const [chatRooms, setChatRooms] = useState([]);
    const [roomSearch, setRoomSearch] = useState('');

    useEffect(() => {
        fetchChatRooms();
    }, []);

    const fetchChatRooms = async () => {
        try {
            const res = await api.get('/chat/rooms/my');
            const rooms = res.data?.data || [];
            console.log(rooms);

            const mappedRooms = rooms.map(room => ({
                roomId: room.room_id,
                roomName: room.room_name,
                roomType: room.room_type,
                profileImageUrl: room.profile_image_url
                    ? `http://localhost:8080${room.profile_image_url}`
                    : '',
                participantCount: room.participant_count,
                totalMessageCount: room.total_message_count,
                unreadCount: room.unread_count,
                lastMessage: room.last_message,
                lastMessageAt: room.last_message_at,
            }));

            setChatRooms(mappedRooms);
            if (mappedRooms.length > 0 && !selectedRoomId) {
                const defaultRoomId = mappedRooms[0].roomId;
                setSelectedRoomId(defaultRoomId);
                if (onSelectRoom) onSelectRoom(defaultRoomId); // ✅ 초기 선택 방 전달
            }
        } catch (err) {
            console.error('❌ 채팅방 목록 불러오기 실패:', err);
            setChatRooms([]);
        }
    };

    const handleRoomClick = (roomId) => {
        console.log('🖱️ 채팅방 클릭:', roomId);

        // ✅ Chat.jsx 쪽 상태 업데이트 위임
        if (onSelectRoom) {
            onSelectRoom(roomId);
        }

        // ✅ 클릭된 방의 뱃지를 제거
        setChatRooms(prevRooms =>
            prevRooms.map(room =>
                room.roomId === roomId
                    ? { ...room, unreadCount: 0 }
                    : room
            )
        );
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
        <div className="chat-room-list-panel">
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
                            className={`chat-room-item ${room.roomId === selectedRoomId ? 'selected' : ''}`}
                            onClick={() => handleRoomClick(room.roomId)}
                        >
                            <img
                                src={room.profileImageUrl || base_profile}
                                alt={room.roomName}
                                onError={(e) => { e.target.src = base_profile; }}
                            />
                            <div className="chat-room-info">
                                <div className="chat-room-top">
                                    <span className="chat-room-name">
                                        {room.roomName}
                                        <span className="chat-room-participants">
                                            <img src={peopleIcon} alt="참여자" className="participants-icon" />
                                            {room.participantCount}
                                        </span>
                                    </span>
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
    );
};

export default ChatRoomList;
