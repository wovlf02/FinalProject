import React, { useEffect, useState } from 'react';
import '../../css/ChatFriendList.css'; // 커스터마이징된 스타일 포함
import api from '../../api/api';

const ChatFriendList = ({ searchKeyword = '' }) => {
    const [friends, setFriends] = useState([]);

    useEffect(() => {
        fetchFriends();
    }, []);

    const fetchFriends = async () => {
        try {
            const res = await api.get('/friends');
            setFriends(res.data?.friends || []);
        } catch (err) {
            console.error('❌ 친구 목록 불러오기 실패:', err);
        }
    };

    const handleStartChat = async (friendId) => {
        try {
            const res = await api.post('/chat/rooms', {
                name: null,
                isPrivate: true,
                targetUserId: friendId,
            });
            window.location.href = `/community/chat/${res.data.roomId}`;
        } catch (err) {
            alert('채팅방 생성 실패');
            console.error('❌ 채팅방 생성 실패:', err);
        }
    };

    const filteredFriends = friends.filter(friend =>
        friend.nickname?.toLowerCase().includes(searchKeyword.toLowerCase()) ||
        friend.email?.toLowerCase().includes(searchKeyword.toLowerCase())
    );

    return (
        <div className="chat-friend-list-panel">
            {filteredFriends.length === 0 ? (
                <div className="friend-empty">친구가 없습니다.</div>
            ) : (
                filteredFriends.map(friend => (
                    <div key={friend.userId} className="chat-friend-card">
                        <div className="chat-friend-content">
                            <img
                                src={friend.profileImageUrl || '/images/base_profile.png'}
                                alt={friend.nickname}
                                className="chat-friend-profile"
                            />
                            <div className="chat-friend-info">
                                <div className="chat-friend-nickname">{friend.nickname}</div>
                                <div className="chat-friend-email">{friend.email}</div>
                                <div className={`chat-friend-status ${friend.online ? 'online' : 'offline'}`}>
                                    <span className="chat-status-dot"></span>
                                    {friend.online ? '온라인' : '오프라인'}
                                </div>
                            </div>
                        </div>
                        <button className="chat-friend-button" onClick={() => handleStartChat(friend.userId)}>
                            채팅 시작
                        </button>
                    </div>
                ))
            )}
        </div>
    );
};

export default ChatFriendList;
