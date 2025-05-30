// src/components/teamstudy/FocusRankingBoard.js
import React from 'react';
import '../../css/FocusRoom.css';

const FocusRankingBoard = ({ rankings, currentUserId, targetTime = null }) => {
    const formatTime = (seconds) => {
        const min = Math.floor(seconds / 60);
        const sec = seconds % 60;
        return `${min}분 ${sec}초`;
    };

    return (
        <div className="focus-ranking-board">
            <h3>📊 실시간 랭킹</h3>
            <ul className="ranking-list">
                {rankings.length === 0 ? (
                    <p className="no-data">참가자 정보 없음</p>
                ) : (
                    rankings.map((user, index) => (
                        <li
                            key={user.userId}
                            className={`ranking-item ${user.userId === currentUserId ? 'highlight' : ''}`}
                        >
                            <span className="rank">{index + 1}위</span>
                            <span className="nickname">{user.nickname}</span>
                            <span className="time">{formatTime(user.focusTime)}</span>
                            {targetTime && user.focusTime >= targetTime * 60 && (
                                <span className="badge">🎯 목표 달성</span>
                            )}
                        </li>
                    ))
                )}
            </ul>
        </div>
    );
};

export default FocusRankingBoard;
