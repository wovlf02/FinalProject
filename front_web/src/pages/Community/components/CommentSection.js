import React from 'react';

const CommentSection = ({comments, comment, setComment, onSubmit}) => (
    <div className="community-comment-section">
        <form onSubmit={onSubmit} className="community-comment-form">
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
                <div key={c.id} className="community-comment">
          <span className="community-comment-meta">
            {c.author} | {new Date(c.date).toLocaleString()}
          </span>
                    <div>{c.text}</div>
                </div>
            ))}
        </div>
    </div>
);

export default CommentSection;
