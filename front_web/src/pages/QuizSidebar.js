import React from 'react';

const QuizSidebar = ({questions, currentIdx, setCurrentIdx}) => {
    return (
        <aside className="quiz-sidebar">
            <div className="quiz-sidebar-title">문제 목록</div>
            <ul className="quiz-sidebar-list">
                {questions.map((q, idx) => (
                    <li
                        key={q.id}
                        className={`quiz-sidebar-item${idx === currentIdx ? ' active' : ''}`}
                        onClick={() => setCurrentIdx(idx)}
                    >
                        <span>{q.id}</span>
                        <span className={`quiz-sidebar-answer ${q.answer === 'O' ? 'o' : q.answer === 'X' ? 'x' : ''}`}>
              {q.answer || ''}
            </span>
                    </li>
                ))}
            </ul>
        </aside>
    );
};

export default QuizSidebar;
