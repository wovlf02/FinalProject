import React from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';

function DashboardCalendar({ selectedDate, setSelectedDate }) {
    return (
        <div className="dashboard-card dashboard-calendar-card">
            <Calendar
                onChange={setSelectedDate}
                value={selectedDate}
                locale="ko-KR"
                calendarType="gregory"
                formatDay={(locale, date) => date.getDate()}
            />
        </div>
    );
}

export default DashboardCalendar;