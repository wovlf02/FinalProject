import React from 'react';
import '../../../css/FocusRanking.css';

const FocusRanking = ({ ranking }) => {
    return (
        <div className="ranking-box">
            <h3 className="ranking-title">랭킹</h3>
            <ul className="ranking-list">
                {ranking.map((item, idx) => (
                    <li key={item.userId} className="ranking-item">
                        <p>{item.nickname} <span className="rank-num">{idx + 1}</span></p>
                        <p className="rank-time">공부시간: {item.focusedTime || '00:00:00'}</p>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default FocusRanking;
