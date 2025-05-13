import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const SideMenu = ({ menuItems, handleNavigation, selectedTab }) => {
  return (
    <div className="side-menu">
      <div className="side-menu-logo">로고</div>
      <ul className="side-menu-list">
        {menuItems.map((item) => (
          <li
            key={item.name}
            className={`side-menu-list-item ${
              selectedTab === item.name ? 'active' : ''
            }`}
          >
            <button
              onClick={() => handleNavigation(item.name, item.path)}
              className={`side-menu-button${selectedTab === item.name ? ' active' : ''}`}
            >
              {item.name}
            </button>
          </li>
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
  const [selectedTab, setSelectedTab] = useState('대시보드');

  const menuItems = [
    { name: '대시보드', path: '/dashboard' },
    { name: '공부 시작', path: '/StudyStart' },
    { name: '단원 평가', path: '/evaluation' },
    { name: '통계', path: '/statistics' },
    { name: '커뮤니티', path: '/community' },
  ];

  const handleNavigation = (name, path) => {
    setSelectedTab(name);
    navigate(path);
  };

  return (
    <div>
      <SideMenu
        menuItems={menuItems}
        handleNavigation={handleNavigation}
        selectedTab={selectedTab}
      />
    </div>
  );
};

export default NavBar;
