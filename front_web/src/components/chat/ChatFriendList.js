import React, { useEffect, useState } from 'react';
import '../../css/ChatFriendList.css';
import base_profile from '../../icons/base_profile.png';

const ChatFriendList = () => {
    const [friends, setFriends] = useState([]);
    const [searchKeyword, setSearchKeyword] = useState('');

    useEffect(() => {
        fetchFriends();
    }, []);

    const fetchFriends = async () => {
        try {
            const res = await fetch('http://localhost:8080/api/friends/list', {
                method: 'GET',
                credentials: 'include'
            });
            const data = await res.json();

            const online = (data.online_friends || []).map(f => ({
                id: f.user_id,
                name: f.nickname,
                email: f.email || '',
                avatar: f.profile_image_url ? `http://localhost:8080${f.profile_image_url}` : base_profile,
                online: true
            }));

            const offline = (data.offline_friends || []).map(f => ({
                id: f.user_id,
                name: f.nickname,
                email: f.email || '',
                avatar: f.profile_image_url ? `http://localhost:8080${f.profile_image_url}` : base_profile,
                online: false
            }));

            setFriends([...online, ...offline]);
        } catch (err) {
            console.error('❌ 친구 목록 불러오기 실패:', err);
        }
    };

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

    const filteredFriends = friends.filter(friend =>
        friend.name?.toLowerCase().includes(searchKeyword.toLowerCase()) ||
        friend.email?.toLowerCase().includes(searchKeyword.toLowerCase())
    );

    return (
        <>
            <div className="chat-friend-header">
                <h4>친구 목록</h4>
                <input
                    type="text"
                    className="friend-search-input"
                    placeholder="닉네임 또는 이메일 검색"
                    value={searchKeyword}
                    onChange={(e) => setSearchKeyword(e.target.value)}
                />
            </div>

            <div className="chat-friend-scroll">
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
                                    onError={(e) => { e.target.src = base_profile; }}
                                />
                                <div className="chat-friend-info">
                                    <div className="chat-friend-nickname">{friend.name}</div>
                                    <div className="chat-friend-email">{friend.email}</div>
                                    <div className={`chat-friend-status ${friend.online ? 'online' : 'offline'}`}>
                                        <span className="chat-status-dot"></span>
                                        {friend.online ? '온라인' : '오프라인'}
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
        </>
    );
};

export default ChatFriendList;
