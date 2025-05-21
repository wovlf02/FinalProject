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

    const fetchFriendList = async () => {
        try {
            const res = await api.get('/friends');
            setOnlineFriends(res.data.onlineFriends || []);
            setOfflineFriends(res.data.offlineFriends || []);
        } catch (err) {
            console.error('❌ 친구 목록 조회 실패:', err);
            alert('친구 목록을 불러오지 못했습니다.');
        }
    };

    const fetchRequests = async () => {
        try {
            const res = await api.get('/friends/requests');
            setFriendRequests(res.data.requests || []);
        } catch (err) {
            console.error('❌ 요청 목록 조회 실패:', err);
            alert('친구 요청 목록을 불러오지 못했습니다.');
        }
    };

    const fetchBlocked = async () => {
        try {
            const res = await api.get('/friends/blocked');
            setBlockedFriends(res.data.blocked || []);
        } catch (err) {
            console.error('❌ 차단 목록 조회 실패:', err);
            alert('차단 목록을 불러오지 못했습니다.');
        }
    };

    const handleAccept = async (requestId) => {
        try {
            await api.post(`/friends/request/${requestId}/accept`, { requestId });
            setFriendRequests(prev => prev.filter(req => req.id !== requestId));
            fetchFriendList();
        } catch (err) {
            console.error('❌ 요청 수락 실패:', err);
            alert('친구 요청 수락에 실패했습니다.');
        }
    };

    const handleReject = async (requestId) => {
        try {
            await api.post(`/friends/request/${requestId}/reject`, { requestId });
            setFriendRequests(prev => prev.filter(req => req.id !== requestId));
        } catch (err) {
            console.error('❌ 요청 거절 실패:', err);
            alert('친구 요청 거절에 실패했습니다.');
        }
    };

    const searchUsersByNickname = async (nickname) => {
        if (!nickname.trim()) {
            setSearchResults([]);
            return;
        }
        try {
            const res = await api.get('/friends/search', { params: { nickname } });
            setSearchResults(res.data.results || []);
        } catch (err) {
            console.error('❌ 친구 검색 실패:', err);
        }
    };

    const handleAddFriend = async (user_id) => {
        try {
            await api.post('/friends/request', {
                targetUserId: user_id,
            });
            console.log("🧾 친구 요청 대상:", user_id);
            alert('친구 요청이 전송되었습니다.');
            setAddName('');
            setAddMsg('');
            setSearchResults([]);
            setShowAddModal(false);
            fetchRequests();
        } catch (err) {
            console.error('❌ 요청 전송 실패:', err);
            alert('친구 요청 전송에 실패했습니다.');
        }
    };


    const handleRemoveFriend = async (id) => {
        try {
            await api.delete(`/friends/${id}`);
            setOnlineFriends(prev => prev.filter(f => f.id !== id));
            setOfflineFriends(prev => prev.filter(f => f.id !== id));
        } catch (err) {
            console.error('❌ 친구 삭제 실패:', err);
            alert('친구 삭제에 실패했습니다.');
        }
    };

    const handleBlockFriend = async (id) => {
        try {
            await api.post(`/friends/block/${id}`);
            setOnlineFriends(prev => prev.filter(f => f.id !== id));
            setOfflineFriends(prev => prev.filter(f => f.id !== id));
            fetchBlocked();
        } catch (err) {
            console.error('❌ 친구 차단 실패:', err);
            alert('친구 차단에 실패했습니다.');
        }
    };

    const handleUnblock = async (id) => {
        try {
            await api.delete(`/friends/block/${id}`);
            setBlockedFriends(prev => prev.filter(f => f.id !== id));
            fetchFriendList();
        } catch (err) {
            console.error('❌ 차단 해제 실패:', err);
            alert('차단 해제에 실패했습니다.');
        }
    };

    const filterFn = (f) =>
        (!search || f.name.toLowerCase().includes(search.toLowerCase())) &&
        (filterType === '전체' ||
            (filterType === '온라인' && onlineFriends.some(o => o.id === f.id)) ||
            (filterType === '오프라인' && offlineFriends.some(o => o.id === f.id)));

    const filteredOnline = onlineFriends.filter(filterFn);
    const filteredOffline = offlineFriends.filter(filterFn);
    const filteredRequests = friendRequests.filter(filterFn);

    return (
        <div className="friend-root">
            <div className="friend-header">
                <h2>친구</h2>
                <div className="friend-header-actions">
                    <button
                        className="friend-block-manager-btn"
                        onClick={() => setShowBlockManager(true)}
                    >
                        차단 관리
                    </button>
                    <button
                        className="friend-join-btn"
                        onClick={() => navigate('/community')}
                    >
                        커뮤니티로 돌아가기
                    </button>
                </div>
            </div>
            <div className="friend-search-row">
                <input
                    className="friend-search"
                    placeholder="친구 검색..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                />
                <div className="friend-search-actions">
                    <button className="friend-add-btn" onClick={() => setShowAddModal(true)}>
                        친구 추가
                    </button>
                    <button
                        className="friend-filter-btn"
                        onClick={() => setShowFilter((v) => !v)}
                    >
                        <span className="friend-filter-icon">☰</span>
                        필터
                    </button>
                    {showFilter && (
                        <div className="friend-filter-dropdown">
                            <div
                                className={filterType === '전체' ? 'active' : ''}
                                onClick={() => {
                                    setFilterType('전체');
                                    setShowFilter(false);
                                }}
                            >
                                전체
                            </div>
                            <div
                                className={filterType === '온라인' ? 'active' : ''}
                                onClick={() => {
                                    setFilterType('온라인');
                                    setShowFilter(false);
                                }}
                            >
                                온라인
                            </div>
                            <div
                                className={filterType === '오프라인' ? 'active' : ''}
                                onClick={() => {
                                    setFilterType('오프라인');
                                    setShowFilter(false);
                                }}
                            >
                                오프라인
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {/* 친구 추가 모달 */}
            {showAddModal && (
                <div className="friend-modal-overlay">
                    <div className="friend-modal">
                        <button
                            className="friend-modal-close"
                            onClick={() => setShowAddModal(false)}
                        >
                            ×
                        </button>
                        <h3>친구 추가</h3>
                        <div className="friend-search-form">
                            <input
                                placeholder="닉네임으로 검색"
                                value={addName}
                                onChange={(e) => setAddName(e.target.value)}
                            />
                            <button
                                className="friend-search-btn"
                                onClick={() => searchUsersByNickname(addName)}
                            >
                                검색
                            </button>
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
                                        onClick={() => {
                                            if (!user.user_id) {
                                                console.warn('❗ user.user_id가 undefined입니다:', user);
                                                alert('유효하지 않은 사용자입니다.');
                                                return;
                                            }
                                            handleAddFriend(user.user_id);
                                        }}
                                        disabled={
                                            user.already_friend ||
                                            user.already_requested ||
                                            user.blocked
                                        }
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
                        <button className="friend-modal-close" onClick={() => setShowBlockManager(false)}>
                            ×
                        </button>
                        <h3>차단한 친구 관리</h3>
                        {blockedFriends.length === 0 ? (
                            <div className="friend-empty" style={{padding: '18px 0'}}>차단한 친구가 없습니다.</div>
                        ) : (
                            <div>
                                {blockedFriends.map((f) => (
                                    <div className="friend-row" key={f.id}>
                                        <img src={f.avatar} alt={f.name} className="friend-avatar"/>
                                        <div className="friend-info">
                                            <div className="friend-name">{f.name}</div>
                                            <div className="friend-message">{f.message}</div>
                                        </div>
                                        <button
                                            className="friend-accept"
                                            onClick={() => handleUnblock(f.id)}
                                        >
                                            차단 해제
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            )}

            {/* 친구 요청 */}
            <div className="friend-section">
                <div className="friend-section-title">
                    친구 요청 <span className="friend-section-count">{filteredRequests.length}개</span>
                </div>
                {filteredRequests.length === 0 ? (
                    <div className="friend-empty">친구 요청이 없습니다.</div>
                ) : (
                    filteredRequests.map((f) => (
                        <div className="friend-row" key={f.id}>
                            <img src={f.avatar} alt={f.name} className="friend-avatar"/>
                            <div className="friend-info">
                                <div className="friend-name">{f.name}</div>
                                <div className="friend-message">{f.message}</div>
                            </div>
                            <div className="friend-request-actions">
                                <button className="friend-accept" onClick={() => handleAccept(f.id)}>
                                    수락
                                </button>
                                <button className="friend-reject" onClick={() => handleReject(f.id)}>
                                    거절
                                </button>
                            </div>
                        </div>
                    ))
                )}
            </div>
            {/* 온라인 */}
            <div className="friend-section">
                <div className="friend-section-title">
                    온라인 <span className="friend-section-count green">{filteredOnline.length}명</span>
                </div>
                {filteredOnline.length === 0 ? (
                    <div className="friend-empty">온라인 친구가 없습니다.</div>
                ) : (
                    filteredOnline.map((f) => (
                        <div className="friend-row" key={f.id}>
                            <img src={f.avatar} alt={f.name} className="friend-avatar"/>
                            <div className="friend-info">
                                <div className="friend-name">{f.name}</div>
                                <div className="friend-message">{f.message}</div>
                            </div>
                            <div className="friend-more-wrap">
                                <button
                                    className="friend-more-btn"
                                    onClick={() => setShowMoreId(showMoreId === f.id ? null : f.id)}
                                >
                                    ⋯
                                </button>
                                {showMoreId === f.id && (
                                    <div className="friend-more-dropdown">
                                        <div onClick={() => handleRemoveFriend(f.id, 'online')}>친구 삭제</div>
                                        <div onClick={() => handleBlockFriend(f.id, 'online')}>차단</div>
                                    </div>
                                )}
                            </div>
                        </div>
                    ))
                )}
            </div>
            {/* 오프라인 */}
            <div className="friend-section">
                <div className="friend-section-title">
                    오프라인 <span className="friend-section-count gray">{filteredOffline.length}명</span>
                </div>
                {filteredOffline.length === 0 ? (
                    <div className="friend-empty">오프라인 친구가 없습니다.</div>
                ) : (
                    filteredOffline.map((f) => (
                        <div className="friend-row" key={f.id}>
                            <img src={f.avatar} alt={f.name} className="friend-avatar"/>
                            <div className="friend-info">
                                <div className="friend-name">{f.name}</div>
                                <div className="friend-message">{f.message}</div>
                            </div>
                            <div className="friend-more-wrap">
                                <button
                                    className="friend-more-btn"
                                    onClick={() => setShowMoreId(showMoreId === f.id ? null : f.id)}
                                >
                                    ⋯
                                </button>
                                {showMoreId === f.id && (
                                    <div className="friend-more-dropdown">
                                        <div onClick={() => handleRemoveFriend(f.id, 'offline')}>친구 삭제</div>
                                        <div onClick={() => handleBlockFriend(f.id, 'offline')}>차단</div>
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
