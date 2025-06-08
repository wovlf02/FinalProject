import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../css/Community.css';
import api from '../../api/api';

const menu = [
    { label: '공지사항', desc: '중요한 소식과 업데이트', icon: '📢', path: '/community/notice' },
    { label: '채팅', desc: '실시간 대화', icon: '💬', path: '/community/chat' },
    { label: '게시판', desc: '정보 공유와 토론', icon: '📂', path: '/community/post' },
    { label: '친구', desc: '친구 목록과 관리', icon: '👥', path: '/community/friend' },
];

const Community = () => {
    const navigate = useNavigate();

    const [notices, setNotices] = useState([]);
    const [popularPosts, setPopularPosts] = useState([]);
    const [onlineFriends, setOnlineFriends] = useState([]);

    useEffect(() => {
        fetchNotices();
        fetchPopularPosts();
        fetchOnlineFriends();
    }, []);

    // ✅ 주요 공지사항
    const fetchNotices = async () => {
        try {
            const res = await api.get('/community/notices/main');
            setNotices(res.data || []);
        } catch (err) {
            console.error('❌ 공지사항 조회 실패:', err);
        }
    };

    // ✅ 인기 게시글
    const fetchPopularPosts = async () => {
        try {
            const res = await api.get('/community/posts/popular');
            const mapped = (res.data.posts || []).map((post) => ({
                postId: post.post_id,
                title: post.title,
                authorNickname: post.writer_nickname,
                likeCount: post.like_count,
            }));
            setPopularPosts(mapped);
        } catch (err) {
            console.error('❌ 인기 게시글 조회 실패:', err);
        }
    };

    // ✅ 접속 중인 친구
    const fetchOnlineFriends = async () => {
        try {
            const res = await api.get('/friends/list');
            setOnlineFriends(res.data.online_friends || []);
        } catch (err) {
            console.error('❌ 온라인 친구 조회 실패:', err);
        }
    };

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
                    <button className="community-more-btn" onClick={() => navigate('/community/notice')}>
                        더보기
                    </button>
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
                {/* 실시간 인기 게시글 */}
                <div className="community-section community-bottom-card">
                    <div className="community-section-title">실시간 인기 게시글</div>
                    <div className="community-post-list">
                        {popularPosts.map((p) => (
                            <div className="community-post-row" key={p.postId}>
                                <div>
                                    <div className="community-post-title">{p.title}</div>
                                    <div className="community-post-author">{p.authorNickname}</div>
                                </div>
                                <div className="community-post-likes">❤ {p.likeCount}</div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* 접속 중인 친구 */}
                <div className="community-section community-bottom-card">
                    <div className="community-section-title">접속 중인 친구</div>
                    <div className="community-friend-list">
                        {onlineFriends.map((f) => (
                            <div className="community-friend" key={f.user_id}>
                                <div className="community-friend-avatar-wrap">
                                    <img
                                        src={f.profile_image_url || '/default_profile.png'}
                                        alt={f.nickname}
                                        className="community-friend-avatar"
                                    />
                                    <span className="community-friend-status" />
                                </div>
                                <div className="community-friend-name">{f.nickname}</div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Community;
