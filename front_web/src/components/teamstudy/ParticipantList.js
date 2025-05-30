// src/components/teamstudy/ParticipantList.js
import React from 'react';
import '../../css/QuizRoom.css';

const ParticipantList = ({ participants = [], currentUserId, presenterId = null }) => {
    return (
        <div className="participant-list">
            <h3>👥 참가자 목록</h3>
            {participants.length === 0 ? (
                <p className="no-participant">참가자가 없습니다.</p>
            ) : (
                <ul>
                    {participants.map((user) => (
                        <li
                            key={user.userId}
                            className={`participant-item ${
                                user.userId === presenterId ? 'presenter' : ''
                            }`}
                        >
                            <span>{user.nickname}</span>
                            {user.userId === currentUserId && <span className="me-tag">👈 나</span>}
                            {user.userId === presenterId && <span className="badge">🎤 발표자</span>}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default ParticipantList;
