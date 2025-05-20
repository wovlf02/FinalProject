import React, { useState } from 'react';
import PostList from './components/PostList';
import PostModal from './components/PostModal';
import '../../css/Post.css';

// 카테고리 및 사이드바 더미 데이터
const categories = ['전체', '질문', '정보 공유', '스터디', '익명', '취업/진로', '프로젝트'];

const popularPosts = [
  { id: 1, title: '알고리즘 문제 풀이 방법 질문드립니다', author: '김코딩', likes: 5 },
  { id: 2, title: '프론트엔드 개발자 로드맵 공유합니다', author: '한개발자', likes: 24 },
  { id: 3, title: '알고리즘 스터디 모집합니다 (구 Z팀)', author: '스터디장', likes: 15 },
  { id: 4, title: '개발자 국밥 밥값 조언 부탁드립니다', author: '익명', likes: 35 },
  { id: 5, title: '유망한 개발 직군 (2025년 최신)', author: '개발도구마스터', likes: 42 },
];

const studyList = [
  {
    name: '알고리즘 스터디',
    color: '#e9d8fd',
    tag: '모집중',
    tagColor: '#a78bfa',
    info: '매주 월/금 20시 | 8명 활동',
  },
  {
    name: '프론트엔드 스터디',
    color: '#dbeafe',
    tag: '모집중',
    tagColor: '#3b82f6',
    info: '매주 토요일 16시 | 10명 활동',
  },
  {
    name: 'CS 기초 스터디',
    color: '#d1fae5',
    tag: '모집중',
    tagColor: '#10b981',
    info: '매주 수 14:00 | 10명 활동',
  },
];

const tags = [
  '알고리즘', '스터디', 'React', 'Vue', '프로젝트',
  '취업', '클라우드', '데이터', 'python', 'javascript', '공유', '팁', '영어'
];

const Post = () => {
  const [posts, setPosts] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('전체');
  const [showModal, setShowModal] = useState(false);

  // 기존 카테고리 필터링 로직 유지
  const filteredPosts =
    selectedCategory === '전체'
      ? posts
      : posts.filter((post) => post.category === selectedCategory);

  // 글쓰기 완료 시 기존 로직 유지
  const handleAddPost = (newPost) => {
    setPosts([newPost, ...posts]);
    setShowModal(false);
  };

  return (
    <div className="postpage-root">
      <div className="postpage-header">
        <div className="postpage-breadcrumb">커뮤니티 &gt; 게시판</div>
        <button className="postpage-write-btn" onClick={() => setShowModal(true)}>
          ✏️ 글쓰기
        </button>
      </div>
      <div className="postpage-main">
        {/* 왼쪽: 기존 게시판 */}
        <div className="postpage-left">
          <div className="postpage-tabs">
            {categories.map((cat) => (
              <button
                key={cat}
                className={`postpage-tab${selectedCategory === cat ? ' selected' : ''}`}
                onClick={() => setSelectedCategory(cat)}
              >
                {cat}
              </button>
            ))}
          </div>
          {/* 기존 PostList를 그대로 사용 */}
          <PostList posts={filteredPosts} setPosts={setPosts} />
        </div>
        {/* 오른쪽: 이미지 스타일의 사이드바 */}
        <div className="postpage-right">
          <div className="postpage-box">
            <div className="postpage-box-title"><span>🔥</span> 인기 게시글</div>
            <ul className="postpage-popular-list">
              {popularPosts.map((p) => (
                <li key={p.id}>
                  <div className="postpage-popular-title">{p.title}</div>
                  <div className="postpage-popular-meta">
                    <span>{p.author}</span>
                    <span>좋아요 {p.likes}</span>
                  </div>
                </li>
              ))}
            </ul>
          </div>
          <div className="postpage-box">
            <div className="postpage-box-title" style={{ color: '#7c3aed' }}>💡 진행 중인 스터디</div>
            <div className="postpage-study-list">
              {studyList.map((s, i) => (
                <div className="postpage-study-item" key={i} style={{ background: s.color }}>
                  <div className="postpage-study-name">{s.name}</div>
                  <span className="postpage-study-tag" style={{ background: s.tagColor }}>{s.tag}</span>
                  <div className="postpage-study-info">{s.info}</div>
                </div>
              ))}
            </div>
          </div>
          <div className="postpage-box">
            <div className="postpage-box-title">🏷️ 인기 태그</div>
            <div className="postpage-tag-list">
              {tags.map(tag => (
                <span className="postpage-tag" key={tag}>{tag}</span>
              ))}
            </div>
          </div>
        </div>
      </div>
      {/* 기존 글쓰기 모달 */}
      {showModal && (
        <PostModal
          onClose={() => setShowModal(false)}
          onAddPost={handleAddPost}
        />
      )}
    </div>
  );
};

export default Post;
