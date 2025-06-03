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

    // 예시 문제 (실제 DB 연동시 이 구조에 맞게 받아오세요)
    const problemExample = {
        title: '수학 · 미분과 적분',
        passage: '다음 함수 f(x) = 2x³ - 3x² + 4x - 10에 대하여 다음을 구하시오.',
        choices: [
            'f(x)를 구하시오.',
            'x=2에서의 접선의 방정식을 구하시오.',
            'f(x)의 증가구간과 감소구간을 구하시오.'
        ]
    };

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

    useEffect(() => {
        enterRoom();
        fetchUserInfo();
        connectWebSocket();

        return () => {
            if (stompRef.current?.connected) {
                stompRef.current.disconnect(() => {
                    console.log('📴 STOMP 연결 해제됨');
                });
            }
            if (roomRef.current) {
                roomRef.current.disconnect();
                console.log('📴 LiveKit 연결 해제됨');
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

    useEffect(() => {
        if (presenterId !== null) {
            setVoteResult(null); // 발표자가 바뀌면 초기화
        }
    }, [presenterId]);

    const connectLiveKitSession = async (identity) => {
        try {
            const res = await api.post('/livekit/token', { roomName });
            const { token, wsUrl } = res.data;

            const room = await connectToLiveKit(identity, roomName, wsUrl, token, 'quizroom-video-grid');
            roomRef.current = room;

            room.on('participantConnected', (participant) => {
                setParticipants((prev) => {
                    const exists = prev.some(p => p.identity === participant.identity);
                    if (!exists) {
                        return [...prev, { identity: participant.identity, nickname: `참가자 ${participant.identity}` }];
                    }
                    return prev;
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
                            document.getElementById('quizroom-video-grid')?.appendChild(el);
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
        const sock = new SockJS('/ws', null, {
            transports: ['websocket', 'xhr-streaming', 'xhr-polling'],
            withCredentials: true
        });
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
                setPresenterId(msg.body.toString());
            });

            client.subscribe(`/sub/quiz/room/${roomId}/vote-result`, (msg) => {
                const result = JSON.parse(msg.body);
                setVoteResult(result);
                setVotePhase(false);
            });

            client.subscribe(`/sub/chat/room/${roomId}`, (msg) => {
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

    // 문제 시작 버튼 클릭 시 예시 문제 세팅
    // 실제 DB 연동시 setProblem(서버에서 받아온 문제 데이터)로 교체
    const handleStart = () => {
        setProblem(problemExample);
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
        <div className="quizroom-main-content">
            {/* 문제 영역 */}
            <section className="quizroom-problem-section">
                <h2>문제풀이</h2>
                {problem ? (
                    <>
                        <div className="problem-title">{problem.title}</div>
                        <div className="problem-passage">{problem.passage}</div>
                        <ul className="problem-choices">
                            {problem.choices.map((c, idx) => (
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
                    {!problem && <button onClick={handleStart}>문제 시작</button>}
                    {problem && !presenterId && <button onClick={handleRaiseHand}>✋ 손들기</button>}
                    {voteResult && (
                        <>
                            <button onClick={handleContinue}>🔁 계속하기</button>
                            <button onClick={handleTerminate}>⛔ 종료하기</button>
                        </>
                    )}
                </div>
            </section>

            {/* 캠 영역 */}
            <section className="quizroom-video-section">
                <h2>캠</h2>
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
                            <div>{p.nickname || p.identity}</div>
                            {p.identity === userId && (
                                <div>
                                    <button onClick={() => toggleCam(p.identity)}>📷 ON/OFF</button>
                                    <button onClick={() => toggleMic(p.identity)}>🎤 ON/OFF</button>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            </section>

            {/* 채팅 영역 */}
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
    );
};

export default QuizRoom;
