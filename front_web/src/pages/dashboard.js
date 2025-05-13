import React, { useState, useEffect, useRef } from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import '../css/Dashboard.css'; // CSS 경로 주의!

function formatStudyTime(minutes) {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  let result = '';
  if (hours > 0) result += `${hours}시간`;
  if (mins > 0) result += (result ? ' ' : '') + `${mins}분`;
  if (!result) result = '0분';
  return result;
}

function Dashboard() {
  const [selectedDate, setSelectedDate] = useState(new Date());

  // 주간 목표(분), 오늘 목표(분), 오늘 공부(분)
  const [weeklyGoalMinutes, setWeeklyGoalMinutes] = useState(() => {
    return Number(localStorage.getItem('weeklyGoalMinutes')) || 20 * 60;
  });
  const [todayGoalMinutes, setTodayGoalMinutes] = useState(() => {
    return Number(localStorage.getItem('todayGoalMinutes')) || Math.floor((Number(localStorage.getItem('weeklyGoalMinutes')) || 20 * 60) / 7);
  });
  const [todayStudyMinutes, setTodayStudyMinutes] = useState(() => {
    const todayKey = 'todayStudyMinutes-' + new Date().toISOString().slice(0,10);
    return Number(localStorage.getItem(todayKey)) || 0;
  });

  // 오늘의 할 일
  const [todos, setTodos] = useState(() => {
    const saved = localStorage.getItem('todos');
    return saved ? JSON.parse(saved) : [
      { text: "수학 모두노트", done: true },
      { text: "1차 사전예측 복습", done: true },
      { text: "영어 듣기 평가+책", done: true },
      { text: "영단어 DAY6 외우기", done: false },
    ];
  });
  const [newTodo, setNewTodo] = useState('');

  // 남은 공부시간 계산
  const todayRemainMinutes = Math.max(todayGoalMinutes - todayStudyMinutes, 0);
  const weekRemainMinutes = Math.max(weeklyGoalMinutes - todayStudyMinutes, 0);

  // 저장
  useEffect(() => {
    localStorage.setItem('weeklyGoalMinutes', weeklyGoalMinutes);
  }, [weeklyGoalMinutes]);
  useEffect(() => {
    localStorage.setItem('todayGoalMinutes', todayGoalMinutes);
  }, [todayGoalMinutes]);
  useEffect(() => {
    const todayKey = 'todayStudyMinutes-' + new Date().toISOString().slice(0,10);
    localStorage.setItem(todayKey, todayStudyMinutes);
  }, [todayStudyMinutes]);
  useEffect(() => {
    localStorage.setItem('todos', JSON.stringify(todos));
  }, [todos]);

  // 할 일 추가/토글/삭제
  const handleAddTodo = (e) => {
    e.preventDefault();
    if (newTodo.trim() === '') return;
    setTodos([...todos, { text: newTodo, done: false }]);
    setNewTodo('');
  };
  const handleToggleTodo = (idx) => {
    setTodos(todos.map((todo, i) =>
      i === idx ? { ...todo, done: !todo.done } : todo
    ));
  };
  const handleDeleteTodo = (idx) => {
    setTodos(todos.filter((_, i) => i !== idx));
  };

  // 공지/성장률 샘플
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

  // 상세설정 카드
  const [showTimeDetail, setShowTimeDetail] = useState(false);
  const detailRef = useRef();

  // 카드 외부 클릭 시 닫기
  useEffect(() => {
    if (!showTimeDetail) return;
    const handleClick = (e) => {
      if (detailRef.current && !detailRef.current.contains(e.target)) {
        setShowTimeDetail(false);
      }
    };
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, [showTimeDetail]);

  // 입력값 핸들러 (시간/분)
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

  // 시간/분 분리값
  const weeklyGoalHour = Math.floor(weeklyGoalMinutes / 60);
  const weeklyGoalMin = weeklyGoalMinutes % 60;
  const todayGoalHour = Math.floor(todayGoalMinutes / 60);
  const todayGoalMin = todayGoalMinutes % 60;
  const todayStudyHour = Math.floor(todayStudyMinutes / 60);
  const todayStudyMin = todayStudyMinutes % 60;

  // D-day 기능 -----------------------
  const [examName, setExamName] = useState(() => localStorage.getItem('examName') || '');
  const [examDate, setExamDate] = useState(() => {
    const saved = localStorage.getItem('examDate');
    return saved ? new Date(saved) : new Date(new Date().setDate(new Date().getDate() + 20));
  });
  const [showExamSetting, setShowExamSetting] = useState(false);
  const [tempExamName, setTempExamName] = useState('');
  const [tempExamDate, setTempExamDate] = useState('');

  // D-day 계산
  const calculateDday = () => {
    if (!examDate) return 0;
    const today = new Date();
    const target = new Date(examDate);
    target.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);
    const diff = target - today;
    return Math.floor(diff / (1000 * 60 * 60 * 24));
  };
  const dDay = calculateDday();

  // D-day 설정 모달 열기
  const openExamSetting = () => {
    setTempExamName(examName);
    setTempExamDate(examDate ? examDate.toISOString().split('T')[0] : '');
    setShowExamSetting(true);
  };

  // D-day 설정 저장
  const saveExamSetting = () => {
    setExamName(tempExamName);
    setExamDate(new Date(tempExamDate));
    localStorage.setItem('examName', tempExamName);
    localStorage.setItem('examDate', tempExamDate);
    setShowExamSetting(false);
  };

  return (
    <div className="dashboard-container">
      <div className="dashboard-title-row">
        <h2 className="dashboard-main-title">학습 대시보드</h2>
        <div className="dashboard-dday">
          {examName ? (
            <>
              <span style={{ marginRight: 8 }}>{examName}</span>
              <span>
                {dDay > 0 ? `D-${dDay}` : dDay === 0 ? 'D-DAY' : `D+${Math.abs(dDay)}`}
              </span>
            </>
          ) : (
            <span>시험일을 설정해주세요</span>
          )}
          <button
            onClick={openExamSetting}
            style={{
              background: 'none',
              border: 'none',
              color: '#2563eb',
              marginLeft: 8,
              cursor: 'pointer'
            }}
            title="시험 설정"
          >
            ✏️
          </button>
        </div>
      </div>

      {/* 시험 설정 모달 */}
      {showExamSetting && (
        <div className="dashboard-modal-overlay">
          <div className="dashboard-modal-card">
            <h3 style={{ marginTop: 0 }}>시험 설정</h3>
            <div className="dashboard-modal-row">
              <label>시험 이름:</label>
              <input
                type="text"
                value={tempExamName}
                onChange={(e) => setTempExamName(e.target.value)}
                placeholder="예: 2026학년도 수능"
              />
            </div>
            <div className="dashboard-modal-row">
              <label>시험 날짜:</label>
              <input
                type="date"
                value={tempExamDate}
                onChange={(e) => setTempExamDate(e.target.value)}
              />
            </div>
            <div className="dashboard-modal-buttons">
              <button onClick={saveExamSetting}>저장</button>
              <button onClick={() => setShowExamSetting(false)}>취소</button>
            </div>
          </div>
        </div>
      )}

      <div className="dashboard-board-grid">
        <div className="dashboard-card dashboard-calendar-card">
          <Calendar
            onChange={setSelectedDate}
            value={selectedDate}
            locale="ko-KR"
            calendarType="gregory"
            formatDay={(locale, date) => date.getDate()}
            tileClassName={({ date, view }) => {
              const highlight = [
                [2025, 8, 12], [2025, 8, 13], [2025, 8, 14],
                [2025, 8, 19], [2025, 8, 20], [2025, 8, 21],
              ];
              if (view === 'month' && highlight.some(([y, m, d]) =>
                date.getFullYear() === y && date.getMonth() === m && date.getDate() === d
              )) {
                return 'react-calendar__tile--active';
              }
              return null;
            }}
          />
        </div>
        <div className="dashboard-card dashboard-time-card">
          <div className="dashboard-time-row">
            <div style={{ color: "#222", fontWeight: 600, fontSize: 18 }}>
              오늘 남은 공부시간
            </div>
            <button
              className="dashboard-time-plus-btn"
              onClick={() => setShowTimeDetail(v => !v)}
              title="상세 설정"
              aria-label="상세 설정"
              tabIndex={0}
            >+</button>
          </div>
          <div className="dashboard-time-value" style={{ marginTop: 10 }}>
            {formatStudyTime(todayRemainMinutes)}
          </div>
          {showTimeDetail && (
            <div
              className="dashboard-time-detail-card"
              ref={detailRef}
            >
              <button
                className="dashboard-time-detail-close"
                onClick={() => setShowTimeDetail(false)}
                title="닫기"
                aria-label="닫기"
              >×</button>
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
                  onChange={e => handleWeeklyGoalChange('hour', e.target.value)}
                />시간
                <input
                  type="number"
                  min={0}
                  max={59}
                  className="dashboard-time-detail-input"
                  value={weeklyGoalMin}
                  onChange={e => handleWeeklyGoalChange('min', e.target.value)}
                />분
              </div>
              <div className="dashboard-time-detail-row">
                <span className="dashboard-time-detail-label">오늘 목표시간</span>
                <input
                  type="number"
                  min={0}
                  max={24}
                  className="dashboard-time-detail-input"
                  value={todayGoalHour}
                  onChange={e => handleTodayGoalChange('hour', e.target.value)}
                />시간
                <input
                  type="number"
                  min={0}
                  max={59}
                  className="dashboard-time-detail-input"
                  value={todayGoalMin}
                  onChange={e => handleTodayGoalChange('min', e.target.value)}
                />분
              </div>
              <div className="dashboard-time-detail-row">
                <span className="dashboard-time-detail-label">오늘 공부한 시간</span>
                <input
                  type="number"
                  min={0}
                  max={24}
                  className="dashboard-time-detail-input"
                  value={todayStudyHour}
                  onChange={e => handleTodayStudyChange('hour', e.target.value)}
                />시간
                <input
                  type="number"
                  min={0}
                  max={59}
                  className="dashboard-time-detail-input"
                  value={todayStudyMin}
                  onChange={e => handleTodayStudyChange('min', e.target.value)}
                />분
              </div>
              <div className="dashboard-time-detail-row" style={{ marginTop: 10 }}>
                <span className="dashboard-time-detail-label" style={{ color: "#2563eb" }}>오늘 남은 공부시간</span>
                <span style={{ color: "#2563eb", fontWeight: 700 }}>
                  {formatStudyTime(todayRemainMinutes)}
                </span>
              </div>
              <div className="dashboard-time-detail-row">
                <span className="dashboard-time-detail-label" style={{ color: "#2563eb" }}>주간 남은 공부시간</span>
                <span style={{ color: "#2563eb", fontWeight: 700 }}>
                  {formatStudyTime(weekRemainMinutes)}
                </span>
              </div>
            </div>
          )}
        </div>
        <div className="dashboard-card dashboard-todo-card">
          <div style={{ color: "#222", fontWeight: 600, marginBottom: 8 }}>
            오늘의 할 일
          </div>
          <form onSubmit={handleAddTodo} style={{ marginBottom: 8 }}>
            <input
              type="text"
              value={newTodo}
              onChange={e => setNewTodo(e.target.value)}
              placeholder="할 일을 입력하세요"
              style={{ width: "70%", marginRight: 8 }}
            />
            <button type="submit">추가</button>
          </form>
          {todos.map((todo, i) => (
            <div
              key={i}
              className={`dashboard-todo-item${todo.done ? ' done' : ''}`}
              style={{ cursor: "pointer" }}
              onClick={() => handleToggleTodo(i)}
            >
              <input
                type="checkbox"
                checked={todo.done}
                readOnly
                style={{ marginRight: 8 }}
                onClick={e => e.stopPropagation()}
              />
              <span style={{ flex: 1 }}>{todo.text}</span>
              <button
                className="dashboard-todo-delete-btn"
                onClick={e => {
                  e.stopPropagation();
                  handleDeleteTodo(i);
                }}
                aria-label="할 일 삭제"
                title="삭제"
              >🗑️</button>
            </div>
          ))}
        </div>
        <div className="dashboard-card dashboard-notice-card">
          <div style={{ fontWeight: 600, marginBottom: 8 }}>공지사항</div>
          <ul className="dashboard-notice-list">
            {notices.map((n, i) => (
              <li key={i} className={`type-${n.type}`}>
                <span>[{n.type}]</span>
                {n.text}
                <span style={{ float: "right", color: "#bbb", fontWeight: 400, fontSize: 13 }}>{n.date}</span>
              </li>
            ))}
          </ul>
        </div>
        <div className="dashboard-card dashboard-growth-card">
          <div style={{ fontWeight: 600, marginBottom: 8 }}>주간 성장률</div>
          {growth.map((g, i) => (
            <div key={i} style={{ marginBottom: 10 }}>
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <span>{g.subject}</span>
                <span style={{ color: "#2563eb", fontWeight: 600 }}>+{g.rate}%</span>
              </div>
              <div className="dashboard-growth-bar-bg">
                <div className="dashboard-growth-bar" style={{ width: `${g.rate * 2}%` }} />
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
