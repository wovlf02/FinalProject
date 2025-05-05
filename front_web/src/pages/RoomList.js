import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const RoomList = () => {
    const [rooms, setRooms] = useState([]);
    const [newRoomTitle, setNewRoomTitle] = useState('');
    const [roomType, setRoomType] = useState('QUIZ');
    const [maxParticipants, setMaxParticipants] = useState(10);
    const navigate = useNavigate();

    useEffect(() => {
        axios.get('http://localhost:8080/api/rooms')
            .then((response) => setRooms(response.data))
            .catch((error) => console.error('방 목록 가져오기 실패:', error));
    }, []);

    const handleCreateRoom = () => {
        axios.post('http://localhost:8080/api/rooms/create', {
            title: newRoomTitle,
            roomType,
            maxParticipants,
        })
            .then((response) => {
                setRooms([...rooms, response.data]);
                setNewRoomTitle('');
                setRoomType('QUIZ');
                setMaxParticipants(10);
            })
            .catch((error) => console.error('방 생성 실패:', error));
    };

    return (
        <div>
            <h1>방 목록</h1>
            <div>
                <input
                    type="text"
                    placeholder="방 이름"
                    value={newRoomTitle}
                    onChange={(e) => setNewRoomTitle(e.target.value)}
                />
                <select value={roomType} onChange={(e) => setRoomType(e.target.value)}>
                    <option value="QUIZ">문제풀이방</option>
                    <option value="FOCUS">공부방</option>
                </select>
                <input
                    type="number"
                    placeholder="최대 참여자 수"
                    value={maxParticipants}
                    onChange={(e) => setMaxParticipants(e.target.value)}
                />
                <button onClick={handleCreateRoom}>방 만들기</button>
            </div>
            <ul>
                {rooms.map((room) => (
                    <li key={room.id}>
                        {room.title} ({room.roomType}) - {room.maxParticipants}명
                        <button onClick={() => navigate(`/video-room/${room.id}`)}>입장</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default RoomList;