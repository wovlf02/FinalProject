// src/pages/TeamStudy.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';
import '../css/TeamStudy.css';

const TeamStudy = () => {
  const navigate = useNavigate();
  const { teamId: routeTeamId } = useParams();

  // 1) 로그인된 유저 정보 가져오기
  const stored = localStorage.getItem('user');
  const initialUser = stored ? JSON.parse(stored) : null;
  const [user, setUser] = useState(initialUser);

  // 2) 방 목록 상태
  const [studyRooms, setStudyRooms] = useState([]);
  const [filteredRooms, setFilteredRooms] = useState([]);

  // 3) 검색어
  const [searchTerm, setSearchTerm] = useState('');

  // 4) 모달 & 새 방 폼 상태
  const [showModal, setShowModal] = useState(false);
  const [newTitle, setNewTitle] = useState('');
  const [newType, setNewType] = useState('QUIZ');
  const [newPassword, setNewPassword] = useState('');
  const [newMax, setNewMax] = useState(10);
  const [newTargetTime, setNewTargetTime] = useState(60);

  // 5) 로그인 체크
  useEffect(() => {
    if (!user) {
      alert('로그인이 필요합니다.');
      navigate('/login');
    }
  }, [user, navigate]);

  // 6) hostId / teamId
  const hostId = user?.user_id;
  const teamId = Number(routeTeamId) || 1;

  // 7) 방 목록 조회
  useEffect(() => {
    if (!user) return;
    axios
      .get('/api/video/rooms', { params: { teamId } })
      .then(res => {
        const rooms = res.data;            // res.data.data → res.data 로
        setStudyRooms(rooms);
        setFilteredRooms(rooms);
      })
      .catch(err => {
        console.error('❌ 방 목록 조회 에러:', err);
        alert('방 목록을 불러오지 못했습니다.');
      });
  }, [user, teamId]);

  // 8) 검색 핸들러
  const handleSearch = () => {
    setFilteredRooms(
      studyRooms.filter(room =>
        room.title.toLowerCase().includes(searchTerm.toLowerCase())
      )
    );
  };

  // 9) 방 생성 핸들러
  const handleCreateRoom = () => {
    if (!newTitle.trim()) {
      alert('방 제목을 입력하세요.');
      return;
    }
    axios
      .post('/api/video/rooms', {
        hostId,
        teamId,
        title: newTitle,
        type: newType,
        maxParticipants: newMax,
        password: newPassword || null,
        targetTime: newType === 'FOCUS' ? newTargetTime : null,
      })
      .then(res => {
        const created = res.data;          // res.data.data → res.data
        const updated = [...studyRooms, created];
        setStudyRooms(updated);
        setFilteredRooms(updated);
        setShowModal(false);
        // 폼 초기화
        setNewTitle('');
        setNewType('QUIZ');
        setNewPassword('');
        setNewMax(10);
        setNewTargetTime(60);
      })
      .catch(err => {
        console.error('❌ 방 생성 실패:', err);
        alert('방 생성에 실패했습니다.');
      });
  };

  // 10) 방 참여 핸들러
  const handleJoin = roomId => {
    navigate(`/video-room/${roomId}`);
  };

  if (!user) return null;

  return (
    <div className="team-study-container">
      <h1>팀 학습 참여하기</h1>

      <div className="search-bar">
        <input
          type="text"
          placeholder="학습방 검색하기"
          value={searchTerm}
          onChange={e => setSearchTerm(e.target.value)}
        />
        <button onClick={handleSearch}>검색</button>
        <button onClick={() => setShowModal(true)}>+ 새 학습방 만들기</button>
      </div>

      <ul className="study-room-list">
        {filteredRooms.map(room => (
          <li key={room.id} className="study-room-item">
            <div className="room-info">
              <h2>
                {room.title}
                {room.locked && <span className="lock">🔒</span>}
              </h2>
              <p>
                참여자: {room.currentParticipants} / {room.maxParticipants} ·{' '}
                {room.type === 'QUIZ' ? '문제풀이방' : '공부방'}
              </p>
              {room.type === 'FOCUS' && (
                <p>목표 시간: {room.targetTime}분</p>
              )}
              <p>상태: {room.status}</p>
            </div>
            <button
              className="join-button"
              onClick={() => handleJoin(room.id)}
            >
              참여하기
            </button>
          </li>
        ))}
      </ul>

      {showModal && (
        <div className="modal">
          <div className="modal-content">
            <h2>새 학습방 만들기</h2>

            <label>방 제목</label>
            <input
              type="text"
              value={newTitle}
              onChange={e => setNewTitle(e.target.value)}
            />

            <label>유형</label>
            <select
              value={newType}
              onChange={e => setNewType(e.target.value)}
            >
              <option value="QUIZ">문제풀이방</option>
              <option value="FOCUS">공부방</option>
            </select>

            {newType === 'FOCUS' && (
              <>
                <label>목표 시간 (분)</label>
                <input
                  type="number"
                  min="1"
                  value={newTargetTime}
                  onChange={e => setNewTargetTime(+e.target.value)}
                />
              </>
            )}

            <label>최대 참여자 수</label>
            <input
              type="number"
              min="1"
              value={newMax}
              onChange={e => setNewMax(+e.target.value)}
            />

            <label>비밀번호 (선택)</label>
            <input
              type="text"
              value={newPassword}
              onChange={e => setNewPassword(e.target.value)}
              placeholder="4자리 숫자 등"
            />

            <div className="modal-buttons">
              <button onClick={handleCreateRoom}>생성</button>
              <button onClick={() => setShowModal(false)}>취소</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TeamStudy;
