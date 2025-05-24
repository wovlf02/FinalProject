import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/api'; // ✅ 공통 axios 인스턴스
import '../css/TeamStudy.css';

const TeamStudy = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [studyRooms, setStudyRooms] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [newRoomTitle, setNewRoomTitle] = useState('');
    const [roomType, setRoomType] = useState('QUIZ');
    const [maxParticipants, setMaxParticipants] = useState(10);
    const [password, setPassword] = useState('');
    const [targetTime, setTargetTime] = useState(60); // ✅ FOCUS 전용
    const [filteredRooms, setFilteredRooms] = useState([]);
    const navigate = useNavigate();

    // ✅ 학습방 목록 불러오기 (세션 기반)
    useEffect(() => {
        const fetchRooms = async () => {
            try {
                const res = await api.post('/team-rooms/list', {}); // ✅ 빈 DTO로 요청
                setStudyRooms(res.data);
                setFilteredRooms(res.data);
            } catch (error) {
                console.error('학습방 목록 불러오기 실패:', error);
            }
        };

        fetchRooms();
    }, []);

    // ✅ 학습방 검색
    const handleSearch = () => {
        const filtered = studyRooms.filter((room) =>
            room.roomName.toLowerCase().includes(searchTerm.toLowerCase())
        );
        setFilteredRooms(filtered);
    };

    // ✅ 학습방 참여
    const handleJoinRoom = (roomId) => {
        navigate(`/video-room/${roomId}`);
    };

    // ✅ 학습방 생성
    const handleCreateRoom = async () => {
        try {
            let requestBody;

            if (roomType === 'FOCUS') {
                requestBody = {
                    room_name: newRoomTitle,
                    password,
                    targetTime,
                    mode: 'FOCUS',
                };
            } else {
                requestBody = {
                    room_name: newRoomTitle,
                    password,
                    mode: 'QUIZ',
                };
            }

            const res = await api.post(
                roomType === 'FOCUS' ? '/team-rooms/focus/create' : '/team-rooms/quiz/create',
                requestBody
            );

            alert('학습방이 생성되었습니다!');
            setStudyRooms((prev) => [...prev, res.data]);
            setFilteredRooms((prev) => [...prev, res.data]);
            setShowModal(false);
            setNewRoomTitle('');
            setRoomType('QUIZ');
            setMaxParticipants(10);
            setPassword('');
            setTargetTime(60);
        } catch (error) {
            console.error('학습방 생성 실패:', error);
            alert('학습방 생성에 실패했습니다.');
        }
    };


    return (
        <div className="team-study-container">
            <h1>팀 학습 참여하기</h1>
            <div className="search-bar">
                <input
                    type="text"
                    placeholder="학습방 검색하기"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <button className="search" onClick={handleSearch}>검색</button>
                <button onClick={() => setShowModal(true)}>+ 새 학습방 만들기</button>
            </div>
            <ul className="study-room-list">
                {filteredRooms.map((room) => (
                    <li key={room.roomId} className="study-room-item">
                        <div className="room-info">
                            <h2>{room.roomName}</h2>
                            <p>참여자 수: {room.maxParticipants}</p>
                            <p>유형: {room.type === 'FOCUS' ? '공부방' : '문제풀이방'}</p>
                        </div>
                        <button className="join-button" onClick={() => handleJoinRoom(room.roomId)}>참여하기</button>
                    </li>
                ))}
            </ul>

            {showModal && (
                <div className="modal">
                    <div className="modal-content">
                        <h2>새 학습방 만들기</h2>

                        <input
                            type="text"
                            placeholder="학습방 이름"
                            value={newRoomTitle}
                            onChange={(e) => setNewRoomTitle(e.target.value)}
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

                        <button onClick={handleCreateRoom}>생성</button>
                        <button onClick={() => setShowModal(false)}>취소</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default TeamStudy;
