import React from 'react';
import '../../../css/FocusVideoGrid.css';

const FocusVideoGrid = ({ participants, localVideoRefs, userId, onToggleMic, onToggleCam }) => {
    return (
        <div className="video-grid">
            {participants.map((p) => (
                <div key={p.userId} className="video-box">
                    <div className="video-header">
                        <span className="nickname">{p.nickname}</span>
                        <span className="time">{p.focusedTime || '00:00:00'}</span>
                    </div>
                    <div className="video-area">
                        <video
                            id={`video-${p.userId}`}
                            ref={el => {
                                if (el && !localVideoRefs.current[p.userId]) {
                                    localVideoRefs.current[p.userId] = el;
                                }
                            }}
                            autoPlay
                            muted={p.userId === userId}
                            className="video-element"
                        />
                        <p>유저 캠 띄우기</p>
                    </div>
                    {p.userId === userId && (
                        <div className="video-controls">
                            <button onClick={() => onToggleCam(p.userId)}>캠 ON/OFF</button>
                            <button onClick={() => onToggleMic(p.userId)}>마이크 ON/OFF</button>
                        </div>
                    )}
                </div>
            ))}
        </div>
    );
};

export default FocusVideoGrid;
