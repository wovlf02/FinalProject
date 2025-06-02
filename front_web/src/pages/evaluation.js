import React, {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import '../css/Evaluation.css';

function getDday(dateStr) {
    const today = new Date();
    const target = new Date(dateStr);
    target.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);
    return Math.max(0, Math.ceil((target - today) / (1000 * 60 * 60 * 24)));
}

function getProgress(current, total) {
    if (!total || total === 0) return 0;
    return Math.min(100, Math.round((current / total) * 100));
}

function getRemain(current, total) {
    return Math.max(0, total - current);
}

const resultList = [
    {subject: '수학', name: '2학기 중간고사', score: 95, avg: 82, rank: '상위 15%'},
    {subject: '영어', name: '2학기 중간고사', score: 88, avg: 79, rank: '상위 25%'},
    {subject: '과학', name: '1차 수행평가', score: 92, avg: 85, rank: '상위 20%'},
];

const chartData = [
    {name: '1학기 중간', score: 80, avg: 75},
    {name: '1학기 기말', score: 85, avg: 78},
    {name: '2학기 중간', score: 95, avg: 82},
    {name: '2학기 기말', score: 90, avg: 85},
];

function Evaluation() {
    const navigate = useNavigate();
    // 시험 목록: localStorage에서 불러오고, 없으면 빈 배열
    const [tests, setTests] = useState(() => {
        const saved = localStorage.getItem('myTests');
        return saved ? JSON.parse(saved) : [];
    });
    const [showAddModal, setShowAddModal] = useState(false);
    const [addForm, setAddForm] = useState({
        title: '',
        date: '',
        range: '',
        current: '',
    });
    const [showDetailModal, setShowDetailModal] = useState(false);
    const [detailTest, setDetailTest] = useState(null);

    // 시험 목록이 바뀔 때마다 localStorage에 저장
    useEffect(() => {
        localStorage.setItem('myTests', JSON.stringify(tests));
    }, [tests]);

    // 새 시험 추가
    const handleAddTest = (e) => {
        e.preventDefault();
        if (!addForm.title || !addForm.date || !addForm.range) return;
        setTests([
            ...tests,
            {
                title: addForm.title,
                date: addForm.date,
                range: Number(addForm.range),
                current: Number(addForm.current) || 0,
            },
        ]);
        setAddForm({title: '', date: '', range: '', current: ''});
        setShowAddModal(false);
    };

    // 모달 닫기
    const handleCloseModal = () => {
        setShowAddModal(false);
        setAddForm({title: '', date: '', range: '', current: ''});
    };

    // 상세보기 모달
    const handleDetail = (test) => {
        setDetailTest(test);
        setShowDetailModal(true);
    };
    const handleCloseDetail = () => {
        setShowDetailModal(false);
        setDetailTest(null);
    };

    // 시험 삭제
    const handleDeleteTest = (title, date) => {
        setTests(tests.filter(t => !(t.title === title && t.date === date)));
        setShowDetailModal(false);
    };

    // 기타 버튼 예시
    const handleSchedule = () => navigate('/unit-evaluation/schedule');
    const handleStudyPlan = () => navigate('/unit-evaluation/plan');
    const handleAIFeedback = () => navigate('/unit-evaluation/feedback');

    return (
        <div className="evaluation-container">
            <div className="evaluation-header">
                <h1>단원 평가</h1>
                <div className="evaluation-header-actions">
                    <button className="primary" onClick={() => setShowAddModal(true)}>+ 새 평가 추가</button>
                    <button onClick={handleSchedule}>일정</button>
                    <button onClick={() => navigate('/unit-evaluation')}>시험 보기</button>
                    <button onClick={handleStudyPlan}>학습 계획</button>
                    <button onClick={handleAIFeedback}>AI 피드백</button>
                </div>
            </div>

            <div className="evaluation-section">
                <h2>시험 준비 현황</h2>
                <div className="evaluation-test-list">
                    {tests.length === 0 && (
                        <div style={{color: "#888", margin: "30px 0"}}>아직 등록된 시험이 없습니다.</div>
                    )}
                    {tests.map((test, idx) => {
                        const dday = getDday(test.date);
                        const progress = getProgress(test.current, test.range);
                        const remain = getRemain(test.current, test.range);
                        return (
                            <div key={test.title + test.date} className="evaluation-test-card">
                                <div className="test-title">{test.title}</div>
                                <div className="test-dday">D-{dday}</div>
                                <div className="test-progress-bar-bg">
                                    <div
                                        className="test-progress-bar"
                                        style={{width: `${progress}%`}}
                                    />
                                </div>
                                <div className="test-progress-info">
                                    <span>학습 진도: {progress}%</span>
                                    <span>남은 단원: {remain}개</span>
                                </div>
                                <button className="test-detail-btn" onClick={() => handleDetail(test)}>
                                    상세보기
                                </button>
                            </div>
                        );
                    })}
                    <button className="evaluation-test-add-btn" onClick={() => setShowAddModal(true)}>+ 새 시험 추가</button>
                </div>
            </div>

            {/* 새 시험 추가 모달 */}
            {showAddModal && (
                <div className="evaluation-modal-overlay">
                    <div className="evaluation-modal-card">
                        <button className="evaluation-modal-close" onClick={handleCloseModal}>×</button>
                        <h3>새 시험 추가</h3>
                        <form onSubmit={handleAddTest}>
                            <div className="evaluation-modal-row">
                                <label>시험명</label>
                                <input
                                    type="text"
                                    value={addForm.title}
                                    onChange={e => setAddForm(f => ({...f, title: e.target.value}))}
                                    required
                                />
                            </div>
                            <div className="evaluation-modal-row">
                                <label>시험일</label>
                                <input
                                    type="date"
                                    value={addForm.date}
                                    onChange={e => setAddForm(f => ({...f, date: e.target.value}))}
                                    required
                                />
                            </div>
                            <div className="evaluation-modal-row">
                                <label>시험범위(단원 수)</label>
                                <input
                                    type="number"
                                    min={1}
                                    value={addForm.range}
                                    onChange={e => setAddForm(f => ({...f, range: e.target.value}))}
                                    required
                                />
                            </div>
                            <div className="evaluation-modal-row">
                                <label>현재 한 범위(단원 수)</label>
                                <input
                                    type="number"
                                    min={0}
                                    value={addForm.current}
                                    onChange={e => setAddForm(f => ({...f, current: e.target.value}))}
                                />
                            </div>
                            <button type="submit" className="evaluation-modal-submit">추가</button>
                        </form>
                    </div>
                </div>
            )}

            {/* 상세보기 모달 */}
            {showDetailModal && detailTest && (
                <div className="evaluation-modal-overlay">
                    <div className="evaluation-modal-card">
                        <button className="evaluation-modal-close" onClick={handleCloseDetail}>×</button>
                        <h3>시험 상세 정보</h3>
                        <div className="evaluation-modal-row">
                            <label>시험명</label>
                            <div>{detailTest.title}</div>
                        </div>
                        <div className="evaluation-modal-row">
                            <label>시험일</label>
                            <div>{detailTest.date}</div>
                        </div>
                        <div className="evaluation-modal-row">
                            <label>시험범위(단원 수)</label>
                            <div>{detailTest.range}개</div>
                        </div>
                        <div className="evaluation-modal-row">
                            <label>현재 한 범위(단원 수)</label>
                            <div>{detailTest.current}개</div>
                        </div>
                        <div className="evaluation-modal-row">
                            <label>D-day</label>
                            <div>D-{getDday(detailTest.date)}</div>
                        </div>
                        <div className="evaluation-modal-row">
                            <label>학습 진도</label>
                            <div>{getProgress(detailTest.current, detailTest.range)}%</div>
                        </div>
                        <div className="evaluation-modal-row">
                            <label>남은 단원</label>
                            <div>{getRemain(detailTest.current, detailTest.range)}개</div>
                        </div>
                        <button
                            className="evaluation-modal-delete"
                            onClick={() => handleDeleteTest(detailTest.title, detailTest.date)}
                        >
                            삭제하기
                        </button>
                    </div>
                </div>
            )}

            <div className="evaluation-bottom-row">
                <div className="evaluation-result-card">
                    <h2>최근 평가 결과</h2>
                    <table>
                        <thead>
                        <tr>
                            <th>과목</th>
                            <th>평가명</th>
                            <th>점수</th>
                            <th>평균</th>
                            <th>석차</th>
                        </tr>
                        </thead>
                        <tbody>
                        {resultList.map((r, idx) => (
                            <tr key={idx}>
                                <td>{r.subject}</td>
                                <td>{r.name}</td>
                                <td>{r.score}점</td>
                                <td>{r.avg}점</td>
                                <td>{r.rank}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
                <div className="evaluation-chart-card">
                    <h2>성적 분석</h2>
                    <div className="evaluation-chart-area">
                        <svg width="220" height="140">
                            {/* Y축 */}
                            <line x1="30" y1="20" x2="30" y2="120" stroke="#ddd"/>
                            {/* X축 */}
                            <line x1="30" y1="120" x2="200" y2="120" stroke="#ddd"/>
                            {/* 평균 점수 선 */}
                            <polyline
                                fill="none"
                                stroke="#a78bfa"
                                strokeWidth="2"
                                points={
                                    chartData.map((d, i) =>
                                        `${30 + i * 45},${120 - (d.avg - 70) * 2}`
                                    ).join(' ')
                                }
                            />
                            {/* 내 점수 선 */}
                            <polyline
                                fill="none"
                                stroke="#6366f1"
                                strokeWidth="2"
                                points={
                                    chartData.map((d, i) =>
                                        `${30 + i * 45},${120 - (d.score - 70) * 2}`
                                    ).join(' ')
                                }
                            />
                            {/* 점 찍기 */}
                            {chartData.map((d, i) => (
                                <circle
                                    key={i}
                                    cx={30 + i * 45}
                                    cy={120 - (d.score - 70) * 2}
                                    r="3"
                                    fill="#6366f1"
                                />
                            ))}
                            {chartData.map((d, i) => (
                                <circle
                                    key={i + 'avg'}
                                    cx={30 + i * 45}
                                    cy={120 - (d.avg - 70) * 2}
                                    r="3"
                                    fill="#a78bfa"
                                />
                            ))}
                            {/* X축 라벨 */}
                            {chartData.map((d, i) => (
                                <text
                                    key={i + 'label'}
                                    x={30 + i * 45}
                                    y={135}
                                    textAnchor="middle"
                                    fontSize="11"
                                    fill="#888"
                                >
                                    {d.name}
                                </text>
                            ))}
                            {/* 범례 */}
                            <rect x="140" y="30" width="10" height="3" fill="#6366f1"/>
                            <text x="155" y="35" fontSize="11" fill="#6366f1">내 점수</text>
                            <rect x="140" y="45" width="10" height="3" fill="#a78bfa"/>
                            <text x="155" y="50" fontSize="11" fill="#a78bfa">평균 점수</text>
                        </svg>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Evaluation;
