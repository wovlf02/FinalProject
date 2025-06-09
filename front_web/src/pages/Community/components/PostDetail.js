import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../../../css/PostDetail.css';
import api from '../../../api/api'; // ✅ axios 인스턴스 (withCredentials 포함)

const PostDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [post, setPost] = useState(null);
    const [comments, setComments] = useState([]);
    const [comment, setComment] = useState('');
    const [liked, setLiked] = useState(false);

    // ✅ 게시글 상세 조회
    const fetchPostDetail = async () => {
        try {
            const res = await api.post('/community/posts/detail', { post_id: id });
            setPost(res.data);
            console.log(res.data);
        } catch (err) {
            console.error('게시글 불러오기 실패:', err);
        }
    };

    // ✅ 댓글 목록 조회
    const fetchComments = async () => {
        try {
            const res = await api.post('/community/comments/by-post', { post_id: id });
            setComments(res.data.data.comments || []);
            console.log("댓글 목록", res.data.data.comments);
        } catch (err) {
            console.error('댓글 목록 조회 실패:', err);
        }
    };

    // ✅ 좋아요 상태 조회
    const checkLikeStatus = async () => {
        try {
            const res = await api.post('/community/likes/posts/check', { post_id: id });
            setLiked(res.data.liked);
        } catch (err) {
            console.error('좋아요 상태 조회 실패:', err);
        }
    };

    // ✅ 좋아요 토글
    const handleLike = async () => {
        try {
            const res = await api.post('/community/likes/posts/toggle', { post_id: id });
            setLiked(res.data.data);
            // 상태 동기화
            fetchPostDetail();
        } catch (err) {
            console.error('좋아요 토글 실패:', err);
        }
    };

    // ✅ 댓글 등록
    const handleAddComment = async (e) => {
        e.preventDefault();
        if (!comment.trim()) return;

        try {
            await api.post('/community/comments/create', {
                post_id: id,
                content: comment
            });
            setComment('');
            fetchComments();
        } catch (err) {
            console.error('댓글 등록 실패:', err);
        }
    };

    useEffect(() => {
        fetchPostDetail();
        fetchComments();
        checkLikeStatus();
    }, [id]);

    if (!post) {
        return <div style={{ padding: 40, textAlign: 'center' }}>게시글을 찾을 수 없습니다.</div>;
    }

    return (
        <div className="post-detail-page">
            <button className="post-detail-back" onClick={() => navigate(-1)}>← 목록으로</button>
            <div className="post-detail-title">{post.title}</div>
            <div className="post-detail-meta">
                <span>{post.writer_nickname || '익명'}</span>
                <span>{post.created_at?.slice(0, 10)}</span>
                <span>조회 {post.view_count}</span>
                <span>
          <button className="like-btn" onClick={handleLike}>
            {liked ? '❤️' : '🤍'} {post.like_count}
          </button>
        </span>
            </div>
            <div className="post-detail-content">{post.content}</div>

            <div className="post-detail-comments">
                <div className="post-detail-comments-title">댓글</div>
                {comments.length > 0 ? (
                    <ul>
                        {comments.map(c => (
                            <li key={c.commentId}>
                                <span className="comment-author">{c.writer_nickname || '익명'}</span>
                                <span className="comment-text">{c.content}</span>
                                <span className="comment-date">{c.created_at?.slice(0, 16).replace('T', ' ')}</span>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <div style={{ color: '#aaa', marginBottom: 8 }}>아직 댓글이 없습니다.</div>
                )}
                <form className="comment-form" onSubmit={handleAddComment}>
                    <input
                        type="text"
                        value={comment}
                        onChange={e => setComment(e.target.value)}
                        placeholder="댓글을 입력하세요"
                        className="comment-input"
                    />
                    <button type="submit" className="comment-submit">등록</button>
                </form>
            </div>
        </div>
    );
};

export default PostDetail;
