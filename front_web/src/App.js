import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'; // Navigate 컴포넌트 추가
import NavBar from './components/NavBar';
import Evaluation from './pages/evaluation';
import Dashboard from './pages/dashboard';
import TeamStudy from './pages/TeamStudy';
import StudyStart from './pages/StudyStart';
import PersonalStudy from './pages/PersonalStudy';
import Login from './pages/Login'; // 로그인 페이지 추가
import VideoRoom from './pages/VideoRoom'; // VideoRoom 컴포넌트 추가
import RoomFull from './pages/RoomFull'; // RoomFull 페이지 추가

function App() {
  return (
    <Router>
      <div style={{ display: 'flex' }}>
        <NavBar />
        <div style={{ flex: 1, marginTop: '60px', padding: '20px' }}>
          <Routes>
            <Route path="/" element={<Navigate to="/login" />} /> {/* 기본 페이지 설정 */}
            <Route path="/login" element={<Login />} /> {/* 로그인 라우트 추가 */}
            <Route path="/evaluation" element={<Evaluation />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/teamStudy" element={<TeamStudy />} />
            <Route path="/studyStart" element={<StudyStart />} />
            <Route path="/personalStudy" element={<PersonalStudy />} />
            <Route path="/video-room/:roomId" element={<VideoRoom />} /> {/* 화상 채팅 방 라우트 추가 */}
            <Route path="/room-full" element={<RoomFull />} /> {/* RoomFull 라우트 추가 */}
            {/* Add routes for other pages */}
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
