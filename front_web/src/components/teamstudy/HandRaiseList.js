import React from 'react';
import '../../css/QuizRoom.css';

const HandRaiseList = ({ raisedUsers = [], currentUserId, presenterId = null }) => {
    return (
        <div className="hand-raise-list">
            <h3>✋ 발표 신청자</h3>

            {raisedUsers.length === 0 ? (
                <p className="no-hand">현재 발표 신청자가 없습니다.</p>
            ) : (
                <ul className="hand-ul">
                    {raisedUsers.map((user) => {
                        const isMe = user.userId === currentUserId;
                        const isPresenter = user.userId === presenterId;

                        return (
                            <li
                                key={user.userId}
                                className={`hand-user ${isPresenter ? 'presenter' : ''} ${isMe ? 'me' : ''}`}
                            >
                                <span className="hand-nickname">{user.nickname}</span>
                                <div className="hand-tags">
                                    {isMe && <span className="me-tag">👈 나</span>}
                                    {isPresenter && <span className="badge">🎤 발표 중</span>}
                                </div>
                            </li>
                        );
                    })}
                </ul>
            )}
        </div>
    );
};

export default HandRaiseList;
