// src/App.js
import React, { useState } from 'react';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
  Outlet
} from 'react-router-dom';

import NavBar from './components/NavBar';
import UnitEvaluation from './pages/UnitEvaluation';
import UnitEvaluationStart from './pages/UnitEvaluationStart';
import Dashboard from './pages/Dashboard';
import TeamList from './pages/TeamList';               // ← 팀 선택 페이지
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
import MyPage from './pages/MyPage';
import QuizResult from './pages/QuizResult';
import UnitEvaluationPlan from './pages/UnitEvaluationPlan';
import UnitEvaluationFeedback from './pages/UnitEvaluationFeedback';
import UnitEvaluationSchedule from './pages/UnitEvaluationSchedule';
import './css/style.css';
import Post from "./pages/Community/Post";
import Notice from "./pages/Community/Notice";
import Chat from "./pages/Community/Chat";
import Friend from "./pages/Community/Friend";
import PostWritePage from './pages/Community/components/PostWritePage';
import PostDetail from './pages/Community/components/PostDetail';
import StudyListPage from './pages/Community/components/StudyListPage';
import StudyDetail from './pages/Community/components/StudyDetail';
import StudyCreatePage from './pages/Community/components/StudyCreatePage';

const initialStudyList = [];

const LayoutWithSidebar = () => (
  <div style={{ display: 'flex' }}>
    <NavBar />
    <div style={{ flex: 1, marginTop: 0 }}>
      <Outlet />
    </div>
  </div>
);

function App() {
  const [posts, setPosts] = useState([]);
  const [studyList, setStudyList] = useState(initialStudyList);

  const handleAddPost = newPost => {
    setPosts([newPost, ...posts]);
  };

  return (
    <Router>
      <Routes>

        {/* public */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* protected + sidebar */}
        <Route element={<LayoutWithSidebar />}>

          {/* 기본 주소는 로그인으로 */}
          <Route path="/" element={<Navigate to="/login" replace />} />

          {/* — 이 아래부터 기존 페이지들 — */}
          <Route path="/unit-evaluation" element={<UnitEvaluation />} />
          <Route path="/unit-evaluation/start" element={<UnitEvaluationStart />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/teamStudy" element={<TeamStudy />} />
          <Route path="/studyStart" element={<StudyStart />} />
          <Route path="/personalStudy" element={<PersonalStudy />} />
          <Route path="/study" element={<StudyListPage studyList={studyList} />} />
          <Route path="/study/:id" element={<StudyDetail studyList={studyList} />} />
          <Route path="/study/create" element={<StudyCreatePage setStudyList={setStudyList} />} />
          <Route path="/camstudy" element={<CamStudyPage />} />
          <Route path="/video-room/:roomId" element={<VideoRoom />} />
          <Route path="/room-full" element={<RoomFull />} />
          <Route path="/backend-test" element={<BackendTest />} />
          <Route path="/rooms" element={<RoomList />} />
          <Route path="/evaluation" element={<Evaluation />} />
          <Route path="/community" element={<Community />} />
          <Route path="/community/notice" element={<Notice />} />
          <Route path="/community/post" element={<Post posts={posts} setPosts={setPosts} studyList={studyList} />} />
          <Route path="/community/post/:id" element={<PostDetail posts={posts} setPosts={setPosts} />} />
          <Route path="/community/chat" element={<Chat />} />
          <Route path="/community/friend" element={<Friend />} />
          <Route path="/statistics" element={<Statistics />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/write" element={<PostWritePage onAddPost={handleAddPost} />} />
          <Route path="/quiz-result" element={<QuizResult />} />
          <Route path="/unit-evaluation/plan" element={<UnitEvaluationPlan />} />
          <Route path="/unit-evaluation/feedback" element={<UnitEvaluationFeedback />} />
          <Route path="/unit-evaluation/schedule" element={<UnitEvaluationSchedule />} />

          {/* — 여기에 새로 추가된 팀 플로우 — */}
          <Route path="/teams" element={<TeamList />} />
          <Route path="/team-study/:teamId" element={<TeamStudy />} />

          {/* 없는 경로는 대시보드로 */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
