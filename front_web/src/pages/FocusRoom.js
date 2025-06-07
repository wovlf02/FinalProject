import React, {useEffect, useRef, useState} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import api from '../api/api';
import '../css/FocusRoom.css';
import {Stomp} from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {connectToLiveKit} from '../utils/livekit';
import * as faceapi from 'face-api.js';

const FocusRoom = () => {
    const {roomId} = useParams();
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
    const [modelsLoaded, setModelsLoaded] = useState(false);
    const [faceDetected, setFaceDetected] = useState(false);
    const [camOn, setCamOn] = useState(true);
    const [chatMessages, setChatMessages] = useState([]);
    const [chatInput, setChatInput] = useState('');

    const myVideoRef = useRef(null);
    const myStreamRef = useRef(null);
    const chatRef = useRef(null);
    const stompRef = useRef(null);
    const intervalRef = useRef(null);
    const faceIntervalRef = useRef(null);
    const roomRef = useRef(null);

    const medalColors = ['#FFD700', '#C0C0C0', '#CD7F32'];

    const formatTime = (seconds) => {
        const h = Math.floor(seconds / 3600);
        const m = Math.floor((seconds % 3600) / 60);
        const s = seconds % 60;
        if (seconds < 60) return `${s}초`;
        else if (seconds < 3600) return `${m}분 ${s}초`;
        else return `${h}시간 ${m}분 ${s}초`;
    };

    const enterRoom = async () => {
        try {
            await api.post('/study/team/enter', null, {params: {roomId}});
        } catch {
            alert('입장 실패');
            navigate('/study/team');
        }
    };

    const fetchUserInfo = async () => {
        const res = await api.get('/users/me');
        const identity = res.data.data.user_id;
        setUserId(identity);
        setParticipants([{identity, nickname: `나 (${identity})`}]);
        await connectLiveKitSession(identity);
        await startMyCam();
    };

    const startMyCam = async () => {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({video: true});
            if (myVideoRef.current) myVideoRef.current.srcObject = stream;
            myStreamRef.current = stream;
            setCamOn(true);
        } catch {
            setCamOn(false);
        }
    };

    const stopMyCam = () => {
        myStreamRef.current?.getTracks().forEach((t) => t.stop());
        if (myVideoRef.current) myVideoRef.current.srcObject = null;
        setCamOn(false);
        setFaceDetected(false);
    };

    const toggleMyCam = async () => {
        if (camOn) stopMyCam();
        else await startMyCam();
    };

    const connectLiveKitSession = async (identity) => {
        const res = await api.post('/livekit/token', {room_name: roomName});
        const {token, ws_url} = res.data;
        const room = await connectToLiveKit(identity, roomName, ws_url, token, 'video-container');
        roomRef.current = room;

        room.on('participantConnected', (participant) => {
            setParticipants((prev) => {
                const exists = prev.some((p) => p.identity === participant.identity);
                return exists ? prev : [...prev, {
                    identity: participant.identity,
                    nickname: `참가자 ${participant.identity}`
                }];
            });

            participant.on('trackSubscribed', (track) => {
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
                    if (!el.srcObject) el.srcObject = new MediaStream([track.mediaStreamTrack]);
                }
            });
        });

        room.on('participantDisconnected', (participant) => {
            setParticipants((prev) => prev.filter((p) => p.identity !== participant.identity));
            const el = document.getElementById(`video-${participant.identity}`);
            if (el) {
                el.srcObject = null;
                el.remove();
            }
        });
    };

    const fetchChatHistory = async () => {
        try {
            const res = await api.get(`/api/focus/chat/${roomId}`);
            setChatList(res.data || []);
        } catch {
        }
    };

    // ✅ 채팅 WebSocket 수신 처리
    const connectWebSocket = () => {
        const sock = new SockJS('/ws');
        const client = Stomp.over(sock);
        stompRef.current = client;

        client.connect({}, () => {
            client.subscribe(`/sub/focus/room/${roomId}`, (msg) => {
                const data = JSON.parse(msg.body);
                console.log("채팅 데이터: ", data);

                // 🔹 채팅 메시지 처리
                if (data.sender_id && data.content) {
                    setChatMessages((prev) => {
                        const isDup = prev.some(
                            m =>
                                m.sentAt === data.sent_at &&
                                m.senderId === data.sender_id &&
                                m.content === data.content
                        );
                        return isDup ? prev : [...prev, data];
                    });

                    setTimeout(() => {
                        chatRef.current?.scrollTo({
                            top: chatRef.current.scrollHeight,
                            behavior: 'smooth',
                        });
                    }, 100);
                    return;
                }

                // 🔹 기타 메시지 (랭킹 등)
                if (data.ranking) setRanking(data.ranking);
                if (data.participants) setParticipants(data.participants);
            });

            client.subscribe(`/sub/focus/room/${roomId}/winner`, (msg) => {
                setWinnerId(Number(msg.body));
            });
        });
    };

    const loadModels = async () => {
        try {
            await faceapi.nets.ssdMobilenetv1.loadFromUri('/models');
            await faceapi.nets.faceLandmark68Net.loadFromUri('/models');
            await faceapi.nets.faceRecognitionNet.loadFromUri('/models');
            setModelsLoaded(true);
        } catch (err) {
            alert('모델 로딩 실패: ' + err);
        }
    };

    const sendMessage = (e) => {
        e.preventDefault();
        if (!chatInput.trim()) return;

        stompRef.current.send('/app/focus/chat/send', {}, JSON.stringify({
            room_id: Number(roomId),
            content: chatInput
        }));
        setChatInput('');
    };

    const isMe = (chat) => String(chat.sender_id) === String(userId);

    const showRanking = ranking.length > 0 ? ranking : [{
        user_id: userId,
        nickname: '나',
        focusedSeconds: focusedSeconds || 0
    }];

    useEffect(() => {
        enterRoom();
        fetchUserInfo();
        connectWebSocket();
        fetchChatHistory();
        loadModels();

        return () => {
            clearInterval(intervalRef.current);
            clearInterval(faceIntervalRef.current);
            stompRef.current?.disconnect();
            roomRef.current?.disconnect();
            stopMyCam();
        };
    }, []);

    useEffect(() => {
        if (!modelsLoaded) return;
        const detect = async () => {
            if (!myVideoRef.current || !camOn) return setFaceDetected(false);
            const detections = await faceapi.detectAllFaces(myVideoRef.current);
            setFaceDetected(detections.length > 0);
        };
        if (camOn) {
            faceIntervalRef.current = setInterval(detect, 700);
        }
        return () => clearInterval(faceIntervalRef.current);
    }, [modelsLoaded, camOn]);

    useEffect(() => {
        if (!stompRef.current) return;
        clearInterval(intervalRef.current);
        if (faceDetected) {
            intervalRef.current = setInterval(() => {
                stompRef.current.send('/app/focus/update-time', {}, JSON.stringify({
                    room_id: Number(roomId),
                    focusedSeconds: 1
                }));
                setFocusedSeconds((prev) => prev + 1);
            }, 1000);
        }
        return () => clearInterval(intervalRef.current);
    }, [faceDetected]);

    useEffect(() => {
        chatRef.current?.scrollTo({
            top: chatRef.current.scrollHeight,
            behavior: 'smooth',
        });
    }, [chatList]);

    return (
        <div className="focus-room-container">
            <h1>📚 공부 집중방</h1>
            <div className="main-content">
                <div id="video-container" className="video-grid">
                    <div className="video-wrapper"
                         style={{border: faceDetected ? '2px solid #38bdf8' : '2px solid #f87171'}}>
                        <video ref={myVideoRef} autoPlay muted playsInline style={{
                            width: 240, height: 180, borderRadius: 12, background: camOn ? "#000" : "#222"
                        }}/>
                        <p>내 캠 (얼굴 {faceDetected ? '인식됨' : '미인식'})</p>
                        <div className="controls">
                            <button onClick={toggleMyCam}>{camOn ? "🎥 끄기" : "🎥 켜기"}</button>
                        </div>
                    </div>
                    {participants.filter(u => u.identity !== userId).map(user => (
                        <div key={user.identity} className="video-wrapper">
                            <video id={`video-${user.identity}`} autoPlay playsInline/>
                            <p>{user.nickname}</p>
                        </div>
                    ))}
                </div>

                <div className="side-section">
                    <div className="ranking-section">
                        <h3>📊 공부시간 랭킹 <span
                            style={{fontSize: 13, color: '#888'}}>{new Date().toISOString().split('T')[0]} 기준</span>
                        </h3>
                        <div className="ranking-list">
                            {showRanking
                                .sort((a, b) => (b.focusedSeconds || 0) - (a.focusedSeconds || 0))
                                .slice(0, 5)
                                .map((user, index) => {
                                    const seconds = user.focusedSeconds || 0;
                                    const percent = Math.min((seconds / (showRanking[0]?.focusedSeconds || 1)) * 100, 100);
                                    const me = String(user.userId ?? user.identity) === String(userId);
                                    return (
                                        <div key={user.user_id ?? user.identity} style={{marginBottom: 12}}>
                                            <div style={{display: 'flex', alignItems: 'center', marginBottom: 4}}>
                                                <div style={{
                                                    fontWeight: 700, fontSize: 15,
                                                    color: medalColors[index] || '#555', marginRight: 8,
                                                    width: 20, textAlign: 'center'
                                                }}>{index + 1}</div>
                                                <div style={{flex: 1, fontWeight: me ? 700 : 500}}>
                                                    {user.nickname || user.identity}
                                                </div>
                                                <div style={{fontSize: 13, color: '#666'}}>{formatTime(seconds)}</div>
                                            </div>
                                            <div style={{
                                                height: 8,
                                                background: '#e5e7eb',
                                                borderRadius: 4,
                                                overflow: 'hidden'
                                            }}>
                                                <div style={{
                                                    width: `${percent}%`,
                                                    height: '100%',
                                                    background: medalColors[index] || '#93c5fd'
                                                }}/>
                                            </div>
                                        </div>
                                    );
                                })}
                        </div>
                    </div>

                    <div className="chat-section">
                        <div className="chat-log" ref={chatRef}>
                            {chatMessages.map((chat, index) => {
                                const isMine = isMe(chat);
                                const timestamp = new Date(chat.sent_at || chat.timestamp).toLocaleTimeString('ko-KR', {
                                    hour: '2-digit',
                                    minute: '2-digit'
                                });

                                return (
                                    <div key={index} className={`chat-message ${isMine ? 'mine' : 'other'}`}>
                                        {isMine ? (
                                            <div className="chat-bubble-right">
                                                <div className="chat-time">{timestamp}</div>
                                                <div className="chat-content">{chat.content}</div>
                                            </div>
                                        ) : (
                                            <div className="chat-bubble-left">
                                                <img
                                                    src={chat.profile_url || '/icons/default-profile.png'}
                                                    alt="profile"
                                                    className="chat-profile-img"
                                                />
                                                <div className="chat-info">
                                                    <div className="chat-nickname">{chat.nickname}</div>
                                                    <div className="chat-content">{chat.content}</div>
                                                    <div className="chat-time">{timestamp}</div>
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                        <form onSubmit={sendMessage} className="chat-input"
                              style={{display: 'flex', gap: 4, marginTop: 8}}>
                            <input
                                type="text"
                                value={chatInput}
                                onChange={(e) => setChatInput(e.target.value)}
                                placeholder="메시지를 입력하세요..."
                                style={{flex: 1, padding: 8, borderRadius: 6, border: '1px solid #ccc'}}
                            />
                            <button type="submit" style={{
                                padding: '8px 16px', borderRadius: 6,
                                border: 'none', background: '#38bdf8', color: '#fff'
                            }}>전송
                            </button>
                        </form>
                        {!faceDetected && modelsLoaded && camOn && (
                            <div style={{color: '#f87171', marginTop: 12, fontWeight: 500}}>
                                얼굴이 인식되지 않으면 집중시간이 증가하지 않습니다!
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default FocusRoom;
