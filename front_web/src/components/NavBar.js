import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/NavBar.css';

const HeaderBar = ({ selectedTab, name }) => {
  return (
    <div className="header-bar">
      <div className="header-bar-logo">로고</div>
      <div>{selectedTab}</div>
      <div>로그인 정보{ name && ` | ${name}` }</div>
    </div>
  );
};

const SideMenu = ({ menuItems, handleNavigation }) => {
  return (
    <div className="side-menu">
      <ul className="side-menu-list">
        {menuItems.map((item) => (
          <li key={item.name} className="side-menu-list-item">
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

const NavBar = ({ name }) => {
  const navigate = useNavigate();
  const [selectedTab, setSelectedTab] = useState('대시보드');

  const menuItems = [
    { name: '대시보드', path: '/dashboard' },
    { name: '공부 시작', path: '/StudyStart' },
    { name: '단원 평가', path: '/evaluation' },
    { name: '통계', path: '/statistics' },
    { name: '커뮤니티', path: '/community' },
    { name: '팀 학습', path: '/teamStudy' },
  ];

  const handleNavigation = (name, path) => {
    setSelectedTab(name);
    navigate(path);
  };

  return (
    <div>
      <HeaderBar selectedTab={selectedTab} name={name} />
      <div style={{ display: 'flex' }}>
        <SideMenu menuItems={menuItems} handleNavigation={handleNavigation} />
        <div style={{ flex: 1, padding: '20px' }}>
          {/* 페이지 콘텐츠 */}
        </div>
      </div>
    </div>
  );
};

export default NavBar;
