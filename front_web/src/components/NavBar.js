import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // navigate 추가
import '../css/NavBar.css';

const HeaderBar = ({ selectedTab, userName }) => {
  return (
    <div className="header-bar">
      <div className="header-bar-logo">로고</div>
      <div>{selectedTab}</div>
      <div>{userName ? `${userName}님` : '로그인 정보'}</div> {/* 사용자 이름 표시 */}
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
              onClick={() => handleNavigation(item.name, item.path)} // 클릭 시 탭 이름과 경로 전달
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

const NavBar = ({ userName }) => {
  const [selectedTab, setSelectedTab] = useState('대시보드'); // 선택된 탭 상태 관리
  const navigate = useNavigate(); // 페이지 이동을 위한 navigate 함수

  const menuItems = [
    { name: '대시보드', path: '/dashboard', content: <div>대시보드 콘텐츠</div> },
    { name: '공부 시작', path: '/studyStart', content: <div>공부 시작 콘텐츠</div> },
    { name: '단원 평가', path: '/evaluation', content: <div>단원 평가 콘텐츠</div> },
    { name: '통계', path: '/statistics', content: <div>통계 콘텐츠</div> },
    { name: '커뮤니티', path: '/community', content: <div>커뮤니티 콘텐츠</div> },
    { name: '팀 학습', path: '/teamStudy', content: <div>팀 학습 콘텐츠</div> },
  ];

  const handleNavigation = (tabName, path) => {
    setSelectedTab(tabName); // 선택된 탭 업데이트
    navigate(path); // 경로로 이동
  };

  const renderContent = () => {
    const selectedItem = menuItems.find((item) => item.name === selectedTab);
    return selectedItem ? selectedItem.content : <div>페이지를 선택하세요.</div>;
  };

  return (
    <div>
      <HeaderBar selectedTab={selectedTab} userName={userName} />
      <div style={{ display: 'flex' }}>
        <SideMenu menuItems={menuItems} handleNavigation={handleNavigation} />
      </div>
    </div>
  );
};

export default NavBar;
