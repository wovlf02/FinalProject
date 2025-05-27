import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/api';
// import '../css/RoomList.css';

const RoomList = () => {
  const navigate = useNavigate();
  const [rooms, setRooms] = useState([]);
  const [newTitle, setNewTitle] = useState('');
  const [maxParticipants, setMaxParticipants] = useState(10);

  useEffect(() => {
    api.get('/api/video/rooms')
      .then(res => setRooms(res.data))
      .catch(err => console.error('방 목록 오류:', err));
  }, []);

  const createRoom = () => {
    const stored = localStorage.getItem('user');
    if (!stored) return alert('로그인이 필요합니다.');
    const user = JSON.parse(stored);

    api.post('/api/video/create', {
      hostId: user.user_id,
      title: newTitle,
      maxParticipants
    })
      .then(res => {
        setRooms(prev => [...prev, res.data]);
        setNewTitle('');
        setMaxParticipants(10);
      })
      .catch(err => console.error('생성 실패:', err));
  };

  const joinRoom = roomId => {
    const stored = localStorage.getItem('user');
    if (!stored) return alert('로그인이 필요합니다.');
    const user = JSON.parse(stored);

    api.post('/api/video/join', { roomId, userId: user.user_id })
      .then(() => navigate(`/video-room/${roomId}`))
      .catch(err => console.error('참여 실패:', err));
  };

  return (
    <div className="room-list-container">
      <h1>방 목록</h1>
      <div className="create-form">
        <input
          value={newTitle}
          onChange={e => setNewTitle(e.target.value)}
          placeholder="방 제목"
        />
        <input
          type="number"
          value={maxParticipants}
          onChange={e => setMaxParticipants(+e.target.value)}
          placeholder="최대 참여자"
        />
        <button onClick={createRoom}>방 만들기</button>
      </div>
      <ul className="room-list">
        {rooms.map(room => (
          <li key={room.roomId} className="room-item">
            <span>{room.title}</span>
            <span>최대 {room.maxParticipants || '무제한'}</span>
            <button onClick={() => joinRoom(room.roomId)}>입장</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default RoomList;
