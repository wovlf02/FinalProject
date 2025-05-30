// src/components/teamstudy/HandRaiseList.js
import React from 'react';
import '../../css/QuizRoom.css';

const HandRaiseList = ({ raisedUsers = [], currentUserId, presenterId = null }) => {
    return (
        <div className="hand-raise-list">
            <h3>✋ 발표 신청자</h3>
            {raisedUsers.length === 0 ? (
                <p className="no-hand">아직 손든 사람이 없습니다.</p>
            ) : (
                <ul>
                    {raisedUsers.map((user) => (
                        <li
                            key={user.userId}
                            className={`hand-user ${user.userId === presenterId ? 'presenter' : ''}`}
                        >
                            <span>{user.nickname}</span>
                            {user.userId === presenterId && <span className="badge">🎤 발표 중</span>}
                            {user.userId === currentUserId && <span className="me-tag">👈 나</span>}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default HandRaiseList;
