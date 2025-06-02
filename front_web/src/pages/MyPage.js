import React, { useEffect, useState } from 'react';
import api from '../api/api';
import '../css/MyPage.css';

const DEFAULT_PROFILE_IMG = 'https://cdn-icons-png.flaticon.com/512/149/149071.png';
const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const MyPage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api.get('/users/me')
            .then(res => {
                const userData = res.data?.data;
                if (!userData) throw new Error('세션 만료');
                setUser(userData);
                console.log('👤 사용자 정보:', userData); // ✅ 실제 구조 확인
            })
            .catch(err => {
                console.error('유저 정보 조회 실패:', err);
                alert('로그인 정보가 만료되었습니다. 다시 로그인 해주세요.');
                window.location.href = '/login';
            })
            .finally(() => setLoading(false));
    }, []);

    const handleLogout = () => {
        api.post('/auth/logout').finally(() => {
            window.location.href = '/login';
        });
    };

    if (loading || !user) return <div className="mypage-container">불러오는 중...</div>;

    const profileImgSrc = (() => {
        if (!user.profile_image_url) return DEFAULT_PROFILE_IMG;
        if (user.profile_image_url.startsWith('http')) return user.profile_image_url;
        return `${BASE_URL}${user.profile_image_url}`;
    })();

    return (
        <div className="mypage-container">
            <h2 className="mypage-title">마이페이지</h2>
            <div className="mypage-info-card">
                <div className="mypage-profile-img-row">
                    <img src={profileImgSrc} alt="프로필" className="mypage-profile-img" />
                </div>
                <div className="mypage-row"><span className="mypage-label">이름</span><span>{user.name}</span></div>
                <div className="mypage-row"><span className="mypage-label">아이디</span><span>{user.username}</span></div>
                <div className="mypage-row"><span className="mypage-label">이메일</span><span>{user.email}</span></div>
                {user.nickname && <div className="mypage-row"><span className="mypage-label">닉네임</span><span>{user.nickname}</span></div>}
                {user.phone && <div className="mypage-row"><span className="mypage-label">전화번호</span><span>{user.phone}</span></div>}
                {user.grade && <div className="mypage-row"><span className="mypage-label">학년</span><span>{user.grade}</span></div>}
                {user.study_habit && <div className="mypage-row"><span className="mypage-label">공부 습관</span><span>{user.study_habit}</span></div>}
                {user.created_at && <div className="mypage-row"><span className="mypage-label">가입일</span><span>{user.created_at.slice(0, 10)}</span></div>}
            </div>
            <div className="mypage-btn-row">
                <button className="mypage-btn logout" onClick={handleLogout}>로그아웃</button>
            </div>
        </div>
    );
};

export default MyPage;
