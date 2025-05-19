import React from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import moment from 'moment';

/**
 * DashboardCalendar
 *
 * @param {Date} selectedDate - 현재 선택된 날짜
 * @param {function} setSelectedDate - 날짜 선택 변경 핸들러
 * @param {string[]} highlightedDates - 하이라이트할 날짜 배열 (형식: 'YYYY-MM-DD')
 */
const DashboardCalendar = ({ selectedDate, setSelectedDate, highlightedDates = [] }) => {
    return (
        <div className="dashboard-card dashboard-calendar-card">
            <Calendar
                onChange={setSelectedDate}
                value={selectedDate}
                locale="ko-KR"
                calendarType="gregory"
                formatDay={(locale, date) => date.getDate()}
                tileClassName={({ date, view }) => {
                    if (view === 'month') {
                        const formatted = moment(date).format('YYYY-MM-DD');
                        return highlightedDates.includes(formatted)
                            ? 'react-calendar__tile--highlight'
                            : null;
                    }
                    return null;
                }}
            />
        </div>
    );
};

export default DashboardCalendar;
