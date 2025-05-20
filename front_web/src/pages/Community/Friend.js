import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../css/Friend.css';

const initialRequests = [
  {
    id: 1,
    name: '한명재',
    message: '함께 아는 친구 3명',
    avatar: 'https://i.pravatar.cc/150?img=11',
  },
  {
    id: 2,
    name: '임수진',
    message: '함께 아는 친구 1명',
    avatar: 'https://i.pravatar.cc/150?img=12',
  },
];

const initialOnline = [
  {
    id: 1,
    name: '김일수',
    message: '공부중',
    avatar: 'https://i.pravatar.cc/150?img=13',
  },
  {
    id: 2,
    name: '이지연',
    message: '공부중',
    avatar: 'https://i.pravatar.cc/150?img=14',
  },
  {
    id: 3,
    name: '박호준',
    message: '공부중',
    avatar: 'https://i.pravatar.cc/150?img=15',
  },
  {
    id: 4,
    name: '최서연',
    message: '공부중',
    avatar: 'https://i.pravatar.cc/150?img=16',
  },
  {
    id: 5,
    name: '정우진',
    message: '공부중',
    avatar: 'https://i.pravatar.cc/150?img=17',
  },
];

const initialOffline = [
  {
    id: 1,
    name: '류소현',
    message: '접속하지 않은 지 3시간 전',
    avatar: 'https://i.pravatar.cc/150?img=18',
  },
  {
    id: 2,
    name: '김동훈',
    message: '마지막 접속: 어제',
    avatar: 'https://i.pravatar.cc/150?img=19',
  },
  {
    id: 3,
    name: '황호준',
    message: '접속한 지 없는: 2주 전',
    avatar: 'https://i.pravatar.cc/150?img=20',
  },
];

const Friend = () => {
  const navigate = useNavigate();

  // 상태 관리
  const [friendRequests, setFriendRequests] = useState(initialRequests);
  const [onlineFriends, setOnlineFriends] = useState(initialOnline);
  const [offlineFriends, setOfflineFriends] = useState(initialOffline);
  const [blockedFriends, setBlockedFriends] = useState([]);
  const [search, setSearch] = useState('');
  const [showAddModal, setShowAddModal] = useState(false);
  const [showFilter, setShowFilter] = useState(false);
  const [filterType, setFilterType] = useState('전체');
  const [showMoreId, setShowMoreId] = useState(null);
  const [showBlockManager, setShowBlockManager] = useState(false);

  // 친구 추가 모달 예시
  const [addName, setAddName] = useState('');
  const [addMsg, setAddMsg] = useState('');

  // 검색 필터
  const filterFn = (f) =>
    (!search ||
      f.name.includes(search) ||
      (f.message && f.message.includes(search))) &&
    (filterType === '전체' ||
      (filterType === '온라인' && onlineFriends.some((o) => o.id === f.id)) ||
      (filterType === '오프라인' && offlineFriends.some((o) => o.id === f.id)));

  // 친구 요청 수락
  const handleAccept = (id) => {
    const accepted = friendRequests.find((f) => f.id === id);
    if (accepted) {
      setOnlineFriends([accepted, ...onlineFriends]);
      setFriendRequests(friendRequests.filter((f) => f.id !== id));
    }
  };

  // 친구 요청 거절
  const handleReject = (id) => {
    setFriendRequests(friendRequests.filter((f) => f.id !== id));
  };

  // 친구 추가
  const handleAddFriend = (e) => {
    e.preventDefault();
    if (!addName.trim()) return;
    setFriendRequests([
      ...friendRequests,
      {
        id: Date.now(),
        name: addName,
        message: addMsg || '함께 아는 친구 없음',
        avatar: 'https://i.pravatar.cc/150?img=21',
      },
    ]);
    setAddName('');
    setAddMsg('');
    setShowAddModal(false);
  };

  // 친구 삭제
  const handleRemoveFriend = (id, type) => {
    if (type === 'online') {
      setOnlineFriends(onlineFriends.filter((f) => f.id !== id));
    } else if (type === 'offline') {
      setOfflineFriends(offlineFriends.filter((f) => f.id !== id));
    }
    setShowMoreId(null);
  };

  // 친구 차단
  const handleBlockFriend = (id, type) => {
    let blocked;
    if (type === 'online') {
      blocked = onlineFriends.find((f) => f.id === id);
      setOnlineFriends(onlineFriends.filter((f) => f.id !== id));
    } else if (type === 'offline') {
      blocked = offlineFriends.find((f) => f.id === id);
      setOfflineFriends(offlineFriends.filter((f) => f.id !== id));
    }
    if (blocked) {
      setBlockedFriends([blocked, ...blockedFriends]);
    }
    setShowMoreId(null);
  };

  // 차단 해제
  const handleUnblock = (id) => {
    const unblocked = blockedFriends.find((f) => f.id === id);
    if (unblocked) {
      setOnlineFriends([unblocked, ...onlineFriends]);
      setBlockedFriends(blockedFriends.filter((f) => f.id !== id));
    }
  };

  // 필터 적용
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
            <button className="friend-modal-close" onClick={() => setShowAddModal(false)}>
              ×
            </button>
            <h3>친구 추가</h3>
            <form onSubmit={handleAddFriend}>
              <input
                placeholder="이름"
                value={addName}
                onChange={(e) => setAddName(e.target.value)}
                required
              />
              <input
                placeholder="상태메시지(선택)"
                value={addMsg}
                onChange={(e) => setAddMsg(e.target.value)}
              />
              <button type="submit">추가</button>
            </form>
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
              <div className="friend-empty" style={{padding:'18px 0'}}>차단한 친구가 없습니다.</div>
            ) : (
              <div>
                {blockedFriends.map((f) => (
                  <div className="friend-row" key={f.id}>
                    <img src={f.avatar} alt={f.name} className="friend-avatar" />
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
              <img src={f.avatar} alt={f.name} className="friend-avatar" />
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
              <img src={f.avatar} alt={f.name} className="friend-avatar" />
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
              <img src={f.avatar} alt={f.name} className="friend-avatar" />
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
