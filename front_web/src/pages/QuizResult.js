import React from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import '../css/UnitEvaluationStart.css';

const QuizResult = () => {
    const {state} = useLocation();
    const navigate = useNavigate();
    const {questions = [], score = 0} = state || {};

    return (
        <div className="quiz-result-container">
            <h2 className="quiz-result-title">채점 결과</h2>
            <div className="quiz-result-score">
                <b>점수:</b> {score} / {questions.length}
            </div>
            <div className="quiz-result-list">
                {questions.map((q) => (
                    <div
                        key={q.id}
                        className={`quiz-result-item ${q.answer === q.correctAnswer ? 'correct' : 'wrong'}`}
                    >
                        <div>
                            <b>문제 {q.id}:</b> {q.text}
                        </div>
                        <div>
                            <b>내 답:</b>{' '}
                            <span className={q.answer === 'O' ? 'o' : q.answer === 'X' ? 'x' : ''}>
                {q.answer || '미입력'}
              </span>
                        </div>
                        <div>
                            <b>정답:</b>{' '}
                            <span className={q.correctAnswer === 'O' ? 'o' : 'x'}>
                {q.correctAnswer}
              </span>
                        </div>
                    </div>
                ))}
            </div>
            <button
                className="unit-eval-main-btn"
                style={{marginTop: 32}}
                onClick={() => navigate('/dashboard')}
            >
                대시보드로 돌아가기
            </button>
        </div>
    );
};

export default QuizResult;
