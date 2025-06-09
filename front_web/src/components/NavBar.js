import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import '../css/Navbar.css';
import api from '../api/api';
import base_profile from '../icons/base_profile.png'; // ✅ 이미지 import로 수정

const SideMenu = ({ menuItems, handleNavigation, selectedTab, selectedSubTab, user }) => {
    return (
        <div className="side-menu">
            <div className="side-menu-logo">로고</div>

            <ul className="side-menu-list">
                {menuItems.map((item) => (
                    <React.Fragment key={item.name}>
                        <li className="side-menu-list-item">
                            <button
                                onClick={() => handleNavigation(item.name, item.path)}
                                className={`side-menu-button${selectedTab === item.name && selectedSubTab === '' ? ' active' : ''}`}
                            >
                                {item.name}
                            </button>
                        </li>
                        {item.name === '커뮤니티' && selectedTab === '커뮤니티' && (
                            <ul className="side-submenu-list">
                                {item.subItems.map((sub) => (
                                    <li
                                        key={sub.name}
                                        className={`side-submenu-item ${selectedSubTab === sub.name ? 'active' : ''}`}
                                    >
                                        <button
                                            onClick={() => handleNavigation('커뮤니티', sub.path, sub.name)}
                                            className={`side-submenu-button ${selectedSubTab === sub.name ? 'active' : ''}`}
                                        >
                                            {sub.name}
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </React.Fragment>
                ))}
            </ul>

            {/* ✅ 사용자 프로필 */}
            {user && (
                <div className="side-user-profile">
                    <img
                        src={user.profile_image_url ? user.profile_image_url : base_profile}
                        alt="프로필"
                        className="side-user-image"
                    />

                    <div className="side-user-nickname">{user.nickname}</div>
                </div>
            )}

            {/* ✅ 마이페이지 버튼 */}
            <div className="side-menu-bottom">
                <button
                    className={`side-menu-button${selectedTab === '마이페이지' ? ' active' : ''}`}
                    onClick={() => handleNavigation('마이페이지', '/mypage')}
                >
                    마이페이지
                </button>
            </div>
        </div>
    );
};


const NavBar = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [selectedTab, setSelectedTab] = useState('');
    const [selectedSubTab, setSelectedSubTab] = useState('');
    const [user, setUser] = useState(null);

    const hideSidebarPaths = ['/unit-evaluation/start'];

    const menuItems = [
        { name: '대시보드', path: '/dashboard' },
        { name: '공부 시작', path: '/StudyStart' },
        { name: '단원 평가', path: '/evaluation' },
        { name: '통계', path: '/statistics' },
        {
            name: '커뮤니티',
            path: '/community',
            subItems: [
                { name: '공지사항', path: '/community/notice' },
                { name: '채팅', path: '/community/chat' },
                { name: '게시판', path: '/community/post' },
                { name: '친구', path: '/community/friend' },
            ],
        },
    ];

    useEffect(() => {
        const path = location.pathname;
        if (path.startsWith('/community')) {
            setSelectedTab('커뮤니티');
            if (path.includes('/notice')) setSelectedSubTab('공지사항');
            else if (path.includes('/chat')) setSelectedSubTab('채팅');
            else if (path.includes('/post')) setSelectedSubTab('게시판');
            else if (path.includes('/friend')) setSelectedSubTab('친구');
            else setSelectedSubTab('');
        } else {
            const mainItem = menuItems.find((item) => path.startsWith(item.path));
            setSelectedTab(mainItem ? mainItem.name : '');
            setSelectedSubTab('');
        }
    }, [location.pathname]);

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const res = await api.get('/users/me');
                console.log(res.data); // 구조 확인용

                setUser(res.data.data); // ✅ 내부 data를 user로 설정

            } catch (error) {
                console.error('프로필 조회 실패:', error);
            }
        };
        fetchUserInfo();
    }, []);


    if (hideSidebarPaths.includes(location.pathname)) return null;

    const handleNavigation = (name, path, subName = '') => {
        setSelectedTab(name);
        setSelectedSubTab(subName);
        navigate(path);
    };

    return (
        <SideMenu
            menuItems={menuItems}
            handleNavigation={handleNavigation}
            selectedTab={selectedTab}
            selectedSubTab={selectedSubTab}
            user={user}
        />
    );
};

export default NavBar;
