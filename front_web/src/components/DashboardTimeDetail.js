import React, { useRef, useEffect } from 'react';

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
}) => {
  const detailRef = useRef();

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (detailRef.current && !detailRef.current.contains(e.target)) {
        setShowTimeDetail(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [setShowTimeDetail]);

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
        <span className="dashboard-time-detail-label" style={{ color: '#2563eb' }}>
          오늘 남은 공부시간
        </span>
        <span style={{ color: '#2563eb', fontWeight: 700 }}>
          {todayRemainMinutes}분
        </span>
      </div>
      <div className="dashboard-time-detail-row">
        <span className="dashboard-time-detail-label" style={{ color: '#2563eb' }}>
          주간 남은 공부시간
        </span>
        <span style={{ color: '#2563eb', fontWeight: 700 }}>
          {weekRemainMinutes}분
        </span>
      </div>
    </div>
  );
};

export default DashboardTimeDetail;