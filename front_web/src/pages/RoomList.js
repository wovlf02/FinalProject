import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/api';

const RoomList = () => {
  const navigate = useNavigate();
  const { teamId: rawTeamId } = useParams();
  const teamId = rawTeamId ? Number(rawTeamId) : 1;

  const [rooms, setRooms] = useState([]);
  const [newTitle, setNewTitle] = useState('');
  const [maxParticipants, setMaxParticipants] = useState(10);

  // 방 목록 및 참여자 수 조회
  const fetchRooms = async () => {
    try {
      const res = await api.get('/api/video/rooms', { params: { teamId } });
      console.log('RoomList 조회 응답:', res.data);
      const list = res.data?.data ?? res.data;
      // 각 방의 참여자 수 가져오기
      const withCount = await Promise.all(
        list.map(async room => {
          try {
            const countRes = await api.get(`/api/video/${room.id}/count`);
            const current = countRes.data?.data ?? countRes.data;
            return { ...room, currentParticipants: current };
          } catch (e) {
            console.error(`방 ${room.id} 참여자 수 조회 실패:`, e);
            return { ...room, currentParticipants: 0 };
          }
        })
      );
      setRooms(withCount);
    } catch (err) {
      console.error('방 목록 조회 오류:', err);
      alert('방 목록을 불러오지 못했습니다.');
    }
  };

  useEffect(() => { fetchRooms(); }, [teamId]);

  // 방 생성
  const createRoom = async () => {
    const stored = localStorage.getItem('user');
    if (!stored) {
      alert('로그인이 필요합니다.');
      return;
    }
    const user = JSON.parse(stored);

    if (!newTitle.trim()) {
      alert('방 제목을 입력하세요.');
      return;
    }

    try {
      const res = await api.post('/api/video/rooms', {
        hostId: user.user_id,
        teamId,
        title: newTitle,
        type: 'QUIZ',
        maxParticipants,
        password: null,
        targetTime: null
      });
      console.log('RoomList 생성 응답:', res.data);
      setNewTitle('');
      setMaxParticipants(10);
      fetchRooms();
    } catch (err) {
      console.error('방 생성 실패:', err);
      alert('방 생성에 실패했습니다.');
    }
  };

  // 방 입장 및 목록 갱신
  const joinRoom = async roomId => {
    const stored = localStorage.getItem('user');
    if (!stored) {
      alert('로그인이 필요합니다.');
      return;
    }
    const user = JSON.parse(stored);

    try {
      await api.post('/api/video/join', { roomId, userId: user.user_id });
      // 참여 후 바로 인원수 갱신
      await fetchRooms();
      navigate(`/video-room/${roomId}`);
    } catch (err) {
      console.error('방 참여 실패:', err);
      alert('방 참여에 실패했습니다.');
    }
  };

  return (
    <div className="room-list-container">
      <h1>팀 {teamId} 학습방 목록</h1>

      <div className="create-form">
        <input
          value={newTitle}
          onChange={e => setNewTitle(e.target.value)}
          placeholder="방 제목"
        />
        <input
          type="number"
          min={1}
          value={maxParticipants}
          onChange={e => setMaxParticipants(+e.target.value)}
          placeholder="최대 참여자"
        />
        <button onClick={createRoom}>방 만들기</button>
      </div>

      <ul className="room-list">
        {rooms.map(room => (
          <li key={room.id} className="room-item">
            <span>{room.title}</span>
            <span>참여자 {room.currentParticipants} / 최대 {room.maxParticipants}</span>
            <button onClick={() => joinRoom(room.id)}>입장</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default RoomList;
