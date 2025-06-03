import React from 'react';
import '../../css/TeamStudy.css';

/**
 * RoomCard - 팀 학습방 카드 컴포넌트
 *
 * @param {object} room - 방 정보 객체
 * @param {function} onJoin - 참여 버튼 클릭 시 호출 (room 객체 전달)
 */
const RoomCard = ({ room, onJoin }) => {
    const {
        title,
        roomId,
        roomType,
        passwordRequired,
        maxParticipants,
        currentParticipants,
    } = room;

    return (
        <li className="study-room-item">
            <div className="room-info">
                <h2 className="room-title">
                    {title}
                    {passwordRequired && <span className="lock-icon"> 🔒</span>}
                </h2>
                <p>유형: {roomType === 'FOCUS' ? '공부방' : '문제풀이방'}</p>
                <p>
                    참여자 수: {currentParticipants ?? '?'} / {maxParticipants ?? '-'}
                </p>
            </div>
            <button className="join-button" onClick={() => onJoin(room)}>
                참여하기
            </button>
        </li>
    );
};

export default RoomCard;
