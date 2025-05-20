import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../../css/Community.css';

const notices = [
  { id: 1, title: '5월 업데이트 안내', date: '2025-05-15', views: 1243 },
  { id: 2, title: '커뮤니티 이용 규칙 개정 안내', date: '2025-05-10', views: 987 },
  { id: 3, title: '여름 이벤트 사전 공지', date: '2025-05-05', views: 856 },
];

const posts = [
  { id: 1, title: '게임 업데이트 소식 공유합니다', author: '게임마스터', likes: 128 },
  { id: 2, title: '주말 이벤트 참가 인증', author: '이벤트킹', likes: 96 },
  { id: 3, title: '신규 캐릭터 분석 및 공략', author: '프로게이머', likes: 85 },
  { id: 4, title: '커뮤니티 모임 후기', author: '모임장', likes: 74 },
  { id: 5, title: '초보자를 위한 팁 모음', author: '버터링쿠키', likes: 67 },
];

const friends = [
  { id: 1, name: '김민수', avatar: 'https://i.pravatar.cc/150?img=1', online: true },
  { id: 2, name: '이지연', avatar: 'https://i.pravatar.cc/150?img=2', online: true },
  { id: 3, name: '박준호', avatar: 'https://i.pravatar.cc/150?img=3', online: true },
  { id: 4, name: '최서연', avatar: 'https://i.pravatar.cc/150?img=4', online: true },
  { id: 5, name: '정우진', avatar: 'https://i.pravatar.cc/150?img=5', online: true },
];

const menu = [
  { label: '공지사항', desc: '중요한 소식과 업데이트', icon: '📢', path: '/community/notice' },
  { label: '채팅', desc: '실시간 대화', icon: '💬', path: '/community/chat' },
  { label: '게시판', desc: '정보 공유와 토론', icon: '📂', path: '/community/post' },
  { label: '친구', desc: '친구 목록과 관리', icon: '👥', path: '/community/friend' },
];

const Community = () => {
  const navigate = useNavigate();

  return (
    <div className="community-root">
      <h1 className="community-title">커뮤니티</h1>

      {/* 상단 메뉴 카드 */}
      <div className="community-menu-row">
        {menu.map((m) => (
          <div
            key={m.label}
            className="community-menu-card"
            onClick={() => navigate(m.path)}
            tabIndex={0}
            role="button"
          >
            <div className="community-menu-icon">{m.icon}</div>
            <div className="community-menu-info">
              <div className="community-menu-label">{m.label}</div>
              <div className="community-menu-desc">{m.desc}</div>
            </div>
          </div>
        ))}
      </div>

      {/* 주요 공지사항 */}
      <div className="community-section">
        <div className="community-section-header">
          <div className="community-section-title">주요 공지사항</div>
          <button className="community-more-btn" onClick={() => navigate('/community/notice')}>더보기</button>
        </div>
        <div className="community-notice-list">
          {notices.map((n) => (
            <div className="community-notice-row" key={n.id}>
              <div className="community-notice-title">{n.title}</div>
              <div className="community-notice-meta">
                <span>{n.date}</span>
                <span className="community-notice-views">👁 {n.views}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* 하단 2단 영역 */}
      <div className="community-bottom-row">
        {/* 인기 게시글 */}
        <div className="community-section community-bottom-card">
          <div className="community-section-title">실시간 인기 게시글</div>
          <div className="community-post-list">
            {posts.map((p) => (
              <div className="community-post-row" key={p.id}>
                <div>
                  <div className="community-post-title">{p.title}</div>
                  <div className="community-post-author">{p.author}</div>
                </div>
                <div className="community-post-likes">❤ {p.likes}</div>
              </div>
            ))}
          </div>
        </div>
        {/* 접속 중인 친구 */}
        <div className="community-section community-bottom-card">
          <div className="community-section-title">접속 중인 친구</div>
          <div className="community-friend-list">
            {friends.map((f) => (
              <div className="community-friend" key={f.id}>
                <div className="community-friend-avatar-wrap">
                  <img src={f.avatar} alt={f.name} className="community-friend-avatar" />
                  <span className="community-friend-status" />
                </div>
                <div className="community-friend-name">{f.name}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Community;
