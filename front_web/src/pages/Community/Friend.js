import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../css/Friend.css';
import api from '../../api/api';
import base_profile from '../../icons/base_profile.png';

const Friend = () => {
    const navigate = useNavigate();

    const [friendRequests, setFriendRequests] = useState([]);
    const [onlineFriends, setOnlineFriends] = useState([]);
    const [offlineFriends, setOfflineFriends] = useState([]);
    const [blockedFriends, setBlockedFriends] = useState([]);

    const [search, setSearch] = useState('');
    const [showAddModal, setShowAddModal] = useState(false);
    const [showFilter, setShowFilter] = useState(false);
    const [filterType, setFilterType] = useState('전체');
    const [showMoreId, setShowMoreId] = useState(null);
    const [showBlockManager, setShowBlockManager] = useState(false);

    const [addName, setAddName] = useState('');
    const [addMsg, setAddMsg] = useState('');
    const [searchResults, setSearchResults] = useState([]);

    useEffect(() => {
        fetchFriendList();
        fetchRequests();
        fetchBlocked();
    }, []);

    // 📌 친구 목록 조회
    const fetchFriendList = async () => {
        try {
            const res = await api.get('/friends/list');
            console.log("친구 목록 ", res.data);
            setOnlineFriends(res.data.online_friends || []);
            setOfflineFriends(res.data.offline_friends || []);
        } catch (err) {
            console.error('❌ 친구 목록 조회 실패:', err);
        }
    };

// 📌 친구 요청 목록 조회
    const fetchRequests = async () => {
        try {
            const res = await api.get('/friends/requests');
            console.log('✅ 실제 요청 목록 응답:', res.data);
            setFriendRequests(res.data.requests || []);
        } catch (err) {
            console.error('❌ 요청 목록 조회 실패:', err);
        }
    };

// 📌 차단 목록 조회
    const fetchBlocked = async () => {
        try {
            const res = await api.get('/friends/blocked');
            setBlockedFriends(res.data.blocked_users || []);
            console.log("차단목록", res.data.blocked_users);
        } catch (err) {
            console.error('❌ 차단 목록 조회 실패:', err);
        }
    };

// 📌 친구 요청 수락
    const handleAccept = async (request_id) => {
        try {
            await api.post('/friends/request/accept', { request_id });
            setFriendRequests(prev => prev.filter(req => req.request_id !== request_id));
            fetchFriendList();
        } catch (err) {
            console.error('❌ 요청 수락 실패:', err);
        }
    };

// 📌 친구 요청 거절
    const handleReject = async (request_id) => {
        try {
            await api.post('/friends/request/reject', { request_id });
            setFriendRequests(prev => prev.filter(req => req.request_id !== request_id));
        } catch (err) {
            console.error('❌ 요청 거절 실패:', err);
        }
    };

// 📌 친구 검색
    const searchUsersByNickname = async (nickname) => {
        if (!nickname.trim()) {
            setSearchResults([]);
            return;
        }
        try {
            const res = await api.post('/friends/search', { nickname });
            setSearchResults(res.data.results || []);
        } catch (err) {
            console.error('❌ 친구 검색 실패:', err);
        }
    };

// 📌 친구 요청 전송
    const handleAddFriend = async (target_user_id) => {
        try {
            await api.post('/friends/request', { target_user_id });
            alert('친구 요청이 전송되었습니다.');
            setAddName('');
            setAddMsg('');
            setSearchResults([]);
            setShowAddModal(false);
            fetchRequests();
        } catch (err) {
            console.error('❌ 친구 요청 전송 실패:', err);
        }
    };

// 📌 친구 삭제
    const handleRemoveFriend = async (user_id) => {
        try {
            await api.post('/friends/delete', { target_user_id: user_id });
            setOnlineFriends(prev => prev.filter(f => f.user_id !== user_id));
            setOfflineFriends(prev => prev.filter(f => f.user_id !== user_id));
        } catch (err) {
            console.error('❌ 친구 삭제 실패:', err);
        }
    };

// 📌 친구 차단
    const handleBlockFriend = async (user_id) => {
        try {
            await api.post('/friends/block', { target_user_id: user_id });
            setOnlineFriends(prev => prev.filter(f => f.user_id !== user_id));
            setOfflineFriends(prev => prev.filter(f => f.user_id !== user_id));
            fetchBlocked();
        } catch (err) {
            console.error('❌ 친구 차단 실패:', err);
        }
    };

// 📌 차단 해제
    const handleUnblock = async (user_id) => {
        try {
            await api.post('/friends/unblock', { target_user_id: user_id });
            setBlockedFriends(prev => prev.filter(f => f.user_id !== user_id));
            fetchFriendList();
        } catch (err) {
            console.error('❌ 차단 해제 실패:', err);
        }
    };

// 📌 필터 조건
    // 🔍 검색어 기반 필터링 함수 (nickname, name에 대해)
    const searchFilter = (f) =>
        !search || (f.nickname || f.name || '').toLowerCase().includes(search.toLowerCase());

// ✅ 조건 분기된 리스트 필터링
    const filteredOnline = (filterType === '전체' || filterType === '온라인')
        ? onlineFriends.filter(searchFilter)
        : [];

    const filteredOffline = (filterType === '전체' || filterType === '오프라인')
        ? offlineFriends.filter(searchFilter)
        : [];

    const filteredRequests = friendRequests.filter(searchFilter);



    return (
        <div className="friend-root">
            {/* 헤더 */}
            <div className="friend-header">
                <h2>친구</h2>
                <div className="friend-header-actions">
                    <button className="friend-block-manager-btn" onClick={() => setShowBlockManager(true)}>차단 관리</button>
                    <button className="friend-join-btn" onClick={() => navigate('/community')}>커뮤니티로 돌아가기</button>
                </div>
            </div>

            {/* 검색 & 필터 */}
            <div className="friend-search-row">
                <input
                    className="friend-search"
                    placeholder="친구 검색..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                />
                <div className="friend-search-actions">
                    <button className="friend-add-btn" onClick={() => setShowAddModal(true)}>친구 추가</button>
                    <button className="friend-filter-btn" onClick={() => setShowFilter((v) => !v)}>
                        <span className="friend-filter-icon">☰</span> 필터
                    </button>
                    {showFilter && (
                        <div className="friend-filter-dropdown">
                            {['전체', '온라인', '오프라인'].map((type) => (
                                <div
                                    key={type}
                                    className={filterType === type ? 'active' : ''}
                                    onClick={() => {
                                        setFilterType(type);
                                        setShowFilter(false);
                                    }}
                                >
                                    {type}
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {/* 친구 추가 모달 */}
            {showAddModal && (
                <div className="friend-modal-overlay">
                    <div className="friend-modal">
                        <button className="friend-modal-close" onClick={() => setShowAddModal(false)}>×</button>
                        <h3>친구 추가</h3>
                        <div className="friend-search-form">
                            <input
                                placeholder="닉네임으로 검색"
                                value={addName}
                                onChange={(e) => setAddName(e.target.value)}
                            />
                            <button className="friend-search-btn" onClick={() => searchUsersByNickname(addName)}>검색</button>
                        </div>
                        <div className="friend-search-result-list">
                            {searchResults.map((user) => (
                                <div key={user.user_id} className="friend-row">
                                    <img
                                        src={user.profile_image_url || base_profile}
                                        alt={`${user.nickname}의 프로필 이미지`}
                                        className="friend-avatar"
                                    />
                                    <div className="friend-info">
                                        <div className="friend-name">{user.nickname}</div>
                                    </div>
                                    <button
                                        className="friend-accept"
                                        onClick={() => handleAddFriend(user.user_id)}
                                        disabled={user.already_friend || user.already_requested || user.blocked}
                                    >
                                        {user.already_friend
                                            ? '이미 친구'
                                            : user.already_requested
                                                ? '요청 보냄'
                                                : user.blocked
                                                    ? '차단됨'
                                                    : '요청'}
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}

            {/* 차단 관리 모달 */}
            {showBlockManager && (
                <div className="friend-modal-overlay">
                    <div className="friend-modal">
                        <button className="friend-modal-close" onClick={() => setShowBlockManager(false)}>×</button>
                        <h3>차단한 친구 관리</h3>
                        {blockedFriends.length === 0 ? (
                            <div className="friend-empty" style={{ padding: '18px 0' }}>차단한 친구가 없습니다.</div>
                        ) : (
                            blockedFriends.map((f) => (
                                <div className="friend-row" key={f.user_id}>
                                    <img src={f.profile_image_url || base_profile} alt={f.nickname} className="friend-avatar" />
                                    <div className="friend-info">
                                        <div className="friend-name">{f.nickname}</div>
                                    </div>
                                    <button className="friend-accept" onClick={() => handleUnblock(f.user_id)}>차단 해제</button>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            )}

            {/* 친구 요청 */}
            <div className="friend-section">
                <div className="friend-section-title">
                    친구 요청 <span className="friend-section-count">{friendRequests.length}개</span>
                </div>
                {friendRequests.length === 0 ? (
                    <div className="friend-empty">친구 요청이 없습니다.</div>
                ) : (
                    friendRequests.map((f) => (
                        <div className="friend-row" key={f.request_id}>
                            <img
                                src={f.profile_image_url || base_profile}
                                alt={`${f.sender_nickname}의 프로필 이미지`}
                                className="friend-avatar"
                            />
                            <div className="friend-info">
                                <div className="friend-name">{f.sender_nickname}</div>
                                <div className="friend-message">
                                    요청 시간: {f.sent_at ? new Date(f.sent_at).toLocaleString() : '정보 없음'}
                                </div>
                            </div>
                            <div className="friend-request-actions">
                                <button className="friend-accept" onClick={() => handleAccept(f.request_id)}>수락</button>
                                <button className="friend-reject" onClick={() => handleReject(f.request_id)}>거절</button>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {/* 온라인 친구 */}
            <div className="friend-section">
                <div className="friend-section-title">
                    온라인 <span className="friend-section-count green">{filteredOnline.length}명</span>
                </div>
                {filteredOnline.length === 0 ? (
                    <div className="friend-empty">온라인 친구가 없습니다.</div>
                ) : (
                    filteredOnline.map((f) => (
                        <div className="friend-row" key={f.userId}>
                            <img
                                src={f.profileImageUrl || base_profile}
                                alt={`${f.nickname}의 프로필 이미지`}
                                className="friend-avatar"
                            />
                            <div className="friend-info">
                                <div className="friend-name">{f.nickname}</div>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {/* 오프라인 친구 */}
            <div className="friend-section">
                <div className="friend-section-title">
                    오프라인 <span className="friend-section-count gray">{filteredOffline.length}명</span>
                </div>
                {filteredOffline.length === 0 ? (
                    <div className="friend-empty">오프라인 친구가 없습니다.</div>
                ) : (
                    filteredOffline.map((f) => (
                        <div className="friend-row" key={f.user_id}>
                            <img src={f.profile_image_url || base_profile} alt={f.nickname} className="friend-avatar" />
                            <div className="friend-info">
                                <div className="friend-name">{f.nickname}</div>
                            </div>
                            <div className="friend-more-wrap">
                                <button
                                    className="friend-more-btn"
                                    onClick={() => setShowMoreId(showMoreId === f.user_id ? null : f.user_id)}
                                >
                                    ⋯
                                </button>
                                {showMoreId === f.user_id && (
                                    <div className="friend-more-dropdown">
                                        <div onClick={() => handleRemoveFriend(f.user_id)}>친구 삭제</div>
                                        <div onClick={() => handleBlockFriend(f.user_id)}>차단</div>
                                    </div>
                                )}
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );


};

export default Friend;
