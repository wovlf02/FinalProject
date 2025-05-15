import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import '../css/Navbar.css';

const SideMenu = ({ menuItems, handleNavigation, selectedTab, selectedSubTab }) => {
  return (
      <div className="side-menu">
        <div className="side-menu-logo">로고</div>
        <ul className="side-menu-list">
          {menuItems.map((item) => (
              <React.Fragment key={item.name}>
                <li className={`side-menu-list-item`}>
                  <button
                      onClick={() => handleNavigation(item.name, item.path)}
                      className={`side-menu-button${
                          selectedTab === item.name && selectedSubTab === '' ? ' active' : ''
                      }`}
                  >
                    {item.name}
                  </button>
                </li>
                {/* 커뮤니티 하위 메뉴 렌더링 */}
                {item.name === '커뮤니티' && selectedTab === '커뮤니티' && (
                    <ul className="side-submenu-list">
                      {item.subItems.map((sub) => (
                          <li
                              key={sub.name}
                              className={`side-submenu-item ${
                                  selectedSubTab === sub.name ? 'active' : ''
                              }`}
                          >
                            <button
                                onClick={() => handleNavigation('커뮤니티', sub.path, sub.name)}
                                className={`side-submenu-button ${
                                    selectedSubTab === sub.name ? 'active' : ''
                                }`}
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

  const hideSidebarPaths = ['/unit-evaluation/start'];

  // ✅ 이 부분을 useEffect 위로 옮김
  const menuItems = [
    { name: '대시보드', path: '/dashboard' },
    { name: '공부 시작', path: '/StudyStart' },
    { name: '단원 평가', path: '/evaluation' },
    { name: '통계', path: '/statistics' },
    {
      name: '커뮤니티',
      path: '/community',
      subItems: [
          { name: '공지사항', path: '/community/notice'},
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
      />
  );
};


export default NavBar;
