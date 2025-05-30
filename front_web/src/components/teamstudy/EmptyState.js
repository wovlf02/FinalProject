// src/components/teamstudy/EmptyState.js
import React from 'react';
import '../../css/TeamStudy.css';

const EmptyState = ({ message = '표시할 팀방이 없습니다.', icon = '📭', className = '' }) => {
    return (
        <div className={`empty-state ${className}`}>
            <div className="empty-icon">{icon}</div>
            <p className="empty-message">{message}</p>
        </div>
    );
};

export default EmptyState;
