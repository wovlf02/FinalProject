import React, { useRef, useEffect } from 'react';
import api from '../../api/api';

const DashboardTimeDetail = ({
                                 weeklyGoalHour,
                                 weeklyGoalMin,
                                 todayGoalHour,
                                 todayGoalMin,
                                 todayStudyHour,
                                 todayStudyMin,
                                 todayRemainMinutes,
                                 weekRemainMinutes,
                                 handleWeeklyGoalChange,
                                 handleTodayGoalChange,
                                 handleTodayStudyChange,
                                 setShowTimeDetail,
                                 weeklyGoalMinutes,
                                 todayGoalMinutes,
                             }) => {
    const detailRef = useRef();

    // ✅ 외부 클릭 시 닫힘
    useEffect(() => {
        const handleClickOutside = (e) => {
            if (detailRef.current && !detailRef.current.contains(e.target)) {
                setShowTimeDetail(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [setShowTimeDetail]);

    // ✅ 서버로 목표 시간 저장
    const saveStudyGoal = async (weekly, today) => {
        try {
            await api.post('/dashboard/study-time', {
                weeklyGoalMinutes: weekly,
                todayGoalMinutes: today,
            });
        } catch (err) {
            console.error('목표 시간 저장 실패:', err);
        }
    };

    // ✅ 주간 목표 시간 변경 → 상태 + 서버 반영
    const handleWeeklyChange = (e) => {
        const value = e.target.value;
        if (value === '' || /^\d*$/.test(value)) {
            handleWeeklyGoalChange('hour', value === '' ? 0 : parseInt(value));
            handleWeeklyGoalChange('min', value === '' ? 0 : parseInt(value));
            saveStudyGoal(value === '' ? 0 : parseInt(value), todayGoalMinutes);
        }
    };

    // ✅ 오늘 목표 시간 변경 → 상태 + 서버 반영
    const handleTodayChange = (e) => {
        const value = e.target.value;
        if (value === '' || /^\d*$/.test(value)) {
            handleTodayGoalChange('hour', value === '' ? 0 : parseInt(value));
            handleTodayGoalChange('min', value === '' ? 0 : parseInt(value));
            saveStudyGoal(weeklyGoalMinutes, value === '' ? 0 : parseInt(value));
        }
    };

    // ✅ 오늘 공부 시간 (프론트 상태만 변경)
    const handleStudyTimeChange = (e) => {
        const value = e.target.value;
        if (value === '' || /^\d*$/.test(value)) {
            handleTodayStudyChange('hour', value === '' ? 0 : parseInt(value));
            handleTodayStudyChange('min', value === '' ? 0 : parseInt(value));
        }
    };

    return (
        <div className="dashboard-time-detail-card" ref={detailRef}>
            <button
                className="dashboard-time-detail-close"
                onClick={() => setShowTimeDetail(false)}
                title="닫기"
                aria-label="닫기"
            >
                ×
            </button>
            <div style={{ fontWeight: 600, marginBottom: 12 }}>공부시간 상세 설정</div>

            {/* 주간 목표 시간 */}
            <div className="dashboard-time-detail-row">
                <span className="dashboard-time-detail-label">주간 목표시간</span>
                <input
                    type="number"
                    min={0}
                    max={168}
                    className="dashboard-time-detail-input"
                    value={weeklyGoalHour}
                    onChange={handleWeeklyChange}
                />
                시간
                <input
                    type="number"
                    min={0}
                    max={59}
                    className="dashboard-time-detail-input"
                    value={weeklyGoalMin}
                    onChange={handleWeeklyChange}
                />
                분
            </div>

            {/* 오늘 목표 시간 */}
            <div className="dashboard-time-detail-row">
                <span className="dashboard-time-detail-label">오늘 목표시간</span>
                <input
                    type="number"
                    min={0}
                    max={24}
                    className="dashboard-time-detail-input"
                    value={todayGoalHour}
                    onChange={handleTodayChange}
                />
                시간
                <input
                    type="number"
                    min={0}
                    max={59}
                    className="dashboard-time-detail-input"
                    value={todayGoalMin}
                    onChange={handleTodayChange}
                />
                분
            </div>

            {/* 오늘 공부 시간 (프론트 상태만 변경) */}
            <div className="dashboard-time-detail-row">
                <span className="dashboard-time-detail-label">오늘 공부한 시간</span>
                <input
                    type="number"
                    min={0}
                    max={24}
                    className="dashboard-time-detail-input"
                    value={todayStudyHour}
                    onChange={handleStudyTimeChange}
                />
                시간
                <input
                    type="number"
                    min={0}
                    max={59}
                    className="dashboard-time-detail-input"
                    value={todayStudyMin}
                    onChange={handleStudyTimeChange}
                />
                분
            </div>

            {/* 잔여 시간 */}
            <div className="dashboard-time-detail-row" style={{ marginTop: 10 }}>
                <span className="dashboard-time-detail-label" style={{ color: '#2563eb' }}>
                    오늘 남은 공부시간
                </span>
                <span style={{ color: '#2563eb', fontWeight: 700 }}>{todayRemainMinutes}분</span>
            </div>
            <div className="dashboard-time-detail-row">
                <span className="dashboard-time-detail-label" style={{ color: '#2563eb' }}>
                    주간 남은 공부시간
                </span>
                <span style={{ color: '#2563eb', fontWeight: 700 }}>{weekRemainMinutes}분</span>
            </div>
        </div>
    );
};

export default DashboardTimeDetail;
