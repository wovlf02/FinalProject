// src/components/teamstudy/RoomCard.js
import React from 'react';
import '../../css/TeamStudy.css';

const RoomCard = ({ room, onJoin }) => {
    return (
        <li className="study-room-item">
            <div className="room-info">
                <h2 className="room-title">
                    {room.title}
                    {room.passwordRequired && <span className="lock-icon"> 🔒</span>}
                </h2>
                <p>유형: {room.roomType === 'FOCUS' ? '공부방' : '문제풀이방'}</p>
                <p>참여자 수: {room.maxParticipants ?? '-'}</p>
            </div>
            <button className="join-button" onClick={() => onJoin(room.roomId)}>참여하기</button>
        </li>
    );
};

export default RoomCard;
