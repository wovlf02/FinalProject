import React from 'react';
import '../../css/QuizRoom.css';

const ParticipantList = ({ participants = [], currentUserId, presenterId = null }) => {
    return (
        <div className="participant-list">
            <h3>👥 참가자 목록</h3>

            {participants.length === 0 ? (
                <p className="no-participant">아직 입장한 참가자가 없습니다.</p>
            ) : (
                <ul className="participant-ul">
                    {participants.map((user) => {
                        const isMe = user.userId === currentUserId;
                        const isPresenter = user.userId === presenterId;

                        return (
                            <li
                                key={user.userId}
                                className={`participant-item ${
                                    isPresenter ? 'presenter' : ''
                                } ${isMe ? 'me' : ''}`}
                            >
                                <span className="participant-nickname">{user.nickname}</span>
                                <div className="participant-tags">
                                    {isMe && <span className="me-tag">👈 나</span>}
                                    {isPresenter && <span className="badge">🎤 발표자</span>}
                                </div>
                            </li>
                        );
                    })}
                </ul>
            )}
        </div>
    );
};

export default ParticipantList;
