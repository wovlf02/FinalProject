import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import useWebRTC from '../hooks/useWebRTC';
import useQuizRoom from '../hooks/useQuizRoom';
import QuizBoard from '../components/teamstudy/QuizBoard';
import HandRaiseList from '../components/teamstudy/HandRaiseList';
import VoteModal from '../components/teamstudy/VoteModal';
import ChatBox from '../components/chat/ChatBox';
import FileUploader from '../components/chat/FileUploader';
import '../css/QuizRoom.css';

const QuizRoom = () => {
    const { roomId } = useParams();

    const { localVideoRef, remoteStreams, startCamera } = useWebRTC();
    const {
        messages, sendMessage,
        participants, raiseHand, vote, voteStatus,
        currentPresenter, isVoting, quizData,
        userId, isMePresenter
    } = useQuizRoom(roomId);

    useEffect(() => {
        startCamera();
    }, []);

    return (
        <div className="quiz-room-container">
            <div className="left-panel">
                <h2>문제 풀이방</h2>
                <QuizBoard quiz={quizData} />

                <div className="video-section">
                    <div className="local-video-wrapper">
                        <video
                            ref={localVideoRef}
                            autoPlay
                            muted
                            playsInline
                            className={`video-box ${isMePresenter ? 'presenter' : ''}`}
                        />
                        <div className="user-label">나 (You)</div>
                    </div>

                    {remoteStreams.map(({ id, stream, isPresenter }) => (
                        <div key={id} className="remote-video-wrapper">
                            <video
                                ref={video => video && (video.srcObject = stream)}
                                autoPlay
                                playsInline
                                className={`video-box ${isPresenter ? 'presenter' : ''}`}
                            />
                            <div className="user-label">{id}</div>
                        </div>
                    ))}
                </div>

                <HandRaiseList
                    participants={participants}
                    onRaiseHand={raiseHand}
                    currentPresenter={currentPresenter}
                    userId={userId}
                />

                {isVoting && (
                    <VoteModal onVote={vote} status={voteStatus} />
                )}
            </div>

            <div className="right-panel">
                <ChatBox messages={messages} onSend={sendMessage} />
                <FileUploader roomId={roomId} />
            </div>
        </div>
    );
};

export default QuizRoom;
