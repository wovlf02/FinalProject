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
    const roomName = `focus-${roomId}`;

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
    const roomRef = useRef(null);
    const localVideoRefs = useRef({});

    useEffect(() => {
        enterRoom();
        fetchUserInfo();
        connectWebSocket();

        return () => {
            if (intervalRef.current) clearInterval(intervalRef.current);
            if (stompRef.current?.connected) {
                stompRef.current.disconnect(() => {
                    console.log("📴 STOMP 연결 해제됨");
                });
            }
            if (roomRef.current) {
                roomRef.current.disconnect();
                console.log("📴 LiveKit 연결 해제됨");
            }
        };
        // eslint-disable-next-line
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
            const identity = res.data.data.user_id.toString();
            setUserId(identity);
            setParticipants([{ identity, nickname: `나 (${identity})` }]);
            await connectLiveKitSession(identity);
        } catch (err) {
            console.error('유저 정보 조회 실패:', err);
        }
    };

    const connectLiveKitSession = async (identity) => {
        try {
            const res = await api.post('/livekit/token', { room_name: roomName });
            const { token, ws_url } = res.data;
            const room = await connectToLiveKit(identity, roomName, ws_url, token, 'video-container');
            roomRef.current = room;

            room.on('participantConnected', (participant) => {
                setParticipants((prev) => {
                    const exists = prev.some(p => p.identity === participant.identity);
                    if (!exists) {
                        return [...prev, { identity: participant.identity, nickname: `참가자 ${participant.identity}` }];
                    }
                    return prev;
                });

                participant.on('trackSubscribed', (track, publication) => {
                    if (track.kind === 'video') {
                        const id = `video-${participant.identity}`;
                        let el = document.getElementById(id);
                        if (!el) {
                            el = document.createElement('video');
                            el.id = id;
                            el.autoplay = true;
                            el.playsInline = true;
                            el.className = 'remote-video';
                            document.getElementById('video-container')?.appendChild(el);
                        }
                        if (!el.srcObject) {
                            el.srcObject = new MediaStream([track.mediaStreamTrack]);
                        }
                    }
                });
            });

            room.on('participantDisconnected', (participant) => {
                setParticipants((prev) => prev.filter(p => p.identity !== participant.identity));
                const el = document.getElementById(`video-${participant.identity}`);
                if (el) {
                    el.srcObject = null;
                    el.remove();
                }
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
                const parsed = JSON.parse(message.body);
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
                client.send('/app/focus/update-time', {}, JSON.stringify({
                    room_id: Number(roomId),
                    focusedSeconds: 1,
                }));
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

    const toggleMic = (id) => {
        const el = localVideoRefs.current[id];
        if (el?.srcObject) {
            const track = el.srcObject.getAudioTracks()[0];
            if (track) track.enabled = !track.enabled;
        }
    };

    const toggleCam = (id) => {
        const el = localVideoRefs.current[id];
        if (el?.srcObject) {
            const track = el.srcObject.getVideoTracks()[0];
            if (track) track.enabled = !track.enabled;
        }
    };

    const sendChat = (e) => {
        e.preventDefault();
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
                {/* 캠 그리드 */}
                <div id="video-container" className="video-grid">
                    {participants.map((user) => (
                        <div key={user.identity} className="video-wrapper">
                            <video
                                id={`video-${user.identity}`}
                                ref={(el) => {
                                    if (el) localVideoRefs.current[user.identity] = el;
                                }}
                                autoPlay
                                muted={user.identity === userId}
                                playsInline
                            />
                            <p>{user.nickname}</p>
                            {user.identity === userId && (
                                <div className="controls">
                                    <button onClick={() => toggleCam(user.identity)}>🎥 ON/OFF</button>
                                    <button onClick={() => toggleMic(user.identity)}>🎤 ON/OFF</button>
                                </div>
                            )}
                        </div>
                    ))}
                </div>

                {/* 오른쪽: 랭킹과 채팅 세로 분리 */}
                <div className="side-section">
                    <div className="ranking-section">
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

                    <div className="chat-section">
                        <div className="chat-log">
                            {chatList.map((chat, index) => (
                                <div key={index}>
                                    <strong>{chat.senderId}:</strong> {chat.content}
                                </div>
                            ))}
                        </div>
                        <form className="chat-input" onSubmit={sendChat}>
                            <input
                                type="text"
                                value={chatMsg}
                                onChange={(e) => setChatMsg(e.target.value)}
                                placeholder="메시지를 입력하세요..."
                            />
                            <button type="submit">전송</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default FocusRoom;
