// src/pages/RoomCreatePage.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/api';
import '../css/TeamStudy.css'; // 기존 스타일 재활용

const RoomCreatePage = () => {
    const navigate = useNavigate();

    const [title, setTitle] = useState('');
    const [roomType, setRoomType] = useState('QUIZ');
    const [maxParticipants, setMaxParticipants] = useState(10);
    const [targetTime, setTargetTime] = useState(60);
    const [password, setPassword] = useState('');

    const handleCreateRoom = async () => {
        if (!title.trim()) {
            alert('방 제목을 입력해주세요.');
            return;
        }

        try {
            const res = await api.post('/video-room/create', {
                title,
                roomType,
                maxParticipants,
                targetTime: roomType === 'FOCUS' ? targetTime : null,
                password: password || null,
            });

            const newRoomId = res.data.roomId || res.data.data;
            alert('학습방이 생성되었습니다!');
            navigate(`/video-room/${newRoomId}`);
        } catch (err) {
            console.error('방 생성 실패:', err);
            alert('학습방 생성에 실패했습니다.');
        }
    };

    return (
        <div className="team-study-container">
            <h2>새 학습방 만들기</h2>

            <div className="modal-content">
                <input
                    type="text"
                    placeholder="학습방 이름"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />

                <select value={roomType} onChange={(e) => setRoomType(e.target.value)}>
                    <option value="QUIZ">문제풀이방</option>
                    <option value="FOCUS">공부방</option>
                </select>

                {roomType === 'FOCUS' && (
                    <>
                        <label>목표 시간 (분)</label>
                        <input
                            type="number"
                            value={targetTime}
                            onChange={(e) => setTargetTime(parseInt(e.target.value))}
                            placeholder="예: 60"
                        />
                    </>
                )}

                <label>최대 참여자 수</label>
                <input
                    type="number"
                    value={maxParticipants}
                    onChange={(e) => setMaxParticipants(parseInt(e.target.value))}
                />

                <label>비밀번호 (선택)</label>
                <input
                    type="text"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <button onClick={handleCreateRoom}>방 생성하기</button>
                <button onClick={() => navigate('/team-study')}>취소</button>
            </div>
        </div>
    );
};

export default RoomCreatePage;
