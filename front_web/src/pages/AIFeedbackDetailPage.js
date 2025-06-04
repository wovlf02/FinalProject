import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

function extractNumber(str) {
  if (!str) return '';
  const match = str.match(/[1-5]/);
  return match ? match[0] : str;
}

// 이미지 URL을 항상 백엔드(8080)로 맞추는 함수
function getImageUrl(imagePath) {
  if (!imagePath) return '';
  if (imagePath.startsWith('http')) return imagePath;
  return `http://localhost:8080/${imagePath.replace(/^\//, '')}`;
}

const AIFeedbackDetailPage = () => {
  const { resultId } = useParams();
  const navigate = useNavigate();
  const [result, setResult] = useState(null);
  const [problemMap, setProblemMap] = useState({});
  const [openedIndex, setOpenedIndex] = useState(null);
  const [aiFeedbacks, setAiFeedbacks] = useState({});
  const [loadingIndex, setLoadingIndex] = useState(null);
  const [selectedProblemId, setSelectedProblemId] = useState(null);
  const [aiFeedback, setAiFeedback] = useState('');
  const [loadingFeedback, setLoadingFeedback] = useState(false);
  const [openPassageIndex, setOpenPassageIndex] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    axios.get(`/api/results/${resultId}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    })
      .then(res => {
        setResult(res.data);
        if (res.data && res.data.details) {
          const ids = res.data.details.map(d => d.problemId).join(',');
          axios.get(`/api/exam/questions/by-ids?ids=${ids}`)
            .then(qres => {
              const map = {};
              qres.data.forEach(q => {
                map[Number(q.problemId || q.id)] = q;
              });
              setProblemMap(map);
            });
        }
      })
      .catch(() => setResult(null));
  }, [resultId]);

  if (!result) {
    return <div style={{ textAlign: 'center', marginTop: 80, fontSize: 20 }}>결과를 불러올 수 없습니다.</div>;
  }

  const { details = [], score, name, subject, solveTime, difficulty } = result;

  const handleProblemClick = (problemId) => {
    setSelectedProblemId(problemId);
    setAiFeedback('');
  };

  const handleGenerateFeedback = async () => {
    if (!selectedProblemId) return;
    setLoadingFeedback(true);
    setAiFeedback('');
    const detail = details.find(d => d.problemId === selectedProblemId);
    const problem = problemMap[selectedProblemId];
    let base64Image = '';
    if (problem?.imagePath) {
      try {
        const imageUrl = getImageUrl(problem.imagePath);
        base64Image = await getBase64FromUrl(imageUrl);
      } catch (e) {
        base64Image = '';
      }
    }

    // **여기서 실제로 넘기는 값 확인**
    console.log({
      question: problem?.content,
      userAnswer: detail?.userAnswer,
      correctAnswer: detail?.correctAnswer,
      explanation: problem?.explanation,
      imageUrl: problem?.imagePath,
      base64Image,
    });

    try {
      const response = await axios.post('http://localhost:8080/api/ai-feedback', {
        question: problem?.content,
        userAnswer: detail?.userAnswer,
        correctAnswer: detail?.correctAnswer,
        explanation: problem?.explanation,
        imageUrl: problem?.imagePath,
        base64Image,
      });
      setAiFeedback(response.data);
    } catch (e) {
      setAiFeedback('AI 해설 생성 실패');
    } finally {
      setLoadingFeedback(false);
    }
  };

  // Base64 변환 함수
  async function getBase64FromUrl(imageUrl) {
    const response = await fetch(imageUrl);
    const blob = await response.blob();
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => resolve(reader.result.split(',')[1]);
      reader.onerror = reject;
      reader.readAsDataURL(blob);
    });
  }

  return (
    <div style={{ background: '#f7f8fa', minHeight: '100vh', padding: 0 }}>
      <div style={{ maxWidth: 900, margin: '0 auto', padding: '40px 0' }}>
        <h2 style={{ fontWeight: 700, fontSize: 28, marginBottom: 24 }}>{name} ({subject})</h2>
        <div
          style={{
            background: 'linear-gradient(120deg, #f8fafc 0%, #e3f2fd 100%)',
            borderRadius: 24,
            boxShadow: '0 6px 32px #1976d222, 0 1.5px 0 #fff inset',
            padding: '56px 0 44px 0',
            marginBottom: 40,
            border: '2.5px solid #e3f2fd',
            textAlign: 'center',
            position: 'relative',
            maxWidth: 520,
            marginLeft: 'auto',
            marginRight: 'auto',
            transition: 'all 0.2s'
          }}
        >
          <div style={{ fontSize: 22, color: '#888', fontWeight: 600, marginBottom: 18, letterSpacing: 1 }}>
            🏆 최종 점수
          </div>
          <div
            style={{
              fontSize: 96,
              fontWeight: 900,
              color: '#1976d2',
              textShadow: '0 4px 24px #1976d244, 0 1.5px 0 #fff',
              marginBottom: 10,
              letterSpacing: 2,
              lineHeight: 1.1,
              transition: 'all 0.2s'
            }}
          >
            {score}
          </div>
          <div style={{ fontSize: 22, color: '#1976d2', fontWeight: 700, letterSpacing: 1 }}>
            점
          </div>
        </div>
        <div style={{ background: '#fff', borderRadius: 16, boxShadow: '0 4px 24px #0001', padding: 32 }}>
          <h3 style={{ fontWeight: 600, fontSize: 20, marginBottom: 24 }}>문제별 정답</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
            {details.map((detail, index) => {
              const isCorrect = detail.correct;
              const problem = problemMap[Number(detail.problemId)];
              const isSelected = selectedProblemId === Number(detail.problemId);
              const hasPassage = problem && problem.passage && problem.passage.content;
              return (
                <div
                  key={index}
                  style={{
                    padding: 20,
                    borderRadius: 16,
                    background: isCorrect
                      ? 'linear-gradient(90deg, #e8f5e9 0%, #f1f8e9 100%)'
                      : 'linear-gradient(90deg, #ffeaea 0%, #ffd6d6 100%)',
                    border: `2px solid ${isCorrect ? '#b2dfdb' : '#ffbdbd'}`,
                    boxShadow: isCorrect
                      ? '0 2px 12px #b2dfdb33'
                      : '0 2px 12px #ffbdbd33',
                    cursor: problem && problem.imagePath ? 'pointer' : 'default',
                    marginBottom: 18,
                    transition: 'all 0.2s',
                    position: 'relative',
                  }}
                  onClick={() => setSelectedProblemId(Number(detail.problemId))}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                    <div style={{ fontWeight: 600, fontSize: 18 }}>문제 {index + 1}</div>
                    <div style={{ fontWeight: 600, color: isCorrect ? '#2e7d32' : '#c62828' }}>
                      {isCorrect ? '정답' : '오답'}
                    </div>
                  </div>
                  {hasPassage && (
                    <div style={{ marginBottom: 10 }}>
                      <button
                        onClick={e => {
                          e.stopPropagation();
                          setOpenPassageIndex(openPassageIndex === index ? null : index);
                        }}
                        style={{
                          background: openPassageIndex === index
                            ? 'linear-gradient(90deg, #e3f2fd 0%, #bbdefb 100%)'
                            : 'linear-gradient(90deg, #fff 0%, #e3f2fd 100%)',
                          color: '#1976d2',
                          border: '1.5px solid #90caf9',
                          borderRadius: 18,
                          padding: '8px 22px',
                          fontWeight: 700,
                          fontSize: 16,
                          cursor: 'pointer',
                          marginBottom: 6,
                          marginRight: 8,
                          boxShadow: openPassageIndex === index
                            ? '0 2px 8px #90caf944'
                            : '0 1px 2px #e3f2fd44',
                          transition: 'all 0.2s',
                          display: 'inline-flex',
                          alignItems: 'center',
                          gap: 8,
                        }}
                      >
                        <span style={{ fontSize: 20, marginRight: 4 }}>📖</span>
                        {openPassageIndex === index ? '지문 닫기' : '지문 보기'}
                      </button>
                      <div
                        style={{
                          opacity: openPassageIndex === index ? 1 : 0,
                          background: 'linear-gradient(90deg, #e3f2fd 0%, #f5fafd 100%)',
                          borderRadius: 14,
                          padding: openPassageIndex === index ? '20px 22px' : '0 22px',
                          marginTop: openPassageIndex === index ? 10 : 0,
                          color: '#222',
                          fontSize: 16,
                          lineHeight: 1.8,
                          boxShadow: openPassageIndex === index ? '0 2px 12px #90caf944' : 'none',
                          whiteSpace: 'pre-line',
                          transition: 'all 0.35s cubic-bezier(.4,2,.6,1)',
                          maxHeight: openPassageIndex === index ? '1000px' : '0',
                          overflow: openPassageIndex === index ? 'visible' : 'hidden',
                        }}
                      >
                        {openPassageIndex === index && problem.passage.content}
                      </div>
                    </div>
                  )}
                  <div style={{ marginBottom: 12 }}>
                    {problem ? problem.content : '문제 내용을 불러오는 중...'}
                  </div>
                  {problem?.imagePath && (
                    <div style={{ textAlign: 'center', margin: '16px 0' }}>
                      <img
                        src={
                          problem.imagePath.startsWith('http')
                            ? problem.imagePath
                            : `http://localhost:8080/${problem.imagePath}`
                        }
                        alt="문제 이미지"
                        style={{
                          maxWidth: '95%',
                          maxHeight: 600,
                          width: 'auto',
                          height: 'auto',
                          borderRadius: 8,
                          boxShadow: '0 2px 8px #0001',
                          marginBottom: 8,
                          display: 'block',
                          marginLeft: 'auto',
                          marginRight: 'auto',
                        }}
                        onError={e => { e.target.style.display = 'none'; }}
                      />
                    </div>
                  )}
                  <div style={{ color: '#666' }}>
                    <div>제출한 답: {detail.userAnswer || '미제출'}</div>
                    <div>정답: {detail.correctAnswer}</div>
                  </div>
                  <div style={{ marginTop: 16, textAlign: 'center' }}>
                    <button
                      onClick={e => {
                        e.stopPropagation();
                        setSelectedProblemId(Number(detail.problemId));
                        handleGenerateFeedback();
                      }}
                      disabled={loadingFeedback && isSelected}
                      style={{
                        background: 'linear-gradient(90deg, #ff9800 0%, #ffb300 100%)',
                        color: '#fff',
                        border: '1.5px solid #fffbe6',
                        borderRadius: 24,
                        padding: '14px 38px',
                        fontWeight: 800,
                        fontSize: 19,
                        boxShadow: '0 4px 24px #ff980055, 0 1.5px 0 #fffbe6 inset',
                        cursor: loadingFeedback && isSelected ? 'not-allowed' : 'pointer',
                        outline: 'none',
                        transition: 'all 0.2s',
                        margin: '0 auto',
                        display: 'inline-block',
                        letterSpacing: 1,
                        opacity: loadingFeedback && isSelected ? 0.7 : 1,
                        position: 'relative',
                        textShadow: '0 1px 8px #ffb30055, 0 1px 0 #fffbe6',
                        filter: loadingFeedback && isSelected ? 'brightness(0.95)' : 'brightness(1.05)',
                      }}
                      onMouseOver={e => {
                        if (!(loadingFeedback && isSelected)) e.target.style.background = 'linear-gradient(90deg, #ffb300 0%, #ff9800 100%)';
                      }}
                      onMouseOut={e => {
                        if (!(loadingFeedback && isSelected)) e.target.style.background = 'linear-gradient(90deg, #ff9800 0%, #ffb300 100%)';
                      }}
                    >
                      <span style={{ display: 'inline-flex', alignItems: 'center', fontWeight: 800 }}>
                        <span style={{ fontSize: 22, marginRight: 10, lineHeight: 1 }}>🤖</span>
                        AI 해설 생성
                      </span>
                    </button>
                    {isSelected && aiFeedback && (
                      <div
                        style={{
                          marginTop: 18,
                          background: 'linear-gradient(90deg, #fffbe6 0%, #e3f2fd 100%)',
                          border: '1.5px solid #ffe082',
                          borderRadius: 14,
                          padding: '24px 28px',
                          color: '#222',
                          fontSize: 17,
                          fontWeight: 500,
                          boxShadow: '0 2px 12px #ffe08244',
                          position: 'relative',
                          textAlign: 'left',
                          lineHeight: 1.7,
                          minHeight: 60,
                          transition: 'all 0.2s'
                        }}
                      >
                        <div style={{
                          display: 'flex',
                          alignItems: 'center',
                          marginBottom: 10,
                          gap: 8
                        }}>
                          <span style={{
                            fontSize: 22,
                            color: '#ff9800',
                            marginRight: 6
                          }}>💡</span>
                          <span style={{
                            fontWeight: 700,
                            fontSize: 18,
                            color: '#ff9800'
                          }}>AI 피드백</span>
                        </div>
                        <div style={{
                          whiteSpace: 'pre-line',
                          wordBreak: 'keep-all',
                          fontSize: 17,
                          color: '#222'
                        }}>
                          {aiFeedback}
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
        <div style={{ marginTop: 32 }}>풀이시간: {solveTime ? `${Math.floor(solveTime / 60)}분 ${solveTime % 60}초` : '-'}</div>
        <button
          onClick={() => navigate('/ai-feedback')}
          style={{
            marginTop: 32,
            padding: '14px 0',
            background: '#1976d2',
            color: '#fff',
            border: 'none',
            borderRadius: 8,
            fontWeight: 600,
            fontSize: 16,
            width: '100%'
          }}
        >
          목록으로
        </button>
      </div>
    </div>
  );
};

export default AIFeedbackDetailPage;
