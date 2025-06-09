// src/hooks/useQuizRoom.js
import { useState, useEffect } from 'react';
import useTeamRoomSocket from './useTeamRoomSocket';

/**
 * 문제풀이방 발표 및 투표 흐름 관리 Hook
 *
 * @param {string} roomId
 * @param {string} userId
 */
const useQuizRoom = (roomId, userId) => {
    const [messages, setMessages] = useState([]);
    const [participants, setParticipants] = useState([]);
    const [quizData, setQuizData] = useState(null);

    const [presenterId, setPresenterId] = useState(null);
    const [handRaisedList, setHandRaisedList] = useState([]);
    const [isVoting, setIsVoting] = useState(false);
    const [voteResult, setVoteResult] = useState(null);

    const isMePresenter = presenterId === userId;

    const {
        connectSocket,
        disconnectSocket,
        sendChat,
        sendEvent,
    } = useTeamRoomSocket(
        roomId,
        (chat) => setMessages((prev) => [...prev, chat]),
        (event) => handleServerEvent(event)
    );

    // ✅ 서버에서 오는 소켓 이벤트 처리
    const handleServerEvent = (event) => {
        switch (event.type) {
            case 'RAISE_HAND':
                setHandRaisedList((prev) => (
                    prev.includes(event.sender) ? prev : [...prev, event.sender]
                ));
                break;
            case 'CHOOSE_PRESENTER':
                setPresenterId(event.presenterId);
                setIsVoting(false);
                setVoteResult(null);
                setHandRaisedList([]);
                break;
            case 'START_VOTE':
                setIsVoting(true);
                setVoteResult(null);
                break;
            case 'FINISH_VOTE':
                setIsVoting(false);
                setVoteResult(event.success ? 'success' : 'fail');
                break;
            case 'QUIZ_DATA':
                setQuizData(event.quiz);  // 서버에서 문제 데이터 전송 시
                break;
            default:
                console.log('[서버 이벤트] 처리되지 않음:', event);
        }
    };

    // ✅ 손들기
    const raiseHand = () => {
        sendEvent('RAISE_HAND');
    };

    // ✅ 발표자 직접 요청 (방장이 클릭할 경우)
    const announcePresenter = () => {
        sendEvent('CHOOSE_PRESENTER', { presenterId: userId });
    };

    // ✅ 투표 시작 요청
    const startVote = () => {
        sendEvent('START_VOTE');
    };

    // ✅ 투표 결과 제출
    const vote = (agree) => {
        sendEvent('SUBMIT_VOTE', { vote: agree });
    };

    // ✅ 문제풀이 시작 요청 (방장이 시작 버튼 눌렀을 때)
    const startQuiz = () => {
        sendEvent('START_QUIZ');
    };

    // ✅ 다음 문제로 넘어가기
    const continueQuiz = () => {
        sendEvent('CONTINUE_QUIZ');
    };

    // ✅ 종료
    const endQuiz = () => {
        sendEvent('TERMINATE_ROOM');
    };

    // ✅ 초기화
    const resetAll = () => {
        setPresenterId(null);
        setHandRaisedList([]);
        setIsVoting(false);
        setVoteResult(null);
    };

    return {
        messages,
        sendChat,
        quizData,
        participants,

        presenterId,
        isMePresenter,
        handRaisedList,
        isVoting,
        voteResult,

        raiseHand,
        announcePresenter,
        startVote,
        vote,
        startQuiz,
        continueQuiz,
        endQuiz,

        connectSocket,
        disconnectSocket,
        resetAll,
    };
};

export default useQuizRoom;
