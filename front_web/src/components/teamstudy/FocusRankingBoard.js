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

            {rankings.length === 0 ? (
                <p className="no-data">참가자 정보가 없습니다.</p>
            ) : (
                <ul className="ranking-list">
                    {rankings.map((user, index) => {
                        const isMe = user.userId === currentUserId;
                        const achievedGoal = targetTime && user.focusTime >= targetTime * 60;

                        return (
                            <li
                                key={user.userId}
                                className={`ranking-item ${isMe ? 'highlight' : ''}`}
                            >
                                <span className="rank">{index + 1}위</span>
                                <span className="nickname">
                                    {user.nickname}
                                    {isMe && <span className="me-tag"> 👈 나</span>}
                                </span>
                                <span className="time">{formatTime(user.focusTime)}</span>
                                {achievedGoal && <span className="badge">🎯 목표 달성</span>}
                            </li>
                        );
                    })}
                </ul>
            )}
        </div>
    );
};

export default FocusRankingBoard;
