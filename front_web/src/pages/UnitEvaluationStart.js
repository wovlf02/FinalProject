import React, {useState, useEffect} from 'react';
import {useNavigate, useLocation} from 'react-router-dom';
import QuizSidebar from './QuizSidebar';
import '../css/UnitEvaluationStart.css';

const NUM_QUESTIONS = 20;

// 임시 문제 데이터 (정답 포함)
const makeQuestions = () =>
    Array(NUM_QUESTIONS)
        .fill(null)
        .map((_, i) => ({
            id: i + 1,
            text: `문제 ${i + 1}: 예시 문항입니다.`,
            correctAnswer: i % 2 === 0 ? 'O' : 'X', // 짝수 O, 홀수 X (예시)
            answer: null,
        }));

const LOCAL_KEY = 'quizProgress';

const UnitEvaluationStart = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const {unitName, subject, level} = location.state || {};

    // 자동 저장/복원
    const [questions, setQuestions] = useState(() => {
        const saved = localStorage.getItem(LOCAL_KEY);
        return saved ? JSON.parse(saved) : makeQuestions();
    });
    const [currentIdx, setCurrentIdx] = useState(0);

    // 타이머
    const [timeLeft, setTimeLeft] = useState(1800); // 30분
    useEffect(() => {
        const timer = setInterval(() => {
            setTimeLeft(prev => {
                if (prev <= 0) {
                    handleSubmit();
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);
        return () => clearInterval(timer);
        // eslint-disable-next-line
    }, []);

    // 키보드 O/X 입력
    useEffect(() => {
        const handleKeyPress = (e) => {
            if (e.key === 'o' || e.key === 'O') handleAnswer(currentIdx, 'O');
            if (e.key === 'x' || e.key === 'X') handleAnswer(currentIdx, 'X');
        };
        window.addEventListener('keydown', handleKeyPress);
        return () => window.removeEventListener('keydown', handleKeyPress);
        // eslint-disable-next-line
    }, [currentIdx, questions]);

    // 자동 저장
    useEffect(() => {
        localStorage.setItem(LOCAL_KEY, JSON.stringify(questions));
    }, [questions]);

    // 제출 모달
    const [showConfirm, setShowConfirm] = useState(false);
    const [unanswered, setUnanswered] = useState([]);

    // 답변 선택
    const handleAnswer = (idx, answer) => {
        setQuestions(qs =>
            qs.map((q, i) => (i === idx ? {...q, answer} : q))
        );
        if (idx < questions.length - 1) setCurrentIdx(idx + 1);
    };

    // 제출
    const handleSubmit = () => {
        const notAnswered = questions.filter(q => !q.answer).map(q => q.id);
        if (notAnswered.length > 0) {
            setUnanswered(notAnswered);
            setShowConfirm(true);
            setCurrentIdx(notAnswered[0] - 1);
            return;
        }
        if (window.confirm('제출하시겠습니까?')) {
            localStorage.removeItem(LOCAL_KEY);
            const score = questions.filter(q => q.answer === q.correctAnswer).length;
            navigate('/quiz-result', {state: {questions, score}});
        }
    };

    // 강제 제출(미해결 있어도)
    const handleForceSubmit = () => {
        setShowConfirm(false);
        if (window.confirm('미해결 문제가 있어도 제출하시겠습니까?')) {
            localStorage.removeItem(LOCAL_KEY);
            const score = questions.filter(q => q.answer === q.correctAnswer).length;
            navigate('/quiz-result', {state: {questions, score}});
        }
    };

    // 타이머 표시
    const formatTime = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    return (
        <div className="quiz-layout">
            <QuizSidebar
                questions={questions}
                currentIdx={currentIdx}
                setCurrentIdx={setCurrentIdx}
            />
            <main className="quiz-main">
                <h2 className="unit-eval-title">단원평가: {unitName || '단원명 미지정'}</h2>
                <div className="unit-eval-meta">
                    <span><b>과목:</b> {subject || '-'}</span>
                    <span><b>난이도:</b> {level || '-'}</span>
                </div>
                <div className="timer">남은 시간: {formatTime(timeLeft)}</div>
                <hr className="unit-eval-divider"/>
                <div className="quiz-question-section">
                    <div className="quiz-question-num">문제 {questions[currentIdx].id}</div>
                    <div className="quiz-question-text">{questions[currentIdx].text}</div>
                    <div className="quiz-answer-buttons">
                        <button
                            className={`quiz-answer-btn o${questions[currentIdx].answer === 'O' ? ' selected' : ''}`}
                            onClick={() => handleAnswer(currentIdx, 'O')}
                        >
                            O
                        </button>
                        <button
                            className={`quiz-answer-btn x${questions[currentIdx].answer === 'X' ? ' selected' : ''}`}
                            onClick={() => handleAnswer(currentIdx, 'X')}
                        >
                            X
                        </button>
                    </div>
                    <div className="quiz-nav-btns">
                        <button
                            className="quiz-nav-btn"
                            onClick={() => setCurrentIdx(idx => Math.max(0, idx - 1))}
                            disabled={currentIdx === 0}
                        >
                            이전
                        </button>
                        <button
                            className="quiz-nav-btn"
                            onClick={() => setCurrentIdx(idx => Math.min(questions.length - 1, idx + 1))}
                            disabled={currentIdx === questions.length - 1}
                        >
                            다음
                        </button>
                    </div>
                </div>
                <div style={{display: 'flex', gap: 16, marginTop: 32}}>
                    <button
                        className="unit-eval-main-btn"
                        onClick={() => {
                            localStorage.removeItem(LOCAL_KEY);
                            navigate('/dashboard');
                        }}
                    >
                        메인으로 돌아가기
                    </button>
                    <button
                        className="unit-eval-main-btn"
                        style={{background: '#2563eb'}}
                        onClick={handleSubmit}
                    >
                        제출하기
                    </button>
                </div>
                {/* 미해결 모달 */}
                {showConfirm && (
                    <div className="quiz-modal-overlay">
                        <div className="quiz-modal">
                            <h3>아직 안 푼 문제가 있습니다!</h3>
                            <div style={{margin: '16px 0', color: '#ef4444'}}>
                                {unanswered.join(', ')}번 문제
                            </div>
                            <div style={{display: 'flex', gap: 16, justifyContent: 'center'}}>
                                <button className="quiz-modal-btn" onClick={() => setShowConfirm(false)}>계속 풀기</button>
                                <button className="quiz-modal-btn force" onClick={handleForceSubmit}>강제 제출</button>
                            </div>
                        </div>
                    </div>
                )}
            </main>
        </div>
    );
};

export default UnitEvaluationStart;
