// src/components/teamstudy/VoteModal.js
import React from 'react';
import '../../css/VoteModal.css';

const VoteModal = ({ presenterName, onVote }) => {
    return (
        <div className="vote-modal">
            <div className="vote-modal-content">
                <h2>🗳️ 투표하기</h2>
                <p><strong>{presenterName}</strong>님의 발표는 문제 풀이에 도움이 되었나요?</p>

                <div className="vote-buttons">
                    <button className="success" onClick={() => onVote(true)}>✅ 도움이 되었어요</button>
                    <button className="fail" onClick={() => onVote(false)}>❌ 도움이 안 되었어요</button>
                </div>
            </div>
        </div>
    );
};

export default VoteModal;
