import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../css/TeamStudy.css';

const TeamStudy = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [studyRooms, setStudyRooms] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [newRoomTitle, setNewRoomTitle] = useState('');
    const [roomType, setRoomType] = useState('QUIZ');
    const [maxParticipants, setMaxParticipants] = useState(10);
    const [password, setPassword] = useState('');
    const [filteredRooms, setFilteredRooms] = useState([]);
    const navigate = useNavigate();

    // ✅ 페이지가 처음 로드될 때 학습방 목록을 서버에서 불러옴
    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token) {
            alert('로그인이 필요합니다.');
            return;
        }

        axios.get('http://localhost:8080/api/study/team/rooms', {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
        .then((response) => {
            setStudyRooms(response.data);
            setFilteredRooms(response.data);
        })
        .catch((error) => {
            console.error('학습방 목록 불러오기 실패:', error);
        });
    }, []);

    const handleSearch = () => {
        const filtered = studyRooms.filter((room) =>
            room.title.toLowerCase().includes(searchTerm.toLowerCase())
        );
        setFilteredRooms(filtered);
    };

    const handleJoinRoom = (roomId) => {
        navigate(`/video-room/${roomId}`);
    };

    const handleCreateRoom = async () => {
        try {
            const token = localStorage.getItem('accessToken');
            if (!token) {
                alert('로그인이 필요합니다.');
                return;
            }

            const response = await axios.post(
                'http://localhost:8080/api/study/team/rooms/create',
                {
                    title: newRoomTitle,
                    roomType,
                    maxParticipants,
                    password,
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                }
            );

            alert('학습방이 생성되었습니다!');
            setStudyRooms(prev => [...prev, response.data]);
            setFilteredRooms(prev => [...prev, response.data]); // 리스트 갱신
            setShowModal(false);
            setNewRoomTitle('');
            setRoomType('QUIZ');
            setMaxParticipants(10);
            setPassword('');
        } catch (error) {
            console.error('학습방 생성 중 오류 발생:', error);
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
                    <li key={room.id} className="study-room-item">
                        <div className="room-info">
                            <h2>{room.title}</h2>
                            <p>참여자: {room.maxParticipants}</p>
                        </div>
                        <button className="join-button" onClick={() => handleJoinRoom(room.id)}>참여하기</button>
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
