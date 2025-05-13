import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/NavBar.css';

const HeaderBar = ({ selectedTab }) => {
  return (
    <div className="header-bar">
      <div className="header-bar-logo">로고</div>
    </div>
  );
};

const SideMenu = ({ menuItems, handleNavigation, selectedTab }) => {
  return (
    <div className="side-menu">
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
              className="side-menu-button"
            >
              {item.name}
            </button>
          </li>
        ))}
      </ul>
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
      <HeaderBar selectedTab={selectedTab} />
      <SideMenu
        menuItems={menuItems}
        handleNavigation={handleNavigation}
        selectedTab={selectedTab}
      />
      {/* 페이지 콘텐츠 */}
    </div>
  );
};

export default NavBar;
