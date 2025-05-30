// src/components/teamstudy/QuizBoard.js
import React from 'react';
import '../../css/QuizRoom.css';

const QuizBoard = ({ problem, showExplanation = false }) => {
    if (!problem) return null;

    const optionNumbers = ['①', '②', '③', '④', '⑤'];

    return (
        <div className="quiz-board">
            <h2 className="problem-title">🧠 문제</h2>
            <div className="problem-content">{problem.question}</div>

            <h3 className="options-title">선택지</h3>
            <ul className="options-list">
                {problem.options.map((opt, idx) => (
                    <li key={idx} className="option-item">
                        <span className="option-number">{optionNumbers[idx] || `${idx + 1}.`}</span>
                        <span className="option-text">{opt}</span>
                    </li>
                ))}
            </ul>

            {showExplanation && problem.explanation && (
                <div className="explanation-box">
                    <h3>📘 해설</h3>
                    <p>{problem.explanation}</p>
                </div>
            )}
        </div>
    );
};

export default QuizBoard;
