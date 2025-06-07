// src/pages/Community/PostToCommunity.js
import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import api from '../../api/api';
import '../../css/Community.css';

const PostToCommunity = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { questionData } = location.state || {};

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');

    // 실패한 문제 정보로 자동 채움
    useEffect(() => {
        if (questionData) {
            setTitle(`[문제 ${questionData.questionId}] ${questionData.title}`);
            setContent(`문제 설명:\n${questionData.description || '설명이 없습니다.'}\n\n[내 풀이 실패 이유 작성]`);
        }
    }, [questionData]);

    const handlePost = async () => {
        if (!title || !content) {
            alert('제목과 내용을 입력해주세요.');
            return;
        }

        try {
            const res = await api.post('/community/post', {
                title,
                content,
                tags: ['질문', '문제풀이'], // 선택적 확장
            });

            alert('게시글이 등록되었습니다.');
            navigate('/community');
        } catch (err) {
            console.error('게시글 업로드 실패:', err);
            alert('게시글 등록에 실패했습니다.');
        }
    };

    return (
        <div className="post-community-container">
            <h2>문제 질문 게시글 작성</h2>

            <label>제목</label>
            <input
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="제목을 입력하세요"
            />

            <label>내용</label>
            <textarea
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="질문 내용을 작성하세요"
                rows={12}
            />

            <button onClick={handlePost}>업로드</button>
        </div>
    );
};

export default PostToCommunity;
