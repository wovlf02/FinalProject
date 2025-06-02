import React, { useState } from 'react';
import '../../css/VoteModal.css';

/**
 * VoteModal - 발표자에 대한 투표 팝업
 *
 * @param {string} presenterName - 발표자 이름
 * @param {function} onVote - 투표 클릭 핸들러 (true/false)
 */
const VoteModal = ({ presenterName = '발표자', onVote }) => {
    const [voted, setVoted] = useState(false);

    const handleVote = (agree) => {
        if (voted) return;
        setVoted(true);
        onVote(agree);
    };

    return (
        <div className="vote-modal">
            <div className="vote-modal-content">
                <h2>🗳️ 발표 평가 투표</h2>
                <p>
                    <strong>{presenterName}</strong>님의 발표가 문제 풀이에 도움이 되었나요?
                </p>

                <div className="vote-buttons">
                    <button
                        className="success"
                        onClick={() => handleVote(true)}
                        disabled={voted}
                    >
                        ✅ 도움이 되었어요
                    </button>
                    <button
                        className="fail"
                        onClick={() => handleVote(false)}
                        disabled={voted}
                    >
                        ❌ 도움이 안 되었어요
                    </button>
                </div>

                {voted && <p className="vote-thankyou">투표해 주셔서 감사합니다!</p>}
            </div>
        </div>
    );
};

export default VoteModal;
