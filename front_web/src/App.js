import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'; // Navigate 컴포넌트 추가
import NavBar from './components/NavBar';
import Evaluation from './pages/evaluation';
import Dashboard from './pages/Dashboard';
import TeamStudy from './pages/TeamStudy';
import StudyStart from './pages/StudyStart';
import PersonalStudy from './pages/PersonalStudy';
import Login from './pages/Login'; // 로그인 페이지 추가
import VideoRoom from './pages/VideoRoom'; // VideoRoom 컴포넌트 추가
import RoomFull from './pages/RoomFull'; // RoomFull 페이지 추가
import BackendTest from './pages/BackendTest';
import Register from './pages/Register'; // 회원가입 페이지 추가
import SelectUnit from './pages/SelectUnit'; // 추가
import ExamView from './pages/ExamView';
import ExamSolve from './pages/ExamSolve';
import Result from './pages/result';
import AIFeedbackPage from './pages/AIFeedbackPage';
import AIFeedbackDetailPage from './pages/AIFeedbackDetailPage';
import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
  const [name, setName] = useState(''); // 이름 상태 추가

  return (
    <Router>
      <div style={{ display: 'flex' }}>
        <NavBar name={name} /> {/* NavBar에 name 전달 */}
        <div style={{ flex: 1, marginTop: '60px', padding: '20px' }}>
          <Routes>
            <Route path="/" element={<Navigate to="/login" />} /> {/* 기본 페이지 설정 */}
            <Route path="/login" element={<Login setName={setName} />} /> {/* Login에 setName 전달 */}
            <Route path="/evaluation" element={<Evaluation />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/teamStudy" element={<TeamStudy />} />
            <Route path="/studyStart" element={<StudyStart />} />
            <Route path="/personalStudy" element={<PersonalStudy />} />
            <Route path="/video-room/:roomId" element={<VideoRoom />} /> {/* 화상 채팅 방 라우트 추가 */}
            <Route path="/room-full" element={<RoomFull />} /> {/* RoomFull 라우트 추가 */}
            <Route path="/backend-test" element={<BackendTest />} /> {/* 테스트 페이지 라우트 추가 */}
            <Route path="/register" element={<Register />} /> {/* 회원가입 페이지 라우트 추가 */}
            <Route path="/select-unit" element={<SelectUnit />} /> {/* 추가 */}
            <Route path="/exam-view" element={<ExamView />} />
            <Route path="/exam-solve" element={<ExamSolve />} />
            <Route path="/result" element={<Result />} />
            <Route path="/ai-feedback" element={<AIFeedbackPage />} />
            <Route path="/ai-feedback/:resultId" element={<AIFeedbackDetailPage />} />
            <Route path="*" element={<Navigate to="/login" />} /> {/* 잘못된 경로 접근 시 로그인 페이지로 리다이렉트 */}
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
