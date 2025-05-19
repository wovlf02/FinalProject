import React, { useState } from 'react';
import '../css/Dashboard.css';
import DashboardTodoList from './dashboard_component/dashboard_todolist';
import DashboardNotice from './dashboard_component/dashboard_notice';
import DashboardWeeklyGrowth from './dashboard_component/dashboard_weeklygrowth';
import DashboardCalendar from './dashboard_component/dashboard_calendar';
import DashboardTime from './dashboard_component/dashboard_time';

function Dashboard() {
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [weeklyGoalMinutes, setWeeklyGoalMinutes] = useState(20 * 60); // 주간 목표 시간 (분)
    const [todayGoalMinutes, setTodayGoalMinutes] = useState(Math.floor(weeklyGoalMinutes / 7)); // 오늘 목표 시간 (분)
    const [todayStudyMinutes, setTodayStudyMinutes] = useState(0); // 오늘 공부한 시간 (분)
    const [todos, setTodos] = useState([
        { text: "수학 모두노트", done: true },
        { text: "1차 사전예측 복습", done: true },
        { text: "영어 듣기 평가+책", done: true },
        { text: "영단어 DAY6 외우기", done: false },
    ]);
    const notices = [
        { type: "중요", text: "2026학년도 9월 모의고사 일정 안내", date: "2025.09.15" },
        { type: "공지", text: "추석 연휴 학습실 운영 안내", date: "2025.09.14" },
        { type: "일반", text: "9월 학부모 상담 신청 안내", date: "2025.09.13" },
    ];
    const growth = [
        { subject: "수학", rate: 12 },
        { subject: "영어", rate: 8 },
        { subject: "국어", rate: 15 },
        { subject: "과학", rate: 5 },
    ];

    return (
        <div className="dashboard-container">
            <h2 className="dashboard-main-title">학습 대시보드</h2>
            <div className="dashboard-board-grid">
                <DashboardCalendar selectedDate={selectedDate} setSelectedDate={setSelectedDate} />
                <DashboardTime
                    weeklyGoalMinutes={weeklyGoalMinutes}
                    todayGoalMinutes={todayGoalMinutes}
                    todayStudyMinutes={todayStudyMinutes}
                    setWeeklyGoalMinutes={setWeeklyGoalMinutes}
                    setTodayGoalMinutes={setTodayGoalMinutes}
                    setTodayStudyMinutes={setTodayStudyMinutes}
                />
                <DashboardTodoList todos={todos} setTodos={setTodos} />
                <DashboardNotice notices={notices} />
                <DashboardWeeklyGrowth growth={growth} />
            </div>
        </div>
    );
}

export default Dashboard;
