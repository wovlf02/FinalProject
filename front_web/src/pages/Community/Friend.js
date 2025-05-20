// Friend.js
import React, { useEffect, useState } from 'react';
import '../../css/Friend.css';
import FriendCard from '../../components/friend/FriendCard';
import api from '../../api/api';

const Friend = () => {
    const [friends, setFriends] = useState([]);
    const [requests, setRequests] = useState([]);
    const [blocked, setBlocked] = useState([]);
    const [searchResults, setSearchResults] = useState([]);
    const [searchInput, setSearchInput] = useState('');

    useEffect(() => {
        fetchAll();
    }, []);

    const fetchAll = async () => {
        try {
            const [res1, res2, res3] = await Promise.all([
                api.get('/friends'),
                api.get('/friends/requests'),
                api.get('/friends/blocked'),
            ]);
            setFriends(res1.data.friends);
            setRequests(res2.data.requests);
            setBlocked(res3.data.blockedUsers || res3.data.blocked); // 백엔드 명칭 유연 처리
        } catch (e) {
            console.error('친구 목록 불러오기 실패:', e);
        }
    };

    const handleSearch = async () => {
        if (!searchInput.trim()) return;
        try {
            const res = await api.get(`/friends/search?nickname=${searchInput.trim()}`);
            setSearchResults(res.data.results);
        } catch (e) {
            alert('검색 실패');
        }
    };

    const updateSearchStatus = (userId, field) => {
        setSearchResults(prev =>
            prev.map(u =>
                u.userId === userId ? { ...u, [field]: true } : u
            )
        );
    };

    return (
        <div className="friend-page-container">
            {/* 사용자 검색 */}
            <div className="friend-column">
                <h3>사용자 검색</h3>
                <div style={{ display: 'flex', marginBottom: '16px' }}>
                    <input
                        className="search-input"
                        type="text"
                        placeholder="닉네임 또는 이메일 입력"
                        value={searchInput}
                        onChange={(e) => setSearchInput(e.target.value)}
                        onKeyDown={(e) => {
                            if(e.key === 'Enter') handleSearch();
                        }}
                    />
                    <button className="friend-btn chat-btn" onClick={handleSearch}>
                        검색
                    </button>
                </div>
                {searchResults.length === 0 ? (
                    <div className="friend-empty">검색 결과가 없습니다.</div>
                ) : (
                    searchResults.map(u => (
                        <FriendCard
                            key={u.userId}
                            user={u}
                            type="search"
                            onRequestSent={() => updateSearchStatus(u.userId, 'alreadyRequested')}
                        />
                    ))
                )}
            </div>

            {/* 친구 목록 */}
            <div className="friend-column">
                <h3>친구 목록</h3>
                {friends.length === 0 ? (
                    <div className="friend-empty">친구가 없습니다.</div>
                ) : (
                    friends.map(friend => (
                        <FriendCard
                            key={friend.userId}
                            user={friend}
                            type="friend"
                            onDelete={async (u) => {
                                await api.delete(`/friends/${u.userId}`);
                                setFriends(prev => prev.filter(f => f.userId !== u.userId));
                            }}
                            onStartChat={async (u) => {
                                try {
                                    const res = await api.post('/chat/rooms', {
                                        name: null,
                                        isPrivate: true,
                                        targetUserId: u.userId,
                                    });
                                    window.location.href = `/chat/room/${res.data.roomId}`;
                                } catch {
                                    alert('채팅방 생성 실패');
                                }
                            }}
                            onBlock={async (u) => {
                                await api.post(`/friends/block/${u.userId}`);
                                fetchAll();
                            }}
                        />
                    ))
                )}
            </div>

            {/* 요청 목록 */}
            <div className="friend-column">
                <h3>요청 목록</h3>
                {requests.length === 0 ? (
                    <div className="friend-empty">요청이 없습니다.</div>
                ) : (
                    requests.map(r => (
                        <FriendCard
                            key={r.requestId}
                            user={r}
                            type="received"
                            onAccept={async (u) => {
                                await api.post(`/friends/request/${u.requestId}/accept`, {
                                    requestId: u.senderId,
                                });
                                fetchAll();
                            }}
                            onReject={async (u) => {
                                await api.post(`/friends/request/${u.requestId}/reject`, {
                                    receiverId: u.senderId,
                                });
                                fetchAll();
                            }}
                        />
                    ))
                )}
            </div>

            {/* 차단 목록 */}
            <div className="friend-column">
                <h3>차단 목록</h3>
                {blocked.length === 0 ? (
                    <div className="friend-empty">차단한 사용자가 없습니다.</div>
                ) : (
                    blocked.map(u => (
                        <FriendCard
                            key={u.userId}
                            user={u}
                            type="blocked"
                            onUnblock={async (target) => {
                                await api.delete(`/friends/block/${target.userId}`);
                                fetchAll();
                            }}
                        />
                    ))
                )}
            </div>
        </div>
    );
};

export default Friend;
