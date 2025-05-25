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
    axios
      .get('http://localhost:8080/api/rooms')
      .then((res) => setRooms(res.data))
      .catch((err) => console.error('방 목록 가져오기 실패:', err));
  }, []);

  const handleCreateRoom = () => {
    axios
      .post('http://localhost:8080/api/rooms/create', {
        title: newRoomTitle,
        roomType,
        maxParticipants,
      })
      .then((res) => {
        setRooms((prev) => [...prev, res.data]);
        setNewRoomTitle('');
        setRoomType('QUIZ');
        setMaxParticipants(10);
      })
      .catch((err) => console.error('방 생성 실패:', err));
  };

  const handleJoin = (room) => {
    // currentParticipants 필드가 백엔드에서 내려온다고 가정
    if (room.currentParticipants >= room.maxParticipants) {
      navigate('/room-full');
    } else {
      navigate(`/video-room/${room.id}`);
    }
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
          onChange={(e) => setMaxParticipants(parseInt(e.target.value, 10))}
        />
        <button onClick={handleCreateRoom}>방 만들기</button>
      </div>
      <ul>
        {rooms.map((room) => {
          const isFull = room.currentParticipants >= room.maxParticipants;
          return (
            <li key={room.id}>
              {room.title} ({room.roomType}) —{' '}
              {room.currentParticipants}/{room.maxParticipants}명
              <button onClick={() => handleJoin(room)} style={{ marginLeft: 8 }}>
                {isFull ? '꽉 찬 방' : '입장'}
              </button>
            </li>
          );
        })}
      </ul>
    </div>
  );
};

export default RoomList;
