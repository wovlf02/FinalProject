import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import useWebRTC from '../hooks/useWebRTC';
import useFocusTimer from '../hooks/useFocusTimer';
import useTeamRoomSocket from '../hooks/useTeamRoomSocket';
import FocusRankingBoard from '../components/teamstudy/FocusRankingBoard';
import ChatBox from '../components/chat/ChatBox';
import FileUploader from '../components/chat/FileUploader';
import '../css/FocusRoom.css';

const FocusRoom = () => {
    const { roomId } = useParams();
    const navigate = useNavigate();

    const { localVideoRef, startCamera } = useWebRTC();
    const { time, startTimer, stopTimer } = useFocusTimer(roomId);
    const { ranking, sendMessage, chatMessages } = useTeamRoomSocket(roomId, 'FOCUS');

    useEffect(() => {
        startCamera();
        startTimer();

        return () => {
            stopTimer(); // cleanup 타이머 종료
        };
    }, []);

    return (
        <div className="focus-room-container">
            <div className="left-panel">
                <h2>📚 집중 공부방</h2>
                <video ref={localVideoRef} autoPlay muted playsInline className="local-video" />

                <div className="study-time-display">
                    집중 시간: {Math.floor(time / 60)}분 {time % 60}초
                </div>

                <FocusRankingBoard ranking={ranking} />
            </div>

            <div className="right-panel">
                <ChatBox messages={chatMessages} onSend={sendMessage} />
                <FileUploader roomId={roomId} />
            </div>
        </div>
    );
};

export default FocusRoom;
