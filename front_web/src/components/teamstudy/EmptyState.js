import React from 'react';
import '../../css/TeamStudy.css';

const EmptyState = ({
                        message = '표시할 팀방이 없습니다.',
                        icon = '📭',
                        className = ''
                    }) => {
    const containerClass = `empty-state ${className}`.trim();

    return (
        <div className={containerClass} role="alert" aria-live="polite">
            <div className="empty-icon" aria-label="empty-icon">{icon}</div>
            <p className="empty-message">{message}</p>
        </div>
    );
};

export default EmptyState;
