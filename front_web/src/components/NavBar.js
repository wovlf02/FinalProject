import React from 'react';
import { useNavigate } from 'react-router-dom';

const NavBar = () => {
  const navigate = useNavigate();

  const menuItems = [
    { name: '대시보드', path: '/dashboard' },
    { name: '공부 시작', path: '/StudyStart' },
    { name: '단원 평가', path: '/evaluation' },
    { name: '통계', path: '/statistics' },
    { name: '커뮤니티', path: '/community' },
  ];

  return (
    <div style={{ width: '200px', backgroundColor: '#f8f9fa', padding: '20px' }}>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {menuItems.map((item) => (
          <li key={item.name} style={{ marginBottom: '10px' }}>
            <button
              onClick={() => navigate(item.path)}
              style={{
                width: '100%',
                padding: '10px',
                textAlign: 'left',
                backgroundColor: 'transparent',
                border: 'none',
                cursor: 'pointer',
              }}
            >
              {item.name}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default NavBar;
