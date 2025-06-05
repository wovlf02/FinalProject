import React, { useEffect, useRef, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/api';
import '../css/QuizRoom.css';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { connectToLiveKit } from '../utils/livekit';

const QuizRoom = () => {
    const { roomId } = useParams();
    const navigate = useNavigate();
    const roomName = `quiz-${roomId}`;

    const [problem, setProblem] = useState(null);
    const [presenterId, setPresenterId] = useState(null);
    const [votePhase, setVotePhase] = useState(false);
    const [voteResult, setVoteResult] = useState(null);
    const [userId, setUserId] = useState(null);
    const [userInfo, setUserInfo] = useState({});
    const [participants, setParticipants] = useState([]);
    const [chatMessages, setChatMessages] = useState([]);
    const [chatInput, setChatInput] = useState('');

    const [showModal, setShowModal] = useState(false);
    const [selectedSubject, setSelectedSubject] = useState('');
    const [selectedSource, setSelectedSource] = useState('');
    const [selectedLevel, setSelectedLevel] = useState('');

    // 내 캠 ON/OFF 관리
    const [camOn, setCamOn] = useState(true);
    const localStreamRef = useRef(null);

    const stompRef = useRef(null);
    const chatRef = useRef(null);
    const roomRef = useRef(null);
    const localVideoRefs = useRef({});

    useEffect(() => {
        enterRoom();
        initAndFetchUser();
        connectWebSocket();
        fetchChatHistory();
        return () => {
            if (stompRef.current?.connected) stompRef.current.disconnect();
            if (roomRef.current) roomRef.current.disconnect();
            stopMyCam();
        };
        // eslint-disable-next-line
    }, []);

    const enterRoom = async () => {
        try {
            await api.post('/study/team/enter', null, { params: { roomId } });
        } catch {
            alert('입장 실패');
            navigate('/study/team');
        }
    };

    const initAndFetchUser = async () => {
        try {
            const res = await api.get('/users/me');
            const user = res.data.data;
            setUserId(user.user_id);
            setUserInfo(user);
            setParticipants([{ identity: user.user_id.toString(), nickname: user.nickname || `나 (${user.user_id})` }]);
            await startMyCam(user.user_id);
            await connectLiveKitSession(user.user_id.toString());
        } catch (err) {
            alert('카메라 권한이 필요합니다.');
        }
    };

    // 내 캠 ON: getUserMedia로 스트림 연결
    const startMyCam = async (uid = userId) => {
        try {
            const mediaStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
            localStreamRef.current = mediaStream;
            const myVideo = localVideoRefs.current[uid];
            if (myVideo) myVideo.srcObject = mediaStream;
            setCamOn(true);
        } catch (e) {
            setCamOn(false);
        }
    };

    // 내 캠 OFF: 트랙 stop, 영상 끔
    const stopMyCam = () => {
        if (localStreamRef.current) {
            localStreamRef.current.getTracks().forEach(track => track.stop());
        }
        if (localVideoRefs.current[userId]) {
            localVideoRefs.current[userId].srcObject = null;
        }
        setCamOn(false);
    };

    // 토글(ON/OFF)
    const toggleMyCam = async () => {
        if (camOn) stopMyCam();
        else await startMyCam(userId);
    };

    const connectLiveKitSession = async (identity) => {
        const res = await api.post('/livekit/token', { roomName });
        const { token, wsUrl } = res.data;
        const room = await connectToLiveKit(identity, roomName, wsUrl, token);
        roomRef.current = room;

        room.on('participantConnected', (participant) => {
            setParticipants((prev) => {
                const exists = prev.some(p => p.identity === participant.identity);
                return exists ? prev : [...prev, { identity: participant.identity, nickname: `참가자 ${participant.identity}` }];
            });

            participant.on('trackSubscribed', (track) => {
                if (track.kind === 'video') {
                    const el = localVideoRefs.current[participant.identity];
                    if (el && !el.srcObject) el.srcObject = new MediaStream([track.mediaStreamTrack]);
                }
            });
        });

        room.on('participantDisconnected', (participant) => {
            setParticipants((prev) => prev.filter(p => p.identity !== participant.identity));
            const el = localVideoRefs.current[participant.identity];
            if (el) {
                el.srcObject = null;
                el.remove();
            }
        });
    };

    const fetchProblem = async () => {
        if (!selectedSubject || !selectedSource || !selectedLevel) {
            alert('모든 조건을 선택해주세요.');
            return;
        }

        try {
            const res = await api.get('/quiz/problems/random', {
                params: {
                    subject: selectedSubject,
                    source: selectedSource,
                    level: selectedLevel
                }
            });
            setProblem(res.data);
            setShowModal(false);
        } catch (error) {
            console.error('문제 불러오기 실패:', error);
            alert('문제를 불러오지 못했습니다.');
        }
    };

    const fetchChatHistory = async () => {
        try {
            const res = await api.get(`/study/chat/quiz/${roomId}`);
            setChatMessages(res.data);
        } catch (e) {
            console.error("❌ 채팅 불러오기 실패", e);
        }
    };

    const connectWebSocket = () => {
        const sock = new SockJS('/ws');
        const client = Stomp.over(sock);
        stompRef.current = client;
        client.connect({}, () => {
            client.send('/app/quiz/enter', {}, JSON.stringify({ roomId }));

            client.subscribe(`/sub/quiz/room/${roomId}/problem`, (msg) => {
                setProblem(JSON.parse(msg.body));
                setPresenterId(null);
                setVotePhase(false);
                setVoteResult(null);
            });

            client.subscribe(`/sub/quiz/room/${roomId}/presenter`, (msg) => {
                setPresenterId(msg.body.toString());
            });

            client.subscribe(`/sub/quiz/room/${roomId}/vote-result`, (msg) => {
                setVoteResult(JSON.parse(msg.body));
                setVotePhase(false);
            });

            client.subscribe(`/sub/quiz/room/${roomId}`, (msg) => {
                const payload = JSON.parse(msg.body);
                setChatMessages((prev) => {
                    const isDuplicate = prev.some(
                        m => m.sent_at === payload.sent_at && m.sender_id === payload.sender_id && m.content === payload.content
                    );
                    return isDuplicate ? prev : [...prev, payload];
                });
                setTimeout(() => {
                    chatRef.current?.scrollTo(0, chatRef.current.scrollHeight);
                }, 100);
            });
        });
    };
    const sendMessage = (e) => {
        e.preventDefault();
        if (!chatInput.trim()) return;

        stompRef.current.send('/app/quiz/chat/send', {}, JSON.stringify({
            room_id: Number(roomId),
            content: chatInput
        }));

        setChatInput('');
    };

    const toggleMic = (id) => {
        const el = localVideoRefs.current[id];
        if (el?.srcObject) {
            const track = el.srcObject.getAudioTracks()[0];
            if (track) track.enabled = !track.enabled;
        }
    };

    const formatTime = (timestamp) => {
        const date = new Date(timestamp);
        return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    };

    return (
        <div className="quizroom-wrapper">
            <h1 className="quizroom-title">📘 문제풀이방</h1>
            <div className="quizroom-main-content">
                <section className="quizroom-problem-section">
                    <h2>문제</h2>
                    {problem ? (
                        <>
                            <div className="problem-title">{problem.title}</div>
                            {problem.subject === '국어' ? (
                                <div className="problem-passage">{problem.passage?.content}</div>
                            ) : (
                                <img src={problem.image_path} alt="문제 이미지" className="problem-image" />
                            )}
                            <ul className="problem-choices">
                                {problem.choices?.map((c, idx) => (
                                    <li key={idx}>{c}</li>
                                ))}
                            </ul>
                        </>
                    ) : (
                        <div className="problem-placeholder">문제가 시작되면 여기에 표시됩니다.</div>
                    )}

                    {presenterId && (
                        <div className="presenter-section">
                            <span>🗣️ 발표자: {presenterId === userId ? "나" : `사용자 ${presenterId}`}</span>
                            {presenterId === userId && (
                                <button onClick={() => {
                                    stompRef.current.send('/app/quiz/end-presentation', {}, JSON.stringify({ roomId }));
                                    setVotePhase(true);
                                }}>🎤 발표 종료</button>
                            )}
                        </div>
                    )}

                    {votePhase && (
                        <div className="vote-section">
                            <h3>발표는 어땠나요?</h3>
                            <button onClick={() => stompRef.current.send('/app/quiz/vote', {}, JSON.stringify({ roomId, vote: 'SUCCESS' }))}>👍 성공</button>
                            <button onClick={() => stompRef.current.send('/app/quiz/vote', {}, JSON.stringify({ roomId, vote: 'FAIL' }))}>👎 실패</button>
                        </div>
                    )}

                    {voteResult && (
                        <div className="result-section">
                            <h4>🗳️ 투표 결과</h4>
                            <div>성공: {voteResult.successCount}명</div>
                            <div>실패: {voteResult.failCount}명</div>
                            <div>결과: <strong>{voteResult.result === 'SUCCESS' ? '정답 인정!' : '정답 미인정'}</strong></div>
                            <button onClick={() => {
                                stompRef.current.send('/app/quiz/continue', {}, JSON.stringify({ roomId }));
                                setProblem(null);
                                setVoteResult(null);
                            }}>🔁 계속하기</button>
                            <button onClick={() => {
                                stompRef.current.send('/app/quiz/terminate', {}, JSON.stringify({ roomId }));
                                navigate('/study/team');
                            }}>⛔ 종료하기</button>
                        </div>
                    )}

                    {!problem && (
                        <button onClick={() => setShowModal(true)}>문제 선택</button>
                    )}
                    {problem && !presenterId && (
                        <button onClick={() => stompRef.current.send('/app/quiz/hand', {}, JSON.stringify({ roomId }))}>✋ 손들기</button>
                    )}
                </section>

                <section className="quizroom-video-section">
                    <h2>캠 화면</h2>
                    <div id="quizroom-video-grid" className="quizroom-video-grid">
                        {participants.map((p) => {
                            const myId = userId?.toString();
                            const pid = p.identity?.toString();
                            const isMe = pid === myId;
                            return (
                                <div key={pid} className="video-tile" style={{ position: "relative" }}>
                                    <video
                                        id={`video-${pid}`}
                                        ref={(elkit) => {
                                            if (elkit) localVideoRefs.current[pid] = elkit;
                                        }}
                                        autoPlay
                                        muted={isMe}
                                        playsInline
                                        style={{
                                            background: isMe && !camOn ? "#222" : "#000"
                                        }}
                                    />
                                    {/* 내 캠 OFF 오버레이 */}
                                    {isMe && !camOn &&
                                        <div style={{
                                            position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
                                            background: 'rgba(40,40,40,0.7)', color: '#fff', display: 'flex',
                                            alignItems: 'center', justifyContent: 'center', borderRadius: 12,
                                            fontSize: 18, fontWeight: 600,
                                            pointerEvents: 'none' // 버튼 클릭 방해 금지
                                        }}>
                                            카메라 OFF
                                        </div>
                                    }
                                    <div className="name">{p.nickname || pid}</div>
                                    {/* 내 캠만 토글 버튼 */}
                                    {isMe && (
                                        <div className="controls">
                                            <button onClick={toggleMyCam}>{camOn ? "📷 끄기" : "📷 켜기"}</button>
                                            <button onClick={() => toggleMic(pid)}>🎤 ON/OFF</button>
                                        </div>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                </section>

                <section className="quizroom-chat-section">
                    <h2>채팅</h2>
                    <div className="chat-log" ref={chatRef}>
                        {chatMessages.map((msg, idx) => {
                            const isMine = msg.sender_id === userId;
                            const profileImg = msg.profile_url || '../../icons/default-profile.png';
                            const time = msg.sent_at || msg.timestamp;
                            return (
                                <div key={idx} className={`chat-message ${isMine ? 'mine' : 'other'}`}>
                                    {isMine ? (
                                        <div className="chat-bubble-right">
                                            <div className="chat-time">{formatTime(time)}</div>
                                            <div className="chat-content">{msg.content}</div>
                                        </div>
                                    ) : (
                                        <div className="chat-bubble-left">
                                            <img src={profileImg} alt="profile" className="chat-profile-img" />
                                            <div className="chat-info">
                                                <div className="chat-nickname">{msg.nickname}</div>
                                                <div className="chat-content">{msg.content}</div>
                                                <div className="chat-time">{formatTime(time)}</div>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                    <form onSubmit={sendMessage} className="chat-input">
                        <input
                            type="text"
                            value={chatInput}
                            onChange={(e) => setChatInput(e.target.value)}
                            placeholder="메시지 입력"
                        />
                        <button type="submit">전송</button>
                    </form>
                </section>
            </div>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>문제 조건 선택</h3>
                        <div className="condition-row">
                            <select onChange={(e) => setSelectedSubject(e.target.value)} defaultValue="">
                                <option value="" disabled>과목 선택</option>
                                <option>국어</option>
                                <option>수학</option>
                                <option>영어</option>
                            </select>
                            <select onChange={(e) => setSelectedSource(e.target.value)} defaultValue="">
                                <option value="" disabled>출처 선택</option>
                                <option>2023년 3월 모의고사</option>
                                <option>2024년 수능</option>
                                <option>2024년 3월 모의고사</option>
                                <option>2024년 6월 평가원 모의고사</option>
                                <option>2024년 9월 평가원 모의고사</option>
                                <option>2025년 수능</option>
                            </select>
                            <select onChange={(e) => setSelectedLevel(e.target.value)} defaultValue="">
                                <option value="" disabled>난이도 선택</option>
                                <option>최하</option>
                                <option>하</option>
                                <option>중</option>
                                <option>상</option>
                                <option>최상</option>
                            </select>
                        </div>
                        <button className="fetch-button" onClick={fetchProblem}>문제 불러오기</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default QuizRoom;
