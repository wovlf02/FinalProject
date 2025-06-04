import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

const ExamResultDetail = () => {
  const location = useLocation();
  const [question, setQuestion] = useState(null);
  const [aiFeedback, setAiFeedback] = useState(null);
  const [loading, setLoading] = useState(false);

  // AI 피드백 가져오기 함수
  const getAiFeedback = async (question, answer, explanation) => {
    try {
      setLoading(true);
      const response = await axios.post('http://localhost:8080/api/ai-feedback', {
        question,
        answer,
        explanation
      });
      setAiFeedback(response.data);
    } catch (error) {
      console.error('AI 피드백을 가져오는데 실패했습니다:', error);
      setAiFeedback('AI 피드백을 가져오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 문제 정보 로드 시 AI 피드백 요청
  useEffect(() => {
    if (question) {
      getAiFeedback(
        question.content,
        question.userAnswer,
        question.explanation
      );
    }
  }, [question]);

  return (
    <div style={{ padding: '20px' }}>
      {/* ... existing question display code ... */}

      {/* AI 피드백 섹션 */}
      <div style={{ 
        marginTop: '20px', 
        padding: '20px',
        backgroundColor: '#f8f9fa',
        borderRadius: '8px',
        border: '1px solid #dee2e6'
      }}>
        <h3 style={{ marginBottom: '15px' }}>AI 피드백</h3>
        {loading ? (
          <div>AI 피드백을 생성중입니다...</div>
        ) : aiFeedback ? (
          <div style={{ 
            whiteSpace: 'pre-wrap',
            lineHeight: '1.6',
            color: '#495057'
          }}>
            {aiFeedback}
          </div>
        ) : (
          <div>AI 피드백을 불러오는 중입니다...</div>
        )}
      </div>

      {/* ... existing code ... */}
    </div>
  );
};

export default ExamResultDetail; 