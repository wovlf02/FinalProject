import React, { useState, useEffect } from 'react';
import '../css/Dashboard.css';
import DashboardCalendar from "../components/dashboard/DashboardCalendar";
import DashboardDday from "../components/dashboard/DashboardDday";
import DashboardGrowth from "../components/dashboard/DashboardGrowth";
import DashboardNotice from "../components/dashboard/DashboardNotice";
import DashboardTimeDetail from "../components/dashboard/DashboardTimeDetail";
import DashboardTodo from "../components/dashboard/DashboardTodo";
import api from '../api/api';
import moment from 'moment';

function Dashboard() {
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [highlightedDates, setHighlightedDates] = useState([]);

    const [weeklyGoalMinutes, setWeeklyGoalMinutes] = useState(20 * 60);
    const [todayGoalMinutes, setTodayGoalMinutes] = useState(Math.floor(weeklyGoalMinutes / 7));
    const [todayStudyMinutes, setTodayStudyMinutes] = useState(0);

    const [showTimeDetail, setShowTimeDetail] = useState(false); // ✅ 상세 설정 토글

    // ✅ 시/분 분리
    const weeklyGoalHour = Math.floor(weeklyGoalMinutes / 60);
    const weeklyGoalMin = weeklyGoalMinutes % 60;
    const todayGoalHour = Math.floor(todayGoalMinutes / 60);
    const todayGoalMin = todayGoalMinutes % 60;
    const todayStudyHour = Math.floor(todayStudyMinutes / 60);
    const todayStudyMin = todayStudyMinutes % 60;

    const todayRemainMinutes = Math.max(todayGoalMinutes - todayStudyMinutes, 0);
    const weekRemainMinutes = Math.max(weeklyGoalMinutes - todayStudyMinutes, 0); // 필요 시 누적값으로 변경

    // ✅ 캘린더 하이라이트
    useEffect(() => {
        const fetchCalendarHighlights = async () => {
            try {
                const yearMonth = moment(selectedDate).format('YYYY-MM');
                const res = await api.get(`/dashboard/calendar`, {
                    params: { month: yearMonth }
                });
                const dates = res.data.map(event => event.date);
                setHighlightedDates(dates);
            } catch (err) {
                console.error("캘린더 일정 조회 실패:", err);
            }
        };

        fetchCalendarHighlights();
    }, [selectedDate]);

    // ✅ 시간 입력 핸들러
    const handleWeeklyGoalChange = (type, value) => {
        const hour = type === 'hour' ? Number(value) : Math.floor(weeklyGoalMinutes / 60);
        const min = type === 'min' ? Number(value) : weeklyGoalMinutes % 60;
        setWeeklyGoalMinutes(hour * 60 + min);
    };

    const handleTodayGoalChange = (type, value) => {
        const hour = type === 'hour' ? Number(value) : Math.floor(todayGoalMinutes / 60);
        const min = type === 'min' ? Number(value) : todayGoalMinutes % 60;
        setTodayGoalMinutes(hour * 60 + min);
    };

    const handleTodayStudyChange = (type, value) => {
        const hour = type === 'hour' ? Number(value) : Math.floor(todayStudyMinutes / 60);
        const min = type === 'min' ? Number(value) : todayStudyMinutes % 60;
        setTodayStudyMinutes(hour * 60 + min);
    };

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

            {/* ✅ D-Day 영역 */}
            <div style={{ marginBottom: '12px' }}>
                <DashboardDday />
            </div>

            <div className="dashboard-board-grid">
                <DashboardCalendar
                    selectedDate={selectedDate}
                    setSelectedDate={setSelectedDate}
                    highlightedDates={highlightedDates}
                />

                {/* ✅ 시간 박스 (버튼만) */}
                <div className="dashboard-card">
                    <div style={{ fontWeight: 600, marginBottom: 8 }}>공부 시간</div>
                    <div>오늘 공부한 시간: {todayStudyHour}시간 {todayStudyMin}분</div>
                    <div>오늘 목표 시간: {todayGoalHour}시간 {todayGoalMin}분</div>
                    <div>주간 목표 시간: {weeklyGoalHour}시간 {weeklyGoalMin}분</div>
                    <button onClick={() => setShowTimeDetail(true)} style={{ marginTop: 8 }}>
                        상세 설정
                    </button>
                </div>

                {/* ✅ 상세 설정 모달 (조건부 렌더링) */}
                {showTimeDetail && (
                    <DashboardTimeDetail
                        weeklyGoalHour={weeklyGoalHour}
                        weeklyGoalMin={weeklyGoalMin}
                        todayGoalHour={todayGoalHour}
                        todayGoalMin={todayGoalMin}
                        todayStudyHour={todayStudyHour}
                        todayStudyMin={todayStudyMin}
                        todayRemainMinutes={todayRemainMinutes}
                        weekRemainMinutes={weekRemainMinutes}
                        handleWeeklyGoalChange={handleWeeklyGoalChange}
                        handleTodayGoalChange={handleTodayGoalChange}
                        handleTodayStudyChange={handleTodayStudyChange}
                        setShowTimeDetail={setShowTimeDetail}
                    />
                )}

                <DashboardTodo selectedDate={selectedDate} />
                <DashboardNotice notices={notices} />
                <DashboardGrowth growth={growth} />
            </div>
        </div>
    );
}

export default Dashboard;
