import React from 'react';
import {useNavigate} from 'react-router-dom';

const RoomFull = () => {
    const navigate = useNavigate();

    return (
        <div style={{textAlign: 'center', marginTop: '50px'}}>
            <h1>방이 가득 찼습니다!</h1>
            <p>다른 방에 참여하거나 새로운 방을 만들어주세요.</p>
            <button onClick={() => navigate('/team-study')} style={{padding: '10px 20px', fontSize: '16px'}}>
                팀 학습 페이지로 이동
            </button>
        </div>
    );
};

export default RoomFull;
