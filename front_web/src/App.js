import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import NavBar from './components/NavBar';
import Evaluation from './pages/evaluation';
import Dashboard from './pages/dashboard';
import TeamStudy from './pages/TeamStudy';
import StudyStart from './pages/StudyStart';
import PersonalStudy from './pages/PersonalStudy';
import Login from './pages/Login';
import VideoRoom from './pages/VideoRoom';
import RoomFull from './pages/RoomFull';
import BackendTest from './pages/BackendTest';
import Register from './pages/Register';

function App() {

  const [name, setName] = useState('');         // 이름
  const [selectedTab, setSelectedTab] = useState('대시보드'); // 선택된 탭 상태
  return (
    <Router>
      <div style={{ display: 'flex' }}>
      <NavBar setSelectedTab={setSelectedTab} userName={name} /> {/* NavBar 컴포넌트 추가 */}
        <div style={{ flex: 1, marginTop: '60px', padding: '20px' }}>
          <Routes>
          
            <Route path="/" element={<Navigate to="/login" />} />
            <Route path="/login" element={<Login setName={setName} />} /> {/* 로그인 페이지 라우트 추가 */}
            <Route path="/evaluation" element={<Evaluation />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/teamStudy" element={<TeamStudy />} />
            <Route path="/studyStart" element={<StudyStart />} />
            <Route path="/personalStudy" element={<PersonalStudy />} />
            <Route path="/video-room/:roomId" element={<VideoRoom />} /> {/* 화상 채팅 방 라우트 추가 */}
            <Route path="/room-full" element={<RoomFull />} /> {/* RoomFull 라우트 추가 */}
            <Route path="/backend-test" element={<BackendTest />} /> {/* 테스트 페이지 라우트 추가 */}
            <Route path="/register" element={<Register />} /> {/* 회원가입 페이지 라우트 추가 */}
            <Route path="*" element={<Navigate to="/login" />} /> {/* 잘못된 경로 접근 시 로그인 페이지로 리다이렉트 */}
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
