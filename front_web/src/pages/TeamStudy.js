import React, { useState, useEffect } from 'react';
import api from "../utils/axios";
import { useNavigate } from 'react-router-dom';
import '../css/TeamStudy.css';
import { io } from "socket.io-client";

const socket = io("http://localhost:4000"); // 🔁 실제 signaling 서버 주소로 수정

const TeamStudy = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [studyRooms, setStudyRooms] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [newRoomTitle, setNewRoomTitle] = useState('');
    const [roomType, setRoomType] = useState('QUIZ');
    const [maxParticipants, setMaxParticipants] = useState(10);
    const [password, setPassword] = useState('');
    const [filteredRooms, setFilteredRooms] = useState([]);
    const [userCounts, setUserCounts] = useState({});
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token) {
            alert('로그인이 필요합니다.');
            return;
        }

        // 학습방 목록 가져오기
        api.get('/api/study/team/rooms', {
            headers: { Authorization: `Bearer ${token}` }
        }).then((res) => {
            setStudyRooms(res.data);
            setFilteredRooms(res.data);
        });

        // 초기 사용자 수 불러오기
        api.get('/api/video/room-user-counts', {
            headers: { Authorization: `Bearer ${token}` }
        }).then(res => {
            setUserCounts(res.data);
        });

        // 소켓 이벤트 수신
        socket.on('updateUserCounts', ({ roomId, count }) => {
            setUserCounts(prev => ({
                ...prev,
                [roomId]: count
            }));
        });

        // 브라우저 닫을 때 leave 처리
        const handleBeforeUnload = () => {
            const currentRoomId = sessionStorage.getItem('currentRoomId');
            const userName = localStorage.getItem('userName') || '익명';

            if (currentRoomId) {
                api.post(`/api/video/leave/${currentRoomId}`, {}, {
                    headers: { Authorization: `Bearer ${token}` }
                }).then(() => {
                    socket.emit('leave-room', { roomId: currentRoomId, userName });
                }).catch(() => {});
            }
        };

        window.addEventListener('beforeunload', handleBeforeUnload);

        return () => {
            window.removeEventListener('beforeunload', handleBeforeUnload);
            handleBeforeUnload(); // 언마운트 시에도 호출
        };
    }, []);

    const handleSearch = () => {
        const filtered = studyRooms.filter((room) =>
            room.title.toLowerCase().includes(searchTerm.toLowerCase())
        );
        setFilteredRooms(filtered);
    };

    const handleJoinRoom = (roomId) => {
        const token = localStorage.getItem('accessToken');
        const userName = localStorage.getItem('userName') || '익명';

        sessionStorage.setItem('currentRoomId', roomId);

        api.post(`/api/video/join/${roomId}`, {}, {
            headers: { Authorization: `Bearer ${token}` }
        }).then(() => {
            socket.emit('join-room', { roomId, userName }); // ✅ 수정
            navigate(`/video-room/${roomId}`);
        }).catch(err => {
            console.error('입장 처리 실패:', err);
            alert('방 입장 중 문제가 발생했습니다.');
        });
    };

    const handleCreateRoom = async () => {
        try {
            const token = localStorage.getItem('accessToken');
            if (!token) {
                alert('로그인이 필요합니다.');
                return;
            }

            const response = await api.post(
                '/api/study/team/rooms/create',
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
            setFilteredRooms(prev => [...prev, response.data]);
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
                            <p>현재 접속자: {userCounts[room.id] || 0} / {room.maxParticipants}</p>
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
