import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/api';
import '../css/TeamStudy.css';

const TeamStudy = () => {
    const [tab, setTab] = useState('ALL');
    const [searchTerm, setSearchTerm] = useState('');
    const [studyRooms, setStudyRooms] = useState([]);
    const [filteredRooms, setFilteredRooms] = useState([]);
    const [showModal, setShowModal] = useState(false);

    const [newRoomTitle, setNewRoomTitle] = useState('');
    const [roomType, setRoomType] = useState('QUIZ');
    const [maxParticipants, setMaxParticipants] = useState(10);
    const [password, setPassword] = useState('');
    const [targetTime, setTargetTime] = useState(60);

    const navigate = useNavigate();

    useEffect(() => {
        fetchRooms();
    }, [tab]);

    const fetchRooms = async () => {
        try {
            const endpoint = tab === 'ALL' ? '/video-room/all' : '/video-room/my';
            const res = await api.get(endpoint);
            const roomList = res.data || [];
            setStudyRooms(roomList);
            setFilteredRooms(roomList);
        } catch (error) {
            console.error('팀방 목록 불러오기 실패:', error);
        }
    };

    const handleSearch = () => {
        const filtered = studyRooms.filter((room) =>
            room.title.toLowerCase().includes(searchTerm.toLowerCase())
        );
        setFilteredRooms(filtered);
    };

    const handleJoinRoom = async (roomId) => {
        const room = studyRooms.find(r => r.roomId === roomId);
        if (!room) return;

        if (room.passwordRequired) {
            const inputPassword = prompt('비밀번호를 입력하세요:');
            if (!inputPassword || inputPassword !== room.password) {
                alert('비밀번호가 일치하지 않습니다.');
                return;
            }
        }

        try {
            await api.post('/video-room/join', { roomId });

            const route = room.roomType === 'FOCUS'
                ? `/study/focus/${roomId}`
                : `/study/quiz/${roomId}`;

            navigate(route);
        } catch (err) {
            console.error('팀방 입장 실패:', err);
            alert('팀방 입장에 실패했습니다.');
        }
    };

    const handleCreateRoom = async () => {
        if (!newRoomTitle.trim()) {
            alert('방 제목을 입력해주세요.');
            return;
        }

        try {
            const createRequest = {
                title: newRoomTitle,
                roomType,
                maxParticipants,
                targetTime: roomType === 'FOCUS' ? targetTime : null,
                password: password || null
            };

            const res = await api.post('/video-room/create', createRequest);
            const newRoomId = res.data.roomId || res.data.data;

            alert('학습방이 생성되었습니다!');
            setShowModal(false);
            resetForm();
            fetchRooms();
        } catch (error) {
            console.error('팀방 생성 실패:', error);
            alert('학습방 생성에 실패했습니다.');
        }
    };

    const resetForm = () => {
        setNewRoomTitle('');
        setRoomType('QUIZ');
        setPassword('');
        setMaxParticipants(10);
        setTargetTime(60);
    };

    return (
        <div className="team-study-container">
            <h1>팀 학습 참여하기</h1>

            <div className="tab-buttons">
                <button className={tab === 'ALL' ? 'active' : ''} onClick={() => setTab('ALL')}>전체 팀방</button>
                <button className={tab === 'MY' ? 'active' : ''} onClick={() => setTab('MY')}>참여 중인 팀</button>
            </div>

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
                {filteredRooms.length === 0 ? (
                    <li className="empty-state">
                        <p className="empty-icon">📭</p>
                        <p className="empty-message">조건에 맞는 학습방이 없습니다.</p>
                    </li>
                ) : (
                    filteredRooms.map((room) => (
                        <li key={room.roomId} className="study-room-item">
                            <div className="room-info">
                                <h2>{room.title}</h2>
                                <p>참여자 수: {room.maxParticipants ?? '-'}</p>
                                <p>유형: {room.roomType === 'FOCUS' ? '공부방' : '문제풀이방'}</p>
                                {room.passwordRequired && <p>🔒 비밀번호 설정됨</p>}
                            </div>
                            <button className="join-button" onClick={() => handleJoinRoom(room.roomId)}>참여하기</button>
                        </li>
                    ))
                )}
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
