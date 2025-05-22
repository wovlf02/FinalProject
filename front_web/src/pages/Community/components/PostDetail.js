import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../../../css/PostDetail.css'; // ← community/components 기준 경로

const PostDetail = ({ posts, setPosts }) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const post = posts.find(p => String(p.id) === String(id));
  const [comment, setComment] = useState('');

  if (!post) {
    return <div style={{ padding: 40, textAlign: 'center' }}>게시글을 찾을 수 없습니다.</div>;
  }

  // 좋아요 기능
  const handleLike = () => {
    setPosts(posts.map(p =>
      p.id === post.id ? { ...p, likes: p.likes + 1 } : p
    ));
  };

  // 댓글 등록
  const handleAddComment = (e) => {
    e.preventDefault();
    if (!comment.trim()) return;
    setPosts(posts.map(p =>
      p.id === post.id
        ? {
            ...p,
            comments: [
              ...p.comments,
              {
                id: Date.now(),
                text: comment,
                author: '익명',
                date: new Date().toISOString(),
              },
            ],
          }
        : p
    ));
    setComment('');
  };

  return (
    <div className="post-detail-page">
      <button className="post-detail-back" onClick={() => navigate(-1)}>
        ← 목록으로
      </button>
      <div className="post-detail-title">{post.title}</div>
      <div className="post-detail-meta">
        <span>{post.author}</span>
        <span>{post.date ? post.date.slice(0, 10) : ''}</span>
        <span>조회 {post.views || 0}</span>
        <span>
          <button className="like-btn" onClick={handleLike}>
            🤍 {post.likes}
          </button>
        </span>
      </div>
      <div className="post-detail-content">{post.content}</div>
      <div className="post-detail-comments">
        <div className="post-detail-comments-title">댓글</div>
        {post.comments && post.comments.length > 0 ? (
          <ul>
            {post.comments.map(c => (
              <li key={c.id}>
                <span className="comment-author">{c.author}</span>
                <span className="comment-text">{c.text}</span>
                <span className="comment-date">{c.date.slice(0, 16).replace('T', ' ')}</span>
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
