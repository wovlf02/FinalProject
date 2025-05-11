import React, { useState } from 'react';
import CommentSection from './CommentSection';

const PostItem = ({ post, onLike, onAddComment }) => {
  const [comment, setComment] = useState('');

  const handleCommentSubmit = (e) => {
    e.preventDefault();
    if (!comment.trim()) return;
    onAddComment(post.id, comment);
    setComment('');
  };

  return (
    <div className="community-post-card">
      <div className="community-post-title">{post.title}</div>
      <div className="community-post-content">{post.content}</div>
      <div className="community-post-meta">
        <span className="community-post-category">{post.category}</span>
        <span>{post.author}</span>
        <span>{new Date(post.date).toLocaleString()}</span>
      </div>
      <div className="community-post-stats">
        <button onClick={() => onLike(post.id)}>🤍 {post.likes}</button>
      </div>
      <CommentSection
        comments={post.comments}
        comment={comment}
        setComment={setComment}
        onSubmit={handleCommentSubmit}
      />
    </div>
  );
};

export default PostItem;
