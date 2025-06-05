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
    const [participants, setParticipants] = useState([]);
    const [chatMessages, setChatMessages] = useState([]);
    const [chatInput, setChatInput] = useState('');

    const [showModal, setShowModal] = useState(false);
    const [selectedSubject, setSelectedSubject] = useState('');
    const [selectedSource, setSelectedSource] = useState('');
    const [selectedLevel, setSelectedLevel] = useState('');
    const [sources, setSources] = useState([]);

    const stompRef = useRef(null);
    const chatRef = useRef(null);
    const roomRef = useRef(null);
    const localVideoRefs = useRef({});
    const localStreamRef = useRef(null);

    useEffect(() => {
        enterRoom();
        initAndFetchUser();
        connectWebSocket();
        return () => {
            if (stompRef.current?.connected) stompRef.current.disconnect();
            if (roomRef.current) roomRef.current.disconnect();
        };
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
            const identity = res.data.data.user_id.toString();
            setUserId(identity);
            setParticipants([{ identity, nickname: `나 (${identity})` }]);
            const mediaStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
            localStreamRef.current = mediaStream;
            const myVideo = localVideoRefs.current[identity];
            if (myVideo) myVideo.srcObject = mediaStream;
            await connectLiveKitSession(identity);
        } catch (err) {
            alert('카메라 권한이 필요합니다.');
        }
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
                setChatMessages((prev) => [...prev, payload]);
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
            roomId,
            text: chatInput
        }));

        setChatInput('');
    };

    const toggleCam = (id) => {
        const el = localVideoRefs.current[id];
        if (el?.srcObject) {
            const track = el.srcObject.getVideoTracks()[0];
            if (track) track.enabled = !track.enabled;
        }
    };

    const toggleMic = (id) => {
        const el = localVideoRefs.current[id];
        if (el?.srcObject) {
            const track = el.srcObject.getAudioTracks()[0];
            if (track) track.enabled = !track.enabled;
        }
    };

    const handleStart = () => setShowModal(true);

    const fetchProblem = async () => {
        if (!selectedSubject || !selectedSource || !selectedLevel) return;
        try {
            const res = await api.get('/quiz/problems/random', {
                params: { subject: selectedSubject, source: selectedSource, level: selectedLevel }
            });
            setProblem(res.data);
            setShowModal(false);
        } catch {
            alert('문제 불러오기 실패');
        }
    };

    const handleRaiseHand = () => {
        stompRef.current.send('/app/quiz/hand', {}, JSON.stringify({ roomId }));
    };

    const handleEndPresentation = () => {
        stompRef.current.send('/app/quiz/end-presentation', {}, JSON.stringify({ roomId }));
        setVotePhase(true);
    };

    const handleVote = (isSuccess) => {
        stompRef.current.send('/app/quiz/vote', {}, JSON.stringify({
            roomId,
            vote: isSuccess ? 'SUCCESS' : 'FAIL'
        }));
    };

    const handleContinue = () => {
        stompRef.current.send('/app/quiz/continue', {}, JSON.stringify({ roomId }));
        setProblem(null);
        setVoteResult(null);
    };

    const handleTerminate = () => {
        stompRef.current.send('/app/quiz/terminate', {}, JSON.stringify({ roomId }));
        navigate('/study/team');
    };

    return (
        <div className="quizroom-wrapper">
            <h1 className="quizroom-title">📘 문제풀이방</h1>
            <div className="quizroom-main-content">
                {/* 문제 섹션 */}
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
                                <button onClick={handleEndPresentation}>🎤 발표 종료</button>
                            )}
                        </div>
                    )}

                    {votePhase && (
                        <div className="vote-section">
                            <h3>발표는 어땠나요?</h3>
                            <button onClick={() => handleVote(true)}>👍 성공</button>
                            <button onClick={() => handleVote(false)}>👎 실패</button>
                        </div>
                    )}

                    {voteResult && (
                        <div className="result-section">
                            <h4>🗳️ 투표 결과</h4>
                            <div>성공: {voteResult.successCount}명</div>
                            <div>실패: {voteResult.failCount}명</div>
                            <div>결과: <strong>{voteResult.result === 'SUCCESS' ? '정답 인정!' : '정답 미인정'}</strong></div>
                        </div>
                    )}

                    <div className="action-buttons">
                        {!problem && <button onClick={handleStart}>문제 선택</button>}
                        {problem && !presenterId && <button onClick={handleRaiseHand}>✋ 손들기</button>}
                        {voteResult && (
                            <>
                                <button onClick={handleContinue}>🔁 계속하기</button>
                                <button onClick={handleTerminate}>⛔ 종료하기</button>
                            </>
                        )}
                    </div>
                </section>

                {/* 캠 화면 */}
                <section className="quizroom-video-section">
                    <h2>캠 화면</h2>
                    <div id="quizroom-video-grid" className="quizroom-video-grid">
                        {participants.map((p) => (
                            <div key={p.identity} className="video-tile">
                                <video
                                    id={`video-${p.identity}`}
                                    ref={(el) => {
                                        if (el) localVideoRefs.current[p.identity] = el;
                                    }}
                                    autoPlay
                                    muted={p.identity === userId}
                                    playsInline
                                />
                                <div className="name">{p.nickname || p.identity}</div>
                                {p.identity === userId && (
                                    <div className="controls">
                                        <button onClick={() => toggleCam(p.identity)}>📷 ON/OFF</button>
                                        <button onClick={() => toggleMic(p.identity)}>🎤 ON/OFF</button>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                </section>

                {/* 채팅 */}
                <section className="quizroom-chat-section">
                    <h2>채팅</h2>
                    <div className="chat-log" ref={chatRef}>
                        {chatMessages.map((msg, idx) => (
                            <div key={idx}><b>{msg.sender}</b>: {msg.text}</div>
                        ))}
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

            {/* 모달 */}
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
