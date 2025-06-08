import React, { useState } from 'react';
import DashboardCalendar from './DashboardCalendar';
import DashboardTodo from './DashboardTodo';
import './Dashboard.css';

const Dashboard = () => {
  const [selectedDate, setSelectedDate] = useState(null);

  const handleDateSelect = (date) => {
    setSelectedDate(date);
  };

  return (
    <div className="dashboard">
      <div className="dashboard-row">
        <div className="dashboard-col">
          <DashboardCalendar
            selectedDate={selectedDate}
            setSelectedDate={setSelectedDate}
          />
        </div>
        <div className="dashboard-col">
          <DashboardTodo onDateSelect={handleDateSelect} />
        </div>
      </div>
    </div>
  );
};

export default Dashboard; 