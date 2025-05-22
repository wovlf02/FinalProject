import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../../../css/PostTable.css';

const PostList = ({ posts, setPosts }) => {
  const navigate = useNavigate();

  return (
    <div className="board-table-wrap">
      <table className="board-table">
        <thead>
          <tr>
            <th style={{width: "40%"}}>제목</th>
            <th style={{width: "15%"}}>작성자</th>
            <th style={{width: "15%"}}>날짜</th>
            <th style={{width: "15%"}}>조회</th>
            <th style={{width: "15%"}}>좋아요</th>
          </tr>
        </thead>
        <tbody>
          {posts.length === 0 ? (
            <tr>
              <td colSpan={5} style={{textAlign: 'center', color: '#888'}}>게시글이 없습니다.</td>
            </tr>
          ) : (
            posts.map((post) => (
              <tr key={post.id}>
                <td className="board-title-cell">
                  <span className={`board-badge board-badge-${post.category}`}>{post.category}</span>
                  <span
                    className="board-title-text"
                    style={{ cursor: 'pointer', textDecoration: 'underline' }}
                    onClick={() => navigate(`/community/post/${post.id}`)}
                  >
                    {post.title}
                  </span>
                  {post.likes >= 20 && <span className="board-hot">HOT</span>}
                  {post.comments && post.comments.length > 0 && (
                    <span className="board-comment-count">[{post.comments.length}]</span>
                  )}
                </td>
                <td>{post.author}</td>
                <td>{post.date ? post.date.slice(0, 10) : ''}</td>
                <td>{post.views || 0}</td>
                <td>{post.likes}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default PostList;
