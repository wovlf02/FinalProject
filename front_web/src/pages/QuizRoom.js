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

    const [problem, setProblem] = useState(null);
    const [presenterId, setPresenterId] = useState(null);
    const [votePhase, setVotePhase] = useState(false);
    const [voteResult, setVoteResult] = useState(null);
    const [userId, setUserId] = useState(null);
    const [participants, setParticipants] = useState([]);
    const [chatMessages, setChatMessages] = useState([]);
    const [chatInput, setChatInput] = useState('');

    const stompRef = useRef(null);
    const chatRef = useRef(null);
    const roomRef = useRef(null);
    const localVideoRefs = useRef({});
    const roomName = `quiz-${roomId}`;

    useEffect(() => {
        enterRoom();
        fetchUserInfo();
        connectWebSocket();
        connectChatSocket();

        return () => {
            if (stompRef.current) stompRef.current.disconnect();
            if (roomRef.current) roomRef.current.disconnect();
        };
    }, []);

    const enterRoom = async () => {
        try {
            await api.post('/study/team/enter', null, { params: { roomId } });
        } catch (err) {
            alert('방 입장 실패');
            navigate('/study/team');
        }
    };

    const fetchUserInfo = async () => {
        try {
            const res = await api.get('/users/me');
            const identity = res.data.user_id;
            setUserId(identity);
            await connectLiveKit(identity);
        } catch (err) {
            console.error('유저 정보 조회 실패', err);
            alert('로그인이 필요합니다.');
            navigate('/');
        }
    };

    const connectLiveKit = async (identity) => {
        try {
            const res = await api.post('/livekit/token', {
                identity,
                room_name: roomName,
            });
            const { token, wsUrl } = res.data;

            const room = await connectToLiveKit(identity, roomName, wsUrl, token);
            roomRef.current = room;

            const updateTracks = () => {
                const all = Array.from(room.participants.values()).concat(room.localParticipant);
                setParticipants(all);

                all.forEach((p) => {
                    p.videoTracks.forEach((pub) => {
                        const mediaStream = new MediaStream([pub.track.mediaStreamTrack]);
                        const el = localVideoRefs.current[p.identity];
                        if (el && !el.srcObject) el.srcObject = mediaStream;
                    });
                });
            };

            room.on('participantConnected', updateTracks);
            room.on('participantDisconnected', updateTracks);
            updateTracks();

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
            client.send('/app/quiz/enter', {}, JSON.stringify({ roomId }));

            client.subscribe(`/sub/quiz/room/${roomId}/problem`, (msg) => {
                const data = JSON.parse(msg.body);
                setProblem(data);
                setPresenterId(null);
                setVotePhase(false);
                setVoteResult(null);
            });

            client.subscribe(`/sub/quiz/room/${roomId}/presenter`, (msg) => {
                setPresenterId(Number(msg.body));
            });

            client.subscribe(`/sub/quiz/room/${roomId}/vote-result`, (msg) => {
                const result = JSON.parse(msg.body);
                setVoteResult(result);
                setVotePhase(false);
            });
        });
    };

    const connectChatSocket = () => {
        const sock = new SockJS('/ws');
        const client = Stomp.over(sock);
        client.connect({}, () => {
            client.subscribe(`/sub/chat/room/${roomId}`, (msg) => {
                const payload = JSON.parse(msg.body);
                setChatMessages((prev) => [...prev, payload]);
                setTimeout(() => {
                    chatRef.current?.scrollTo(0, chatRef.current.scrollHeight);
                }, 100);
            });
        });
        stompRef.current = client;
    };

    const sendMessage = (e) => {
        e.preventDefault();
        if (!chatInput.trim()) return;
        stompRef.current.send('/app/chat/send', {}, JSON.stringify({
            roomId,
            sender: userId,
            text: chatInput,
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

    const handleStart = () => {
        stompRef.current.send('/app/quiz/start', {}, JSON.stringify({ roomId }));
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
            vote: isSuccess ? 'SUCCESS' : 'FAIL',
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
        <div className="quiz-room-container">
            <h1>🧠 문제풀이방</h1>

            <div className="main-layout">
                <div className="video-grid">
                    {participants.map((p) => (
                        <div key={p.identity} className="video-tile">
                            <video
                                ref={(el) => {
                                    if (el) localVideoRefs.current[p.identity] = el;
                                }}
                                autoPlay
                                muted={p.identity === userId.toString()}
                                playsInline
                            />
                            <div>{p.identity}</div>
                            {p.identity === userId.toString() && (
                                <>
                                    <button onClick={() => toggleCam(p.identity)}>📷 ON/OFF</button>
                                    <button onClick={() => toggleMic(p.identity)}>🎤 ON/OFF</button>
                                </>
                            )}
                        </div>
                    ))}
                </div>

                <div className="chat-section">
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
                </div>
            </div>

            {problem && (
                <div className="problem-section">
                    <h2>{problem.title}</h2>
                    <p className="passage">{problem.passage}</p>
                    <ul>
                        {problem.choices.map((c, idx) => (
                            <li key={idx}>{String.fromCharCode(65 + idx)}. {c}</li>
                        ))}
                    </ul>
                </div>
            )}

            {presenterId && (
                <div className="presenter-section">
                    <p>🗣️ 발표자: 사용자 {presenterId}</p>
                    {Number(presenterId) === Number(userId) && (
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
                    <h3>🗳️ 투표 결과</h3>
                    <p>성공: {voteResult.successCount}명</p>
                    <p>실패: {voteResult.failCount}명</p>
                    <p>결과: <strong>{voteResult.result === 'SUCCESS' ? '정답 인정!' : '정답 미인정'}</strong></p>
                </div>
            )}

            <div className="action-buttons">
                {!problem && <button onClick={handleStart}>문제 시작</button>}
                {problem && !presenterId && <button onClick={handleRaiseHand}>✋ 손들기</button>}
                {voteResult && (
                    <>
                        <button onClick={handleContinue}>🔁 계속하기</button>
                        <button onClick={handleTerminate}>⛔ 종료하기</button>
                    </>
                )}
            </div>
        </div>
    );
};

export default QuizRoom;
