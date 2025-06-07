import React, { useEffect, useState } from 'react';
import api from '../../../api/api';

const CommentSection = ({ postId }) => {
    const [comment, setComment] = useState('');
    const [comments, setComments] = useState([]);

    // ✅ 댓글 목록 조회
    const fetchComments = async () => {
        try {
            const res = await api.post('/community/comments/by-post', { postId });
            setComments(res.data.data || []);
        } catch (err) {
            console.error('댓글 조회 실패:', err);
        }
    };

    // ✅ 댓글 등록
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!comment.trim()) return;

        try {
            await api.post('/community/comments/create', {
                postId,
                content: comment
            });
            setComment('');
            fetchComments(); // 등록 후 새로고침
        } catch (err) {
            console.error('댓글 등록 실패:', err);
        }
    };

    useEffect(() => {
        fetchComments();
    }, [postId]);

    return (
        <div className="community-comment-section">
            <form onSubmit={handleSubmit} className="community-comment-form">
                <input
                    type="text"
                    placeholder="댓글을 입력하세요"
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                />
                <button type="submit">등록</button>
            </form>
            <div className="community-comments-list">
                {comments.map((c) => (
                    <div key={c.commentId} className="community-comment">
            <span className="community-comment-meta">
              {c.authorNickname || '익명'} | {new Date(c.createdAt).toLocaleString()}
            </span>
                        <div>{c.content}</div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default CommentSection;

