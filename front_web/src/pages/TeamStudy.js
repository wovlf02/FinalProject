// src/pages/TeamStudy.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';
import '../css/TeamStudy.css';

const TeamStudy = () => {
  const navigate = useNavigate();
  const { teamId: routeTeamId } = useParams();
  const teamId = Number(routeTeamId) || 1;

  // 로그인된 유저
  const stored = localStorage.getItem('user');
  const [user] = useState(stored ? JSON.parse(stored) : null);

  // 방 목록
  const [studyRooms, setStudyRooms] = useState([]);
  // 검색어
  const [searchTerm, setSearchTerm] = useState('');

  // 새 방 모달 & 폼 상태
  const [showModal, setShowModal] = useState(false);
  const [newTitle, setNewTitle] = useState('');
  const [newType, setNewType] = useState('QUIZ');
  const [newPassword, setNewPassword] = useState('');
  const [newMax, setNewMax] = useState(10);
  const [newTargetTime, setNewTargetTime] = useState(60);

  // 로그인 체크
  useEffect(() => {
    if (!user) {
      alert('로그인이 필요합니다.');
      navigate('/login');
    }
  }, [user, navigate]);

  // 방 목록 가져오는 함수 (콘솔에 계속 찍히게)
  const fetchRooms = () => {
    if (!user) return;
    axios
      .get('/api/video/rooms', { params: { teamId } })
      .then(res => {
        console.log(
          `[${new Date().toLocaleTimeString()}] 팀 ${teamId} 방 목록 응답:`,
          res.data
        );
        setStudyRooms(res.data);
      })
      .catch(err => {
        console.error('방 목록 조회 에러', err);
      });
  };

  // 마운트 시와 50초마다 목록 갱신
  useEffect(() => {
    fetchRooms();
    const id = setInterval(fetchRooms, 50000);
    return () => clearInterval(id);
  }, [user, teamId]);

  // 방 생성
  const handleCreateRoom = () => {
    if (!newTitle.trim()) {
      return alert('방 제목을 입력하세요.');
    }
    axios
      .post('/api/video/rooms', {
        hostId: user.user_id,
        teamId,   // 절대 빠뜨리지 말 것!
        title: newTitle,
        type: newType,
        maxParticipants: newMax,
        password: newPassword || null,
        targetTime: newType === 'FOCUS' ? newTargetTime : null,
      })
      .then(res => {
        console.log('생성 응답:', res.data);
        fetchRooms();
        setShowModal(false);
        setNewTitle('');
        setNewType('QUIZ');
        setNewPassword('');
        setNewMax(10);
        setNewTargetTime(60);
      })
      .catch(err => {
        console.error('방 생성 실패', err);
        alert('방 생성에 실패했습니다.');
      });
  };

  // 방 입장
  const handleJoin = roomId => {
    navigate(`/video-room/${roomId}`);
  };

  if (!user) return null;

  // 검색어가 있으면 필터링, 없으면 전체
  const displayedRooms = searchTerm
    ? studyRooms.filter(r =>
        r.title.toLowerCase().includes(searchTerm.toLowerCase())
      )
    : studyRooms;

  return (
    <div className="team-study-container">
      <h1>팀 학습 참여하기</h1>

      <div className="search-bar">
        <input
          value={searchTerm}
          onChange={e => setSearchTerm(e.target.value)}
          placeholder="학습방 검색하기"
        />
        <button onClick={() => {}}>검색</button>
        <button onClick={() => setShowModal(true)}>
          + 새 학습방 만들기
        </button>
      </div>

      <ul className="study-room-list">
        {displayedRooms.map(room => (
          <li key={room.id} className="study-room-item">
            <div className="room-info">
              <h2>
                {room.title}
                {room.locked && <span className="lock">🔒</span>}
              </h2>
              <p>
                참여자: {room.currentParticipants ?? 0} /{' '}
                {room.maxParticipants ?? '무제한'} ·{' '}
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
