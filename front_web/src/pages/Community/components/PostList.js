import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../../../css/PostTable.css';

// ✅ 카테고리 영문 → 한글 매핑 (UI용)
const CATEGORY_KOR = {
    QUESTION: '질문',
    INFO: '정보 공유',
    STUDY: '스터디',
    ANONYMOUS: '익명',
    GENERAL: '일반',
    NOTICE: '공지사항'
};

/**
 * @param posts : Post.jsx에서 전달받은 게시글 목록
 */
const PostList = ({ posts = [] }) => {
    const navigate = useNavigate();

    return (
        <div className="board-table-wrap">
            <table className="board-table">
                <thead>
                <tr>
                    <th style={{ width: '40%' }}>제목</th>
                    <th style={{ width: '15%' }}>작성자</th>
                    <th style={{ width: '15%' }}>날짜</th>
                    <th style={{ width: '15%' }}>조회</th>
                    <th style={{ width: '15%' }}>좋아요</th>
                </tr>
                </thead>
                <tbody>
                {posts.length === 0 ? (
                    <tr>
                        <td colSpan={5} style={{ textAlign: 'center', color: '#888' }}>
                            게시글이 없습니다.
                        </td>
                    </tr>
                ) : (
                    posts.map((post) => (
                        <tr key={post.postId}>
                            <td className="board-title-cell">
                                    <span className={`board-badge board-badge-${post.category}`}>
                                        {CATEGORY_KOR[post.category] || post.category}
                                    </span>
                                <span
                                    className="board-title-text"
                                    style={{ cursor: 'pointer', textDecoration: 'underline' }}
                                    onClick={() => navigate(`/community/post/${post.postId}`)}
                                >
                                        {post.title}
                                    </span>
                                {post.likeCount >= 20 && <span className="board-hot">HOT</span>}
                                {post.commentCount > 0 && (
                                    <span className="board-comment-count">[{post.commentCount}]</span>
                                )}
                            </td>
                            <td>{post.author}</td>
                            <td>{post.createdAt?.slice(0, 10)}</td>
                            <td>{post.viewCount}</td>
                            <td>{post.likeCount}</td>
                        </tr>
                    ))
                )}
                </tbody>
            </table>
        </div>
    );
};

export default PostList;
