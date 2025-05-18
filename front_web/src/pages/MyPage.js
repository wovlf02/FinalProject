import React, {useEffect, useState} from 'react';
import axios from 'axios';
import '../css/MyPage.css';

const DEFAULT_PROFILE_IMG = 'https://cdn-icons-png.flaticon.com/512/149/149071.png'; // 기본 이미지 URL

const MyPage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    const accessToken = localStorage.getItem('accessToken');

    useEffect(() => {
        if (!accessToken) {
            window.location.href = '/login';
            return;
        }
        axios.get('http://localhost:8080/api/user/me', {
            headers: {
                Authorization: `Bearer ${accessToken}`,
            }
        })
            .then(res => {
                setUser(res.data);
                setLoading(false);
            })
            .catch(err => {
                alert('로그인 정보가 만료되었습니다. 다시 로그인 해주세요.');
                localStorage.clear();
                window.location.href = '/login';
            });
    }, [accessToken]);

    const handleLogout = () => {
        localStorage.clear();
        window.location.href = '/login';
    };

    const handleChangePassword = () => {
        alert('비밀번호 변경 기능은 추후 지원됩니다.');
    };

    if (loading || !user) return <div className="mypage-container">불러오는 중...</div>;

    // 프로필 이미지 경로 처리 (백엔드에서 http로 주면 그대로, 상대경로면 서버 주소 붙이기)
    let profileImgSrc = DEFAULT_PROFILE_IMG;
    if (user.profileImageUrl) {
        profileImgSrc = user.profileImageUrl.startsWith('http')
            ? user.profileImageUrl
            : `http://localhost:8080${user.profileImageUrl}`;
    }

    return (
        <div className="mypage-container">
            <h2 className="mypage-title">마이페이지</h2>
            <div className="mypage-info-card">
                <div className="mypage-profile-img-row">
                    <img
                        src={profileImgSrc}
                        alt="프로필"
                        className="mypage-profile-img"
                    />
                </div>
                <div className="mypage-row">
                    <span className="mypage-label">이름</span>
                    <span>{user.name}</span>
                </div>
                <div className="mypage-row">
                    <span className="mypage-label">아이디</span>
                    <span>{user.username}</span>
                </div>
                <div className="mypage-row">
                    <span className="mypage-label">이메일</span>
                    <span>{user.email}</span>
                </div>
                {user.nickname && (
                    <div className="mypage-row">
                        <span className="mypage-label">닉네임</span>
                        <span>{user.nickname}</span>
                    </div>
                )}
                {user.phone && (
                    <div className="mypage-row">
                        <span className="mypage-label">전화번호</span>
                        <span>{user.phone}</span>
                    </div>
                )}
                {user.grade && (
                    <div className="mypage-row">
                        <span className="mypage-label">학년</span>
                        <span>{user.grade}</span>
                    </div>
                )}
                {user.studyHabit && (
                    <div className="mypage-row">
                        <span className="mypage-label">공부 습관</span>
                        <span>{user.studyHabit}</span>
                    </div>
                )}
                {user.createdAt && (
                    <div className="mypage-row">
                        <span className="mypage-label">가입일</span>
                        <span>{user.createdAt.slice(0, 10)}</span>
                    </div>
                )}
            </div>
            <div className="mypage-btn-row">
                <button className="mypage-btn logout" onClick={handleLogout}>로그아웃</button>
            </div>
        </div>
    );
};

export default MyPage;
