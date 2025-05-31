import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/api';

const RoomList = () => {
    const [rooms, setRooms] = useState([]);
    const [newRoomTitle, setNewRoomTitle] = useState('');
    const [roomType, setRoomType] = useState('QUIZ');
    const [maxParticipants, setMaxParticipants] = useState(10);
    const navigate = useNavigate();

    useEffect(() => {
        console.log('[📡 요청] 방 목록 요청 전송');
        api.get('/rooms')
            .then((response) => {
                console.log('[📥 응답] 방 목록:', response.data);
                response.data.forEach(room =>
                    console.log(`[📌 roomId=${room.room_id}] roomType=${room.room_type}, title=${room.title}`)
                );
                setRooms(response.data);
            })
            .catch((error) => {
                console.error('🚨 방 목록 가져오기 실패:', error);
            });
    }, []);

    const handleCreateRoom = () => {
        const createRequest = {
            title: newRoomTitle,
            roomType: roomType,
            maxParticipants: parseInt(maxParticipants)
        };

        console.log('[📝 생성 요청] roomType:', roomType);
        console.log('[📤 요청 바디]', createRequest);

        api.post('/rooms/create', createRequest)
            .then((response) => {
                console.log('[✅ 생성 성공] 응답:', response.data);
                alert('방이 생성되었습니다!');
                setRooms([...rooms, response.data]);
                setNewRoomTitle('');
                setRoomType('QUIZ');
                setMaxParticipants(10);
            })
            .catch((error) => {
                console.error('🚨 방 생성 실패:', error);
                alert('방 생성에 실패했습니다.');
            });
    };

    return (
        <div>
            <h1>방 목록</h1>

            <div>
                <input
                    type="text"
                    placeholder="방 이름"
                    value={newRoomTitle}
                    onChange={(e) => {
                        console.log('[🖊️ 입력 변경] 방 제목:', e.target.value);
                        setNewRoomTitle(e.target.value);
                    }}
                />
                <select
                    value={roomType}
                    onChange={(e) => {
                        console.log('[🖊️ 선택 변경] 방 유형:', e.target.value);
                        setRoomType(e.target.value);
                    }}
                >
                    <option value="QUIZ">문제풀이방</option>
                    <option value="FOCUS">공부방</option>
                </select>
                <input
                    type="number"
                    placeholder="최대 참여자 수"
                    value={maxParticipants}
                    onChange={(e) => {
                        const val = parseInt(e.target.value);
                        console.log('[🖊️ 입력 변경] 참여자 수:', val);
                        setMaxParticipants(val);
                    }}
                />
                <button onClick={handleCreateRoom}>방 만들기</button>
            </div>

            <ul>
                {rooms.map((room) => {
                    console.log('[🧾 렌더링 중] room:', room);
                    return (
                        <li key={room.room_id}>
                            {room.title} ({room.room_type === 'FOCUS' ? '공부방' : '문제풀이방'}) - {room.max_participants}명
                            <button
                                onClick={() => {
                                    console.log(`[➡️ 이동] 방 입장: /video-room/${room.room_id}`);
                                    navigate(`/video-room/${room.room_id}`);
                                }}
                            >
                                입장
                            </button>
                        </li>
                    );
                })}
            </ul>
        </div>
    );
};

export default RoomList;
