import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import NavBar from './components/NavBar';
import UnitEvaluation from './pages/UnitEvaluation';
import UnitEvaluationStart from './pages/UnitEvaluationStart';
import Dashboard from './pages/dashboard';
import TeamStudy from './pages/TeamStudy';
import StudyStart from './pages/StudyStart';
import PersonalStudy from './pages/PersonalStudy';
import CamStudyPage from './pages/CamStudyPage';
import Login from './pages/Login';
import VideoRoom from './pages/VideoRoom';
import RoomFull from './pages/RoomFull';
import BackendTest from './pages/BackendTest';
import Register from './pages/Register';
import RoomList from './pages/RoomList';
import Evaluation from './pages/evaluation';
import Community from './pages/Community/Community';
import Statistics from './pages/Statistics';
import './css/style.css';

function App() {
  return (
    <Router>
      <div style={{ display: 'flex' }}>
        <NavBar />
        <div style={{ flex: 1, marginTop: '60px', padding: '20px' }}>
          <Routes>
            <Route path="/" element={<Navigate to="/dashboard" />} />
            <Route path="/login" element={<Login />} />
            <Route path="/unit-evaluation" element={<UnitEvaluation />} />
            <Route path="/unit-evaluation/start" element={<UnitEvaluationStart />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/teamStudy" element={<TeamStudy />} />
            <Route path="/studyStart" element={<StudyStart />} />
            <Route path="/personalStudy" element={<PersonalStudy />} />
            <Route path="/study" element={<CamStudyPage />} />
            <Route path="/video-room/:roomId" element={<VideoRoom />} />
            <Route path="/room-full" element={<RoomFull />} />
            <Route path="/backend-test" element={<BackendTest />} />
            <Route path="/register" element={<Register />} />
            <Route path="/rooms" element={<RoomList />} />
            <Route path="/evaluation" element={<Evaluation />} />
            <Route path="/community" element={<Community />} />
            <Route path="/statistics" element={<Statistics />} />
            <Route path="*" element={<Navigate to="/dashboard" />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
