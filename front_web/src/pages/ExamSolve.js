import React, { useState, useEffect } from 'react';

const ExamSolve = ({
  question,
  currentQuestion,
  totalQuestions,
  answers,
  setAnswers,
  setCurrentQuestion,
  onFinish,
  initialTime,
  difficulty,
  subject,
  unitTitle = '',
}) => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [memo, setMemo] = useState('');
  const [timeLeft, setTimeLeft] = useState(initialTime);
  const [aiFeedback, setAiFeedback] = useState('');
  const [loadingFeedback, setLoadingFeedback] = useState(false);

  const answer = question.answer || '';
  const isMultiple = answer.trim().endsWith('번');
  const isSubjective = !isMultiple;

  const correctRate = question.correctRate ?? question.correct_rate;

  useEffect(() => {
    if (!question) return;
    if (timeLeft <= 0) {
      handleSubmit();
      return;
    }
    const timer = setInterval(() => setTimeLeft(t => t - 1), 1000);
    return () => clearInterval(timer);
    // eslint-disable-next-line
  }, [question, timeLeft]);

  const handleAnswer = (answer) => {
    const newAnswers = [...answers];
    newAnswers[currentQuestion] = answer;
    setAnswers(newAnswers);
  };

  const handleSubmit = async () => {
    if (isSubmitting) return;
    setIsSubmitting(true);

    // AI 피드백 요청
    setLoadingFeedback(true);
    try {
      const res = await fetch('/api/ai-feedback', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          question: question.content,
          userAnswer: answers[currentQuestion],
          correctAnswer: question.answer,
          explanation: question.explanation,
        }),
      });
      const data = await res.json();
      setAiFeedback(data.feedback);
    } catch (e) {
      setAiFeedback('AI 피드백을 불러오지 못했습니다.');
    }
    setLoadingFeedback(false);

    onFinish(answers, timeLeft);
  };

  if (!question) {
    return <div>문제가 없습니다.</div>;
  }

  const formatTime = (seconds) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  // 진행 점바
  const renderProgressDots = () => {
    const dots = [];
    for (let i = 0; i < Math.min(totalQuestions, 6); i++) {
      dots.push(
        <span
          key={i}
          style={{
            display: 'inline-block',
            width: 10,
            height: 10,
            borderRadius: '50%',
            background: i === currentQuestion ? '#1976d2' : '#e0e0e0',
            margin: '0 4px',
            transition: 'background 0.2s',
          }}
        />
      );
    }
    return dots;
  };

  // 하단 진행률 바
  const progressPercent = Math.round(((currentQuestion + 1) / totalQuestions) * 100);

  function extractNumber(str) {
    if (!str) return '';
    const match = str.match(/[1-5]/);
    return match ? match[0] : str;
  }

  const isCorrect = extractNumber(answers[currentQuestion]) === extractNumber(question.answer);

  console.log('문제 정답률:', question.correct_rate, typeof question.correct_rate);
  console.log('문제 객체:', question);

  return (
    <div style={{ background: '#fafbfc', minHeight: '100vh', padding: 0, fontFamily: 'inherit' }}>
      <div style={{ maxWidth: 700, margin: '0 auto', padding: '32px 0 0 0' }}>
        {/* 상단 정보 */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
          <div style={{ display: 'flex', alignItems: 'center', fontWeight: 700, fontSize: 22 }}>
            <span>{subject} - {unitTitle}</span>
            {question.source && (
              <span style={{ marginLeft: 10, color: '#888', fontSize: 16, fontWeight: 500 }}>
                [{question.source}]
              </span>
            )}
            {correctRate !== undefined && correctRate !== null && correctRate !== '' && !isNaN(Number(correctRate)) && (
              <span style={{ marginLeft: 10, color: '#888', fontSize: 16, fontWeight: 500 }}>
                | 정답률: {Math.round(Number(correctRate))}%
              </span>
            )}
          </div>
          <div style={{ fontWeight: 600, fontSize: 18 }}>{formatTime(timeLeft)}</div>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: 12 }}>
          <div style={{ fontSize: 17, color: '#222', fontWeight: 500 }}>문제 {currentQuestion + 1}/{totalQuestions}</div>
          <div style={{ flex: 1 }} />
          <div>{renderProgressDots()}</div>
        </div>
        {/* 문제 지문 */}
        {question.passage && question.passage.content && (
          <div style={{
            background: '#f5f5f5',
            borderRadius: 8,
            padding: 18,
            marginBottom: 18,
            color: '#333',
            fontSize: 17,
            lineHeight: 1.7
          }}>
            {question.passage.content}
          </div>
        )}
        <div style={{ fontSize: 22, fontWeight: 600, marginBottom: 28 }}>{question.content}</div>
        {/* 문제 카드 */}
        <div style={{ background: '#fff', borderRadius: 18, boxShadow: '0 2px 16px #0002', padding: 40, marginBottom: 28 }}>
          {/* 문제 이미지 */}
          {question.imagePath && (() => {
            console.log('문제 이미지 경로:', question.imagePath);
            let imageUrl = '';
            if (question.imagePath.startsWith('http')) {
              imageUrl = question.imagePath;
            } else {
              imageUrl = `http://localhost:8080/${question.imagePath}`;
            }
            return (
              <div style={{ textAlign: 'center', marginBottom: 24 }}>
                <img
                  src={imageUrl}
                  alt="문제 이미지"
                  style={{ maxWidth: '100%', maxHeight: 320, borderRadius: 10, boxShadow: '0 2px 12px #0002' }}
                  onError={e => { e.target.style.display = 'none'; }}
                />
              </div>
            );
          })()}
          {(() => {
            let options = question.options;
            if (isMultiple && !Array.isArray(options)) {
              // 항상 1~5번 보기
              options = ['① 1', '② 2', '③ 3', '④ 4', '⑤ 5'];
            }
            if (isMultiple) {
              return (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 16, marginBottom: 24 }}>
                  {options.map((option, index) => {
                    // 보기 번호(①~⑤)와 텍스트 분리
                    const number = ['①', '②', '③', '④', '⑤'][index] || `${index + 1}`;
                    let text = option.replace(/^①|②|③|④|⑤|[1-5]\.?\s*/, '').trim();
                    // 기본 1~5 숫자만 있을 경우 텍스트를 빈 문자열로
                    if (['1','2','3','4','5'].includes(text)) text = '';
                    return (
                      <label key={index} style={{
                        display: 'flex', alignItems: 'center',
                        background: answers[currentQuestion] === option ? '#e3f2fd' : '#fafbfc',
                        border: answers[currentQuestion] === option ? '2.5px solid #1976d2' : '1.5px solid #ddd',
                        borderRadius: 12,
                        padding: '18px 24px',
                        fontWeight: 500,
                        fontSize: 18,
                        cursor: 'pointer',
                        marginBottom: 2,
                        transition: 'all 0.2s',
                        minHeight: 48,
                        boxShadow: answers[currentQuestion] === option ? '0 2px 8px #1976d233' : 'none',
                      }}>
                        <input
                          type="radio"
                          name={`option-${currentQuestion}`}
                          checked={answers[currentQuestion] === option}
                          onChange={() => handleAnswer(option)}
                          style={{ marginRight: 18, accentColor: '#1976d2', width: 22, height: 22 }}
                        />
                        <span style={{ fontWeight: 700, fontSize: 22, width: 32, textAlign: 'center', color: '#1976d2', flexShrink: 0 }}>{number}</span>
                        {text && <span style={{ fontSize: 18, color: '#222', marginLeft: 12 }}>{text}</span>}
                      </label>
                    );
                  })}
                </div>
              );
            } else if (isSubjective) {
              return (
                <div style={{ marginBottom: 24 }}>
                  <input
                    type="text"
                    value={answers[currentQuestion] || ''}
                    onChange={e => handleAnswer(e.target.value)}
                    placeholder="정답을 입력하세요"
                    style={{
                      width: '100%',
                      padding: 18,
                      border: '2px solid #1976d2',
                      borderRadius: 10,
                      fontSize: 18,
                      fontFamily: 'inherit',
                      marginBottom: 2
                    }}
                  />
                </div>
              );
            } else {
              return <div style={{ color: '#888', fontSize: 15, marginBottom: 18 }}>정답 입력란이 없습니다.</div>;
            }
          })()}
          {/* 연습공간 */}
          <div style={{ marginTop: 24 }}>
            <div style={{ fontSize: 15, color: '#888', marginBottom: 8 }}>연습공간</div>
            <textarea
              value={memo}
              onChange={e => setMemo(e.target.value)}
              placeholder="여기에 계산이나 메모를 할 수 있습니다."
              style={{
                width: '100%',
                minHeight: 100,
                padding: 16,
                border: '1.5px solid #ccc',
                borderRadius: 10,
                fontSize: 16,
                background: '#fafbfc',
                resize: 'vertical',
                fontFamily: 'inherit',
              }}
            />
          </div>
        </div>
        {/* 하단 버튼/진행률 */}
        <div style={{ display: 'flex', gap: 10, marginBottom: 10 }}>
          <button
            onClick={() => setCurrentQuestion(prev => Math.max(0, prev - 1))}
            disabled={currentQuestion === 0}
            style={{
              flex: 1,
              padding: '14px 0',
              background: '#fff',
              color: '#1976d2',
              border: '1.5px solid #1976d2',
              borderRadius: 8,
              fontWeight: 600,
              fontSize: 16,
              opacity: currentQuestion === 0 ? 0.5 : 1,
              cursor: currentQuestion === 0 ? 'not-allowed' : 'pointer',
              transition: 'all 0.2s'
            }}
          >
            이전
          </button>
          {currentQuestion < totalQuestions - 1 ? (
            <button
              onClick={() => setCurrentQuestion(prev => prev + 1)}
              style={{
                flex: 1,
                padding: '14px 0',
                background: '#111',
                color: '#fff',
                border: 'none',
                borderRadius: 8,
                fontWeight: 600,
                fontSize: 16,
                transition: 'all 0.2s'
              }}
            >
              다음
            </button>
          ) : (
            <button
              onClick={handleSubmit}
              disabled={isSubmitting}
              style={{
                flex: 1,
                padding: '14px 0',
                background: '#111',
                color: '#fff',
                border: 'none',
                borderRadius: 8,
                fontWeight: 600,
                fontSize: 16,
                opacity: isSubmitting ? 0.7 : 1,
                transition: 'all 0.2s'
              }}
            >
              제출하기
            </button>
          )}
        </div>
        {/* 하단 진행률 바 */}
        <div style={{ width: '100%', height: 7, background: '#e0e0e0', borderRadius: 5, marginBottom: 18 }}>
          <div style={{ width: `${progressPercent}%`, height: '100%', background: '#1976d2', borderRadius: 5, transition: 'width 0.2s' }} />
        </div>
        {/* AI 피드백 영역 */}
        {isSubmitting && (
          <div style={{
            background: '#f3f7fa',
            borderRadius: 10,
            padding: 18,
            marginTop: 18,
            color: '#1976d2',
            fontSize: 17,
            fontWeight: 500,
            boxShadow: '0 1px 6px #0001'
          }}>
            {loadingFeedback
              ? "AI 피드백 생성 중..."
              : aiFeedback || "AI 피드백이 없습니다."}
          </div>
        )}
      </div>
    </div>
  );
};

export default ExamSolve;
