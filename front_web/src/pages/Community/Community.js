import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../../css/Community.css';

const Community = () => {
    const navigate = useNavigate();

    const dummyChatRooms = [
        // 🧍‍♀️ 1:1 채팅방 5개
        {
            id: 1,
            opponentName: '김민지',
            opponentProfile: 'https://i.pravatar.cc/150?img=7',
            lastMessage: '오늘 자료 확인해봤어?',
            unreadCount: 2,
            lastSentAt: '15:24',
            notificationsEnabled: true,
            isPinned: true,
            isGroup: false,
        },
        {
            id: 2,
            opponentName: '이준호',
            opponentProfile: 'https://i.pravatar.cc/150?img=12',
            lastMessage: '내일 회의 몇 시였지?',
            unreadCount: 0,
            lastSentAt: '13:10',
            notificationsEnabled: false,
            isPinned: false,
            isGroup: false,
        },
        {
            id: 3,
            opponentName: '정하늘',
            opponentProfile: 'https://i.pravatar.cc/150?img=15',
            lastMessage: '수정 요청된 부분 반영했어요!',
            unreadCount: 4,
            lastSentAt: '10:48',
            notificationsEnabled: true,
            isPinned: true,
            isGroup: false,
        },
        {
            id: 4,
            opponentName: '박지후',
            opponentProfile: 'https://i.pravatar.cc/150?img=18',
            lastMessage: '이번 주말에 만날 수 있을까?',
            unreadCount: 0,
            lastSentAt: '어제',
            notificationsEnabled: true,
            isPinned: false,
            isGroup: false,
        },
        {
            id: 5,
            opponentName: '한서윤',
            opponentProfile: 'https://i.pravatar.cc/150?img=20',
            lastMessage: '감사합니다! 덕분에 해결했어요 😊',
            unreadCount: 1,
            lastSentAt: '09:03',
            notificationsEnabled: false,
            isPinned: false,
            isGroup: false,
        },

        // 👥 그룹 채팅방 3개
        {
            id: 6,
            roomName: '스터디 팀 A',
            groupProfileImage: '', // 빈 문자열이면 hostProfile 대체 사용
            hostProfile: 'https://i.pravatar.cc/150?img=23',
            memberCount: 5,
            lastMessage: '오늘 몇 시에 시작할까요?',
            unreadCount: 3,
            lastSentAt: '11:12',
            notificationsEnabled: false,
            isPinned: true,
            isGroup: true,
        },
        {
            id: 7,
            roomName: '웹개발 그룹',
            groupProfileImage: 'https://i.pravatar.cc/150?img=30',
            hostProfile: 'https://i.pravatar.cc/150?img=31',
            memberCount: 8,
            lastMessage: 'API 연동 완료했습니다.',
            unreadCount: 0,
            lastSentAt: '어제',
            notificationsEnabled: true,
            isPinned: false,
            isGroup: true,
        },
        {
            id: 8,
            roomName: '토익 스터디',
            groupProfileImage: '',
            hostProfile: 'https://i.pravatar.cc/150?img=26',
            memberCount: 6,
            lastMessage: '다음 단어 시험은 금요일!',
            unreadCount: 6,
            lastSentAt: '08:41',
            notificationsEnabled: false,
            isPinned: true,
            isGroup: true,
        },
    ];




    const dummyPosts = [
        {
            id: 1,
            title: '게시글 작성 UI를 통한 테스트',
            name: '홍길동',
            profileImage: 'https://i.pravatar.cc/150?img=3',
            date: '2025.05.14',
            time: '14:32',
            content: '테스트입니다',
            likes: 3,
            comments: 4,
            views: 42,
        },
        {
            id: 2,
            title: '두 번째 게시글',
            name: '김철수',
            profileImage: 'https://i.pravatar.cc/150?img=5',
            date: '2025.05.13',
            time: '09:20',
            content: '여기는 두 번째 글의 내용입니다.',
            likes: 1,
            comments: 0,
            views: 13,
        },
        {
            id: 3,
            title: '세 번째 커뮤니티 글',
            name: '이영희',
            profileImage: 'https://i.pravatar.cc/150?img=6',
            date: '2025.05.12',
            time: '17:45',
            content: '오늘은 날씨가 참 좋네요.',
            likes: 5,
            comments: 2,
            views: 55,
        },
        {
            id: 4,
            title: '스터디 구합니다!',
            name: '박민준',
            profileImage: 'https://i.pravatar.cc/150?img=9',
            date: '2025.05.11',
            time: '11:11',
            content: '함께 공부할 분 구합니다 :)',
            likes: 7,
            comments: 5,
            views: 87,
        },
        {
            id: 5,
            title: 'React 질문 있어요',
            name: '최서연',
            profileImage: 'https://i.pravatar.cc/150?img=11',
            date: '2025.05.10',
            time: '21:08',
            content: 'useEffect 사용 시 주의할 점이 궁금합니다.',
            likes: 2,
            comments: 3,
            views: 30,
        },
    ];

    const dummyFriends = [
        {
            id: 1,
            nickname: '홍길동',
            email: 'hong@example.com',
            profileImage: 'https://i.pravatar.cc/150?img=3',
            isOnline: true,
        },
        {
            id: 2,
            nickname: '김철수',
            email: 'kim@example.com',
            profileImage: 'https://i.pravatar.cc/150?img=5',
            isOnline: false,
        },
        {
            id: 3,
            nickname: '이영희',
            email: 'lee@example.com',
            profileImage: 'https://i.pravatar.cc/150?img=6',
            isOnline: true,
        },
        {
            id: 4,
            nickname: '박민준',
            email: 'park@example.com',
            profileImage: 'https://i.pravatar.cc/150?img=9',
            isOnline: false,
        },
        {
            id: 5,
            nickname: '최서연',
            email: 'choi@example.com',
            profileImage: 'https://i.pravatar.cc/150?img=11',
            isOnline: true,
        },
    ];

    return (
        <div className="community-home">
            <h2>커뮤니티</h2>
            <p className="community-subtitle">실시간 채팅, 게시판 글, 친구 목록을 한눈에 확인해보세요</p>

            <div className="community-columns">
                {/* ✅ 채팅방 섹션 */}
                <div className="community-column">
                    <div className="community-column-header">
                        <h3>채팅방</h3>
                        <button onClick={() => navigate('/community/chat')}>더보기</button>
                    </div>

                    {dummyChatRooms.map((room) => {
                        const profileSrc = room.isGroup
                            ? room.groupProfileImage || room.ownerProfileImage
                            : room.opponentProfile;

                        return (
                            <div key={room.id} className="chatroom-card">
                                <div className="chatroom-left">
                                    <img src={profileSrc} alt="profile" className="chatroom-avatar" />
                                    <div className="chatroom-info">
                                        <div className="chatroom-name">
                                            {room.isGroup ? (
                                                <>
                                                    {room.groupName}
                                                    <img src="/images/people.png" alt="group" className="group-icon" />
                                                    <span className="member-count">{room.memberCount}</span>
                                                </>
                                            ) : (
                                                room.opponentName
                                            )}
                                        </div>
                                        <div className="chatroom-last-message">{room.lastMessage}</div>
                                    </div>
                                </div>

                                <div className="chatroom-meta">
                                    <div className="chatroom-top-meta">
                                        {room.isPinned && <span className="chatroom-pin">📌</span>}
                                        {!room.notificationsEnabled && (
                                            <span className="chatroom-notify-off">🔕</span>
                                        )}
                                        <span className="chatroom-time">{room.lastSentAt}</span>
                                    </div>
                                    {room.unreadCount > 0 && (
                                        <span className="chatroom-unread">{room.unreadCount}</span>
                                    )}
                                </div>
                            </div>
                        );
                    })}
                </div>

                {/* ✅ 게시글 섹션 */}
                <div className="community-column">
                    <div className="community-column-header">
                        <h3>게시글</h3>
                        <button onClick={() => navigate('/community/post')}>더보기</button>
                    </div>
                    {dummyPosts.map((post) => (
                        <div key={post.id} className="community-post-card">
                            <div className="post-card-top">
                                <div className="post-writer">
                                    <img src={post.profileImage} alt="profile" className="post-profile" />
                                    <span className="post-author">{post.name}</span>
                                </div>
                                <div className="post-top-meta">
                                    <span className="post-date">{post.date}</span>
                                    <span className="post-time">{post.time}</span>
                                    <span className="post-menu">⋯</span>
                                </div>
                            </div>
                            <h4 className="post-title">{post.title}</h4>
                            <p>{post.content}</p>
                            <div className="post-stats">
                                <span>❤️ {post.likes}</span>
                                <span>💬 {post.comments}</span>
                                <span>👁 {post.views}</span>
                            </div>
                        </div>
                    ))}
                </div>

                {/* ✅ 친구 목록 섹션 */}
                <div className="community-column">
                    <div className="community-column-header">
                        <h3>친구 목록</h3>
                        <button onClick={() => navigate('/community/friend')}>더보기</button>
                    </div>
                    {dummyFriends.map((friend) => (
                        <div key={friend.id} className="friend-card">
                            <div className="friend-content">
                                <img src={friend.profileImage} alt="profile" className="friend-profile" />
                                <div className="friend-info">
                                    <div className="friend-nickname">{friend.nickname}</div>
                                    <div className="friend-email">{friend.email}</div>
                                    <div className={`friend-status ${friend.isOnline ? 'online' : 'offline'}`}>
                                        <span className="status-dot"></span>
                                        {friend.isOnline ? '온라인' : '오프라인'}
                                    </div>
                                </div>
                            </div>
                            <button className="chat-button">채팅 시작</button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default Community;