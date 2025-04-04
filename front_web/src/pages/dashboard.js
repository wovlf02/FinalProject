import React, { useState } from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';

const Dashboard = () => {
  const [selectedDate, setSelectedDate] = useState(new Date());

  const handleDateChange = (date) => {
    setSelectedDate(date);
    console.log('Selected date:', date);
  };

  return (
    <div className="dashboard" style={{ padding: '20px' }}>
      <h1>학습 대시보드</h1>
      <div className="calendar-container" style={{ marginBottom: '20px' }}>
        <Calendar onChange={handleDateChange} value={selectedDate} />
      </div>
      <div>
        <h2>선택된 날짜: {selectedDate.toLocaleDateString()}</h2>
        {/* 추가적인 대시보드 요소를 여기에 추가할 수 있습니다. */}
      </div>
    </div>
  );
};

export default Dashboard;
