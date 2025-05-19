import React from 'react';
import Calendar from 'react-calendar';

const DashboardCalendar = ({ selectedDate, setSelectedDate }) => {
  return (
    <div className="dashboard-card dashboard-calendar-card">
      <Calendar
        onChange={setSelectedDate}
        value={selectedDate}
        locale="ko-KR"
        calendarType="gregory"
        formatDay={(locale, date) => date.getDate()}
        tileClassName={({ date, view }) => {
          const highlight = [
            [2025, 8, 12],
            [2025, 8, 13],
            [2025, 8, 14],
            [2025, 8, 19],
            [2025, 8, 20],
            [2025, 8, 21],
          ];
          if (
            view === 'month' &&
            highlight.some(
              ([y, m, d]) =>
                date.getFullYear() === y &&
                date.getMonth() === m &&
                date.getDate() === d
            )
          ) {
            return 'react-calendar__tile--active';
          }
          return null;
        }}
      />
    </div>
  );
};

export default DashboardCalendar;