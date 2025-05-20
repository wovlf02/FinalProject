import React from 'react';
import '../../css/ChatFriendList.css';
import base_profile from '../../icons/base_profile.png'; // ✅ fallback 이미지 import

const ChatFriendList = ({ searchKeyword = '', friends = [] }) => {
    const filteredFriends = friends.filter(friend =>
        friend.name?.toLowerCase().includes(searchKeyword.toLowerCase()) ||
        friend.email?.toLowerCase().includes(searchKeyword.toLowerCase())
    );

    const handleStartChat = async (friendId) => {
        try {
            const res = await fetch('http://localhost:8080/api/chat/rooms', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    name: null,
                    isPrivate: true,
                    targetUserId: friendId,
                }),
            });
            const data = await res.json();
            window.location.href = `/community/chat/${data.roomId}`;
        } catch (err) {
            alert('채팅방 생성 실패');
            console.error('❌ 채팅방 생성 실패:', err);
        }
    };

    return (
        <div className="chat-friend-list-panel">
            {filteredFriends.length === 0 ? (
                <div className="friend-empty">친구가 없습니다.</div>
            ) : (
                filteredFriends.map(friend => (
                    <div key={friend.id} className="chat-friend-card">
                        <div className="chat-friend-content">
                            <img
                                src={friend.avatar}
                                alt={friend.name}
                                className="chat-friend-profile"
                                onError={(e) => { e.target.src = base_profile; }} // ✅ fallback 이미지 처리
                            />
                            <div className="chat-friend-info">
                                <div className="chat-friend-nickname">{friend.name}</div>
                                <div className="chat-friend-email">{friend.email}</div>
                                <div className="chat-friend-status offline">
                                    <span className="chat-status-dot"></span>
                                    오프라인
                                </div>
                            </div>
                        </div>
                        <button className="chat-friend-button" onClick={() => handleStartChat(friend.id)}>
                            채팅 시작
                        </button>
                    </div>
                ))
            )}
        </div>
    );
};

export default ChatFriendList;
