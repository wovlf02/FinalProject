// src/pages/TeamList.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const TeamList = () => {
  const [teams, setTeams] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    axios.get('/api/teams')
      .then(res => {
        // ApiResponse 안의 data 필드
        setTeams(res.data.data);
      })
      .catch(err => {
        console.error('팀 목록 조회 실패', err);
        alert('팀 목록을 불러오지 못했습니다.');
      });
  }, []);

  return (
    <div style={{ padding: 20 }}>
      <h1>내가 속한 팀</h1>
      <ul>
        {teams.map(team => (
          <li key={team.id} style={{ margin: '8px 0' }}>
            <button
              onClick={() => navigate(`/team-study/${team.id}`)}
              style={{ fontSize: 16 }}
            >
              {team.name}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default TeamList;
