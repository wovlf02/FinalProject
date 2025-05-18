import React from 'react';
import {useNavigate} from 'react-router-dom';
import '../css/StudyStart.css';

const StudyStart = () => {
    const navigate = useNavigate();

    return (
        <div className="study-start-container">
            <h1 className="study-start-title">학습 모드 선택</h1>
            <div className="study-start-card-row">
                {/* 개인 학습 카드 */}
                <div className="study-start-card">
                    <div className="study-start-card-icon">
                        <span role="img" aria-label="개인">👤</span>
                    </div>
                    <h2 className="study-start-card-title">개인 학습</h2>
                    <p className="study-start-card-desc">
                        혼자서 자유롭게 학습하고 진도를 관리할 수 있습니다.
                    </p>
                    <ul className="study-start-card-list">
                        <li>맞춤형 학습 진도 관리</li>
                        <li>카메라 동작 탐지로 공부 환경 조성</li>
                    </ul>
                    <button
                        className="study-start-card-btn"
                        onClick={() => navigate('/personalStudy')}
                    >
                        시작하기
                    </button>
                </div>
                {/* 팀 학습 카드 */}
                <div className="study-start-card">
                    <div className="study-start-card-icon">
                        <span role="img" aria-label="팀">👥</span>
                    </div>
                    <h2 className="study-start-card-title">팀 학습</h2>
                    <p className="study-start-card-desc">
                        친구들과 함께 학습하고 경쟁할 수 있습니다.
                    </p>
                    <ul className="study-start-card-list">
                        <li>실시간 경쟁 모드</li>
                        <li>팀원간 학습을 통해 공부 환경 조성</li>
                    </ul>
                    <button
                        className="study-start-card-btn"
                        onClick={() => navigate('/teamStudy')}
                    >
                        시작하기
                    </button>
                </div>
            </div>
        </div>
    );
};

export default StudyStart;
