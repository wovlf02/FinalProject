import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import NavBar from './components/NavBar';
import UnitEvaluation from './pages/UnitEvaluation';
import UnitEvaluationStart from './pages/UnitEvaluationStart';
import Dashboard from './pages/Dashboard';
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
import Post from './pages/Community/Post';
import Notice from './pages/Community/Notice';
import Chat from './pages/Community/Chat';
import Friend from './pages/Community/Friend';
import PostWritePage from './pages/Community/components/PostWritePage';
import PostDetail from './pages/Community/components/PostDetail';
import StudyListPage from './pages/Community/components/StudyListPage';
import StudyDetail from './pages/Community/components/StudyDetail';
import StudyCreatePage from './pages/Community/components/StudyCreatePage';
import UnitEvaluationPlanList from './pages/UnitEvaluationPlanList';
import PlanMenu from './pages/PlanMenu';

// ✅ 새로 추가된 팀 학습 관련 페이지
import QuizRoom from './pages/QuizRoom';
import FocusRoom from './pages/FocusRoom';

// 초기 스터디 목록
const initialStudyList = [];

const LayoutWithSidebar = () => (
    <div style={{ display: 'flex' }}>
        <NavBar />
        <div style={{ flex: 1, marginTop: '0px' }}>
            <Outlet />
        </div>
    </div>
);

function App() {
    const [posts, setPosts] = useState([]);
    const [studyList, setStudyList] = useState(initialStudyList);

    const handleAddPost = (newPost) => {
        setPosts([newPost, ...posts]);
    };

    return (
        <Router>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route element={<LayoutWithSidebar />}>
                    <Route path="/" element={<Navigate to="/login" />} />
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
                    <Route path="/plan/menu" element={<PlanMenu />} />
                    <Route path="/plan/create" element={<UnitEvaluationPlan />} />
                    <Route path="/plan/list" element={<UnitEvaluationPlanList />} />
                    <Route path="/unit-evaluation/plan" element={<UnitEvaluationPlan />} />
                    <Route path="/unit-evaluation/feedback" element={<UnitEvaluationFeedback />} />
                    <Route path="/unit-evaluation/schedule" element={<UnitEvaluationSchedule />} />

                    {/* ✅ 팀 학습 방 진입 URL 통일 */}
                    <Route path="/team-study/quiz/:roomId" element={<QuizRoom />} />
                    <Route path="/team-study/focus/:roomId" element={<FocusRoom />} />

                    <Route path="*" element={<Navigate to="/dashboard" />} />
                </Route>
            </Routes>
        </Router>
    );
}

export default App;
