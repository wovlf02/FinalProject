import React, {useState} from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import '../css/UnitEvaluationSchedule.css';

const initialSchedules = [
    {id: 1, date: '2025-05-20', title: '단원평가 준비 시작', done: false},
    {id: 2, date: '2025-05-25', title: '모의고사 실시', done: true},
    {id: 3, date: '2025-05-30', title: '단원평가 시험일', done: false},
    {id: 4, date: '2025-05-22', title: '친구와 약속', done: false},
];

function formatDate(date) {
    return date.toISOString().slice(0, 10);
}

function getDDay(targetDate) {
    const today = new Date();
    const t = new Date(targetDate);
    t.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);
    const diff = Math.ceil((t - today) / (1000 * 60 * 60 * 24));
    if (diff === 0) return 'D-day';
    if (diff > 0) return `D-${diff}`;
    return `D+${Math.abs(diff)}`;
}

const UnitEvaluationSchedule = () => {
    const [value, setValue] = useState(new Date());
    const [schedules, setSchedules] = useState(initialSchedules);
    const [newTitle, setNewTitle] = useState('');
    const [newDate, setNewDate] = useState(formatDate(new Date()));

    // 오늘/선택 날짜 일정 필터
    const selectedDate = formatDate(value);
    const filteredSchedules = schedules.filter(sch => sch.date === selectedDate);

    // 7일 이내 다가오는 일정
    const today = new Date();
    const upcoming = schedules
        .filter(sch => {
            const diff = (new Date(sch.date) - today) / (1000 * 60 * 60 * 24);
            return diff >= 0 && diff <= 7;
        })
        .sort((a, b) => new Date(a.date) - new Date(b.date));

    // 일정 추가
    const handleAddSchedule = (e) => {
        e.preventDefault();
        if (!newTitle.trim()) return;
        setSchedules([
            ...schedules,
            {
                id: Date.now(),
                date: newDate,
                title: newTitle,
                done: false,
            }
        ]);
        setNewTitle('');
        setNewDate(selectedDate);
    };

    // 일정 삭제
    const handleDelete = (id) => {
        setSchedules(schedules.filter(sch => sch.id !== id));
    };

    // 일정 완료 체크
    const handleDone = (id) => {
        setSchedules(schedules.map(sch =>
            sch.id === id ? {...sch, done: !sch.done} : sch
        ));
    };

    // 달력에 일정 있는 날 ● 표시
    const tileContent = ({date, view}) => {
        if (view === 'month') {
            const hasSchedule = schedules.some(sch => sch.date === formatDate(date));
            return hasSchedule ? <span className="calendar-dot">●</span> : null;
        }
        return null;
    };

    return (
        <div className="schedule-container">
            <h2>단원평가 일정</h2>
            <div className="schedule-main-row">
                <div className="schedule-calendar-col">
                    <Calendar
                        onChange={setValue}
                        value={value}
                        tileContent={tileContent}
                        locale="ko"
                        calendarType="iso8601"
                        className="big-calendar"
                    />
                    <div className="schedule-list-section">
                        <div className="schedule-list-title">
                            {selectedDate} 일정 ({filteredSchedules.length}건)
                        </div>
                        {/* 인라인 일정 추가 폼 - 큼직하게 가로 버튼 */}
                        <form className="schedule-add-inline" onSubmit={handleAddSchedule}>
                            <input
                                type="date"
                                value={newDate}
                                onChange={e => setNewDate(e.target.value)}
                                className="schedule-add-date big"
                                required
                            />
                            <input
                                type="text"
                                value={newTitle}
                                onChange={e => setNewTitle(e.target.value)}
                                placeholder="일정 내용을 자유롭게 입력하세요 (예: 시험, 약속, 숙제...)"
                                maxLength={32}
                                className="schedule-add-title big"
                                required
                            />
                            <button type="submit" className="schedule-add-btn-inline big">＋</button>
                        </form>
                        <ul className="schedule-list">
                            {filteredSchedules.length === 0 ? (
                                <li className="schedule-empty">일정이 없습니다.</li>
                            ) : (
                                filteredSchedules.map(sch => (
                                    <li key={sch.id} className={`schedule-item${sch.done ? ' done' : ''}`}>
                                        <label className="schedule-check-label">
                                            <input
                                                type="checkbox"
                                                checked={sch.done}
                                                onChange={() => handleDone(sch.id)}
                                            />
                                            <span className="schedule-title">{sch.title}</span>
                                        </label>
                                        <button className="schedule-del-btn" onClick={() => handleDelete(sch.id)}>삭제
                                        </button>
                                    </li>
                                ))
                            )}
                        </ul>
                    </div>
                </div>
                <div className="schedule-upcoming-col">
                    <div className="schedule-upcoming-title">다가오는 일정 (D-7 ~ D-day)</div>
                    <ul className="schedule-upcoming-list">
                        {upcoming.length === 0 ? (
                            <li className="schedule-empty">7일 이내 일정이 없습니다.</li>
                        ) : (
                            upcoming.map(sch => (
                                <li key={sch.id} className="schedule-upcoming-item">
                                    <div className="schedule-upcoming-row">
                                        <span className="schedule-upcoming-dday">{getDDay(sch.date)}</span>
                                        <span className="schedule-upcoming-title2">{sch.title}</span>
                                    </div>
                                    <div className="schedule-upcoming-info">
                                        <span>{sch.date}</span>
                                    </div>
                                </li>
                            ))
                        )}
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default UnitEvaluationSchedule;
