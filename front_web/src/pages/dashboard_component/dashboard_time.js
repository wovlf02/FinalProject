import React, { useRef, useState, useEffect } from 'react';

function formatStudyTime(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    let result = '';
    if (hours > 0) result += `${hours}시간`;
    if (mins > 0) result += (result ? ' ' : '') + `${mins}분`;
    if (!result) result = '0분';
    return result;
}

function DashboardTime({
    weeklyGoalMinutes,
    todayGoalMinutes,
    todayStudyMinutes,
    setWeeklyGoalMinutes,
    setTodayGoalMinutes,
    setTodayStudyMinutes,
}) {
    const [showTimeDetail, setShowTimeDetail] = useState(false);
    const detailRef = useRef();

    const todayRemainMinutes = Math.max(todayGoalMinutes - todayStudyMinutes, 0);
    const weekRemainMinutes = Math.max(weeklyGoalMinutes - todayStudyMinutes, 0);

    const weeklyGoalHour = Math.floor(weeklyGoalMinutes / 60);
    const weeklyGoalMin = weeklyGoalMinutes % 60;
    const todayGoalHour = Math.floor(todayGoalMinutes / 60);
    const todayGoalMin = todayGoalMinutes % 60;
    const todayStudyHour = Math.floor(todayStudyMinutes / 60);
    const todayStudyMin = todayStudyMinutes % 60;

    const handleWeeklyGoalChange = (type, value) => {
        let hours = Math.floor(weeklyGoalMinutes / 60);
        let mins = weeklyGoalMinutes % 60;
        if (type === 'hour') hours = Number(value);
        else mins = Number(value);
        setWeeklyGoalMinutes(hours * 60 + mins);
        setTodayGoalMinutes(Math.floor((hours * 60 + mins) / 7));
    };

    const handleTodayGoalChange = (type, value) => {
        let hours = Math.floor(todayGoalMinutes / 60);
        let mins = todayGoalMinutes % 60;
        if (type === 'hour') hours = Number(value);
        else mins = Number(value);
        setTodayGoalMinutes(hours * 60 + mins);
    };

    const handleTodayStudyChange = (type, value) => {
        let hours = Math.floor(todayStudyMinutes / 60);
        let mins = todayStudyMinutes % 60;
        if (type === 'hour') hours = Number(value);
        else mins = Number(value);
        setTodayStudyMinutes(hours * 60 + mins);
    };

    useEffect(() => {
        if (!showTimeDetail) return;
        const handleClickOutside = (e) => {
            if (detailRef.current && !detailRef.current.contains(e.target)) {
                setShowTimeDetail(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [showTimeDetail]);

    return (
        <div className="dashboard-card dashboard-time-card">
            <div className="dashboard-time-row">
                <div style={{ color: "#222", fontWeight: 600, fontSize: 18 }}>
                    오늘 남은 공부시간
                </div>
                <button
                    className="dashboard-time-plus-btn"
                    onClick={() => setShowTimeDetail((v) => !v)}
                    title="상세 설정"
                    aria-label="상세 설정"
                    tabIndex={0}
                >
                    +
                </button>
            </div>
            <div className="dashboard-time-value" style={{ marginTop: 10 }}>
                {formatStudyTime(todayRemainMinutes)}
            </div>
            {showTimeDetail && (
                <div className="dashboard-time-detail-card" ref={detailRef}>
                    <button
                        className="dashboard-time-detail-close"
                        onClick={() => setShowTimeDetail(false)}
                        title="닫기"
                        aria-label="닫기"
                    >
                        ×
                    </button>
                    <div style={{ fontWeight: 600, marginBottom: 12 }}>
                        공부시간 상세 설정
                    </div>
                    <div className="dashboard-time-detail-row">
                        <span className="dashboard-time-detail-label">주간 목표시간</span>
                        <input
                            type="number"
                            min={0}
                            max={168}
                            className="dashboard-time-detail-input"
                            value={weeklyGoalHour}
                            onChange={(e) => handleWeeklyGoalChange('hour', e.target.value)}
                        />
                        시간
                        <input
                            type="number"
                            min={0}
                            max={59}
                            className="dashboard-time-detail-input"
                            value={weeklyGoalMin}
                            onChange={(e) => handleWeeklyGoalChange('min', e.target.value)}
                        />
                        분
                    </div>
                    <div className="dashboard-time-detail-row">
                        <span className="dashboard-time-detail-label">오늘 목표시간</span>
                        <input
                            type="number"
                            min={0}
                            max={24}
                            className="dashboard-time-detail-input"
                            value={todayGoalHour}
                            onChange={(e) => handleTodayGoalChange('hour', e.target.value)}
                        />
                        시간
                        <input
                            type="number"
                            min={0}
                            max={59}
                            className="dashboard-time-detail-input"
                            value={todayGoalMin}
                            onChange={(e) => handleTodayGoalChange('min', e.target.value)}
                        />
                        분
                    </div>
                    <div className="dashboard-time-detail-row">
                        <span className="dashboard-time-detail-label">오늘 공부한 시간</span>
                        <input
                            type="number"
                            min={0}
                            max={24}
                            className="dashboard-time-detail-input"
                            value={todayStudyHour}
                            onChange={(e) => handleTodayStudyChange('hour', e.target.value)}
                        />
                        시간
                        <input
                            type="number"
                            min={0}
                            max={59}
                            className="dashboard-time-detail-input"
                            value={todayStudyMin}
                            onChange={(e) => handleTodayStudyChange('min', e.target.value)}
                        />
                        분
                    </div>
                    <div className="dashboard-time-detail-row" style={{ marginTop: 10 }}>
                        <span
                            className="dashboard-time-detail-label"
                            style={{ color: "#2563eb" }}
                        >
                            오늘 남은 공부시간
                        </span>
                        <span style={{ color: "#2563eb", fontWeight: 700 }}>
                            {formatStudyTime(todayRemainMinutes)}
                        </span>
                    </div>
                    <div className="dashboard-time-detail-row">
                        <span
                            className="dashboard-time-detail-label"
                            style={{ color: "#2563eb" }}
                        >
                            주간 남은 공부시간
                        </span>
                        <span style={{ color: "#2563eb", fontWeight: 700 }}>
                            {formatStudyTime(weekRemainMinutes)}
                        </span>
                    </div>
                </div>
            )}
        </div>
    );
}

export default DashboardTime;