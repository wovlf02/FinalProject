import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // useNavigate 추가
import '../css/TeamStudy.css';

const TeamStudy = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [studyRooms] = useState([
        { id: 1, title: '수학 기출 문제 스터디', participants: '15/20', status: '진행중' },
        { id: 2, title: '과학 개념 마스터', participants: '8/10', status: '진행중' },
        { id: 3, title: '영어 단어 테스트', participants: '12/15', status: '진행중' },
    ]);
    const [filteredRooms, setFilteredRooms] = useState(studyRooms);
    const navigate = useNavigate(); // navigate 함수 선언

    const handleSearch = () => {
        const filtered = studyRooms.filter((room) =>
            room.title.toLowerCase().includes(searchTerm.toLowerCase())
        );
        setFilteredRooms(filtered);
    };

    const handleJoinRoom = (roomId) => {
        navigate(`/video-room/${roomId}`); // 화상 채팅 방으로 이동
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
                <button>+ 새 학습방 만들기</button>
            </div>
            <ul className="study-room-list">
                {filteredRooms.map((room) => (
                    <li key={room.id} className="study-room-item">
                        <div className="room-info">
                            <h2>{room.title}</h2>
                            <p>참여자: {room.participants} · {room.status}</p>
                        </div>
                        <button className="join-button" onClick={() => handleJoinRoom(room.id)}>참여하기</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default TeamStudy;
