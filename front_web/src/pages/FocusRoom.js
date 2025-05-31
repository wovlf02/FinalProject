import React, { useEffect, useRef, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/api';
import '../css/FocusRoom.css';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { connectToLiveKit } from '../utils/livekit';

const FocusRoom = () => {
    const { roomId } = useParams();
    const navigate = useNavigate();

    const [focusedSeconds, setFocusedSeconds] = useState(0);
    const [ranking, setRanking] = useState([]);
    const [winnerId, setWinnerId] = useState(null);
    const [confirmed, setConfirmed] = useState(false);
    const [userId, setUserId] = useState(null);
    const [participants, setParticipants] = useState([]);
    const [chatList, setChatList] = useState([]);
    const [chatMsg, setChatMsg] = useState('');
    const stompRef = useRef(null);
    const intervalRef = useRef(null);
    const roomName = `focus-${roomId}`;
    const localVideoRefs = useRef({});
    const roomRef = useRef(null);

    useEffect(() => {
        enterRoom();
        fetchUserInfo();
        connectWebSocket();

        return () => {
            if (intervalRef.current) clearInterval(intervalRef.current);
            if (stompRef.current) stompRef.current.disconnect();
            if (roomRef.current) roomRef.current.disconnect();
        };
    }, []);

    const enterRoom = async () => {
        try {
            await api.post('/study/team/enter', null, { params: { roomId } });
        } catch (error) {
            alert('입장 실패: 로그인 필요 또는 존재하지 않는 방');
            navigate('/study/team');
        }
    };

    const fetchUserInfo = async () => {
        try {
            const res = await api.get('/users/me');
            const identity = res.data.user_id;
            setUserId(identity);
            await connectLiveKit(identity.toString());
        } catch (err) {
            console.error('유저 정보 조회 실패:', err);
        }
    };

    const connectLiveKit = async (identity) => {
        try {
            const room = await connectToLiveKit(identity, roomName, (room) => {
                roomRef.current = room;

                room.localParticipant.videoTracks.forEach((pub) => {
                    const mediaStream = new MediaStream([pub.track.mediaStreamTrack]);
                    const el = localVideoRefs.current[identity];
                    if (el && !el.srcObject) el.srcObject = mediaStream;
                });
            });
        } catch (e) {
            console.error('LiveKit 연결 실패:', e);
            alert('LiveKit 연결 실패: 캠/마이크 권한 또는 서버 문제');
        }
    };

    const connectWebSocket = () => {
        const sock = new SockJS('/ws');
        const client = Stomp.over(sock);
        stompRef.current = client;

        client.connect({}, () => {
            client.subscribe(`/sub/focus/room/${roomId}`, (message) => {
                const body = message.body;
                if (body === 'TERMINATED') {
                    alert('방이 종료되었습니다.');
                    navigate('/study/team');
                    return;
                }
                const parsed = JSON.parse(body);
                setRanking(parsed.ranking || []);
                setParticipants(parsed.participants || []);
            });

            client.subscribe(`/sub/focus/room/${roomId}/winner`, (message) => {
                setWinnerId(Number(message.body));
            });

            client.subscribe(`/sub/focus/room/${roomId}/chat`, (message) => {
                const chat = JSON.parse(message.body);
                setChatList(prev => [...prev, chat]);
            });

            intervalRef.current = setInterval(() => {
                const payload = {
                    roomId: Number(roomId),
                    focusedSeconds: 1,
                };
                client.send('/app/focus/update-time', {}, JSON.stringify(payload));
                setFocusedSeconds(prev => prev + 1);
            }, 1000);
        });
    };

    const handleGoal = () => {
        stompRef.current.send('/app/focus/goal-achieved', {}, JSON.stringify({ room_id: Number(roomId) }));
    };

    const handleConfirmExit = () => {
        setConfirmed(true);
        stompRef.current.send('/app/focus/confirm-exit', {}, JSON.stringify({ room_id: Number(roomId) }));
    };

    const toggleMic = (userId) => {
        const el = localVideoRefs.current[userId];
        if (el?.srcObject) {
            const track = el.srcObject.getAudioTracks()[0];
            if (track) track.enabled = !track.enabled;
        }
    };

    const toggleCam = (userId) => {
        const el = localVideoRefs.current[userId];
        if (el?.srcObject) {
            const track = el.srcObject.getVideoTracks()[0];
            if (track) track.enabled = !track.enabled;
        }
    };

    const sendChat = () => {
        if (chatMsg.trim() !== '') {
            stompRef.current.send(`/app/focus/chat/${roomId}`, {}, JSON.stringify({
                senderId: userId,
                content: chatMsg
            }));
            setChatMsg('');
        }
    };

    return (
        <div className="focus-room-container">
            <h1>📚 공부 집중방</h1>

            <div className="main-content">
                <div className="video-grid">
                    {participants.map((user) => (
                        <div key={user.userId} className="video-wrapper">
                            <video
                                ref={(el) => {
                                    if (el && !localVideoRefs.current[user.userId]) {
                                        localVideoRefs.current[user.userId] = el;
                                    }
                                }}
                                autoPlay
                                muted={user.userId === userId}
                                playsInline
                            />
                            <p>{user.nickname}</p>
                            {user.userId === userId && (
                                <div className="controls">
                                    <button onClick={() => toggleCam(user.userId)}>🎥 ON/OFF</button>
                                    <button onClick={() => toggleMic(user.userId)}>🎤 ON/OFF</button>
                                </div>
                            )}
                        </div>
                    ))}
                </div>

                <div className="chat-section">
                    <div className="chat-log">
                        {chatList.map((chat, index) => (
                            <div key={index}>
                                <strong>{chat.senderId}:</strong> {chat.content}
                            </div>
                        ))}
                    </div>
                    <div className="chat-input">
                        <input
                            type="text"
                            value={chatMsg}
                            onChange={(e) => setChatMsg(e.target.value)}
                            placeholder="메시지를 입력하세요..."
                        />
                        <button onClick={sendChat}>전송</button>
                    </div>
                </div>
            </div>

            <div className="info-section">
                <h2>🕒 집중 시간: {Math.floor(focusedSeconds / 60)}분 {focusedSeconds % 60}초</h2>
                {winnerId && <p className="winner">🎉 승리자: 사용자 {winnerId}번!</p>}

                <div className="button-group">
                    <button onClick={handleGoal}>🎯 목표 달성</button>
                    <button onClick={handleConfirmExit} disabled={confirmed}>✅ 결과 확인</button>
                </div>

                <h3>📊 실시간 랭킹</h3>
                <ul className="ranking-list">
                    {ranking.length === 0 ? (
                        <p>랭킹 정보를 불러오는 중...</p>
                    ) : (
                        ranking.map((user, index) => (
                            <li key={user.userId}>
                                {index + 1}. {user.nickname} - {user.focusedSeconds}초
                            </li>
                        ))
                    )}
                </ul>
            </div>
        </div>
    );
};

export default FocusRoom;
