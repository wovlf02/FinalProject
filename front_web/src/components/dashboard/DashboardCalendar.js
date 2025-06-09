import React, { useState, useEffect } from 'react';
import moment from 'moment';
import api from '../../api/api';
import './DashboardCalendar.css';

/**
 * DashboardCalendar
 *
 * @param {Date} selectedDate - 현재 선택된 날짜
 * @param {function} setSelectedDate - 날짜 선택 변경 핸들러
 * @param {string[]} highlightedDates - 하이라이트할 날짜 배열 (형식: 'YYYY-MM-DD')
 */
const DashboardCalendar = ({ selectedDate: propSelectedDate, setSelectedDate: propSetSelectedDate, highlightedDates = [] }) => {
    const [currentDate, setCurrentDate] = useState(moment());
    const [todos, setTodos] = useState([]);
    const [examSchedules, setExamSchedules] = useState([]);
    const [selectedDate, setSelectedDate] = useState(propSelectedDate ? moment(propSelectedDate) : null);

    // 할일 목록을 가져오는 함수
    const fetchTodos = async () => {
        try {
            const response = await api.get('/dashboard/todos');
            setTodos(response.data);
        } catch (error) {
            console.error('Failed to fetch todos:', error);
        }
    };

    // 시험 일정을 가져오는 함수
    const fetchExamSchedules = async () => {
        try {
            const response = await api.get('/dashboard/exams');
            if (response.data && response.data.success) {
                setExamSchedules(response.data.data || []);
            } else {
                setExamSchedules([]);
            }
        } catch (error) {
            console.error('Failed to fetch exam schedules:', error);
            setExamSchedules([]);
        }
    };

    // 컴포넌트 마운트 시와 월이 변경될 때 할일 목록과 시험 일정을 가져옵니다
    useEffect(() => {
        fetchTodos();
        fetchExamSchedules();
    }, [currentDate]);

    // 1분마다 할일 목록과 시험 일정을 새로고침합니다
    useEffect(() => {
        const interval = setInterval(() => {
            fetchTodos();
            fetchExamSchedules();
        }, 60000);
        return () => clearInterval(interval);
    }, []);

    // propSelectedDate가 변경될 때 내부 state를 업데이트합니다
    useEffect(() => {
        if (propSelectedDate) {
            setSelectedDate(moment(propSelectedDate));
        }
    }, [propSelectedDate]);

    const handleDateSelect = (date) => {
        const momentDate = moment(date);
        setSelectedDate(momentDate);
        if (propSetSelectedDate) {
            propSetSelectedDate(momentDate);
        }
    };

    const renderCalendar = () => {
        const startOfMonth = currentDate.clone().startOf('month');
        const endOfMonth = currentDate.clone().endOf('month');
        const startDate = startOfMonth.clone().startOf('week');
        const endDate = endOfMonth.clone().endOf('week');
        const days = [];
        let day = startDate;

        while (day <= endDate) {
            const todosForDay = todos.filter(todo => 
                moment(todo.date).format('YYYY-MM-DD') === day.format('YYYY-MM-DD')
            );

            const examsForDay = Array.isArray(examSchedules) ? examSchedules.filter(exam => 
                moment(exam.exam_date).format('YYYY-MM-DD') === day.format('YYYY-MM-DD')
            ) : [];

            days.push(
                <div
                    key={day.format('YYYY-MM-DD')}
                    className={`calendar-day ${day.month() !== currentDate.month() ? 'other-month' : ''} ${selectedDate && day.format('YYYY-MM-DD') === selectedDate.format('YYYY-MM-DD') ? 'selected' : ''}`}
                    onClick={() => handleDateSelect(day)}
                >
                    <div className="day-number">{day.format('D')}</div>
                    <div className="day-todos">
                        {todosForDay.map(todo => (
                            <div key={todo.id} className={`todo-item priority-${todo.priority}`}>
                                {todo.title}
                            </div>
                        ))}
                        {examsForDay.map(exam => (
                            <div key={exam.id} className="todo-item exam-item">
                                {exam.title}
                            </div>
                        ))}
                    </div>
                </div>
            );
            day = day.clone().add(1, 'day');
        }

        return days;
    };

    return (
        <div className="dashboard-calendar">
            <div className="calendar-header">
                <button onClick={() => setCurrentDate(currentDate.clone().subtract(1, 'month'))}>
                    &lt;
                </button>
                <h2>{currentDate.format('YYYY년 MM월')}</h2>
                <button onClick={() => setCurrentDate(currentDate.clone().add(1, 'month'))}>
                    &gt;
                </button>
            </div>
            <div className="calendar-weekdays">
                {['일', '월', '화', '수', '목', '금', '토'].map(day => (
                    <div key={day} className="weekday">{day}</div>
                ))}
            </div>
            <div className="calendar-grid">
                {renderCalendar()}
            </div>
        </div>
    );
};

export default DashboardCalendar;
