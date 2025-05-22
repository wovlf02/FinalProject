import React, { useState } from 'react';
import PostList from './components/PostList';
import '../../css/Post.css';
import { useNavigate } from 'react-router-dom';

// 카테고리 및 사이드바 더미 데이터
const categories = ['전체', '질문', '정보공유', '스터디', '익명', '취업/진로', '프로젝트'];

// 전체 스터디 리스트 예시 (실제 서비스라면 서버에서 받아옴)
export const allStudyList = [
  { id: 1, name: '알고리즘 스터디', color: '#e9d8fd', tag: '모집중', tagColor: '#a78bfa', info: '매주 월/금 20시 | 8명 활동', status: '모집중' },
  { id: 2, name: '프론트엔드 스터디', color: '#dbeafe', tag: '모집중', tagColor: '#3b82f6', info: '매주 토요일 16시 | 10명 활동', status: '모집중' },
  { id: 3, name: 'CS 기초 스터디', color: '#d1fae5', tag: '모집중', tagColor: '#10b981', info: '매주 수 14:00 | 10명 활동', status: '모집중' },
  { id: 4, name: 'AI 딥러닝 스터디', color: '#fee2e2', tag: '마감', tagColor: '#ef4444', info: '매주 일 13:00 | 7명 활동', status: '마감' },
];

// 검색 옵션
const searchOptions = [
  { value: 'title', label: '제목' },
  { value: 'content', label: '내용' },
  { value: 'title_content', label: '제목+내용' },
  { value: 'author', label: '작성자' },
];

const POSTS_PER_PAGE = 10;

// 인기 태그 동적 추출 함수
function getPopularTags(posts, topN = 10) {
  const tagCount = {};
  posts.forEach(post => {
    if (!post.tag) return;
    post.tag.split(',').map(t => t.trim()).forEach(tag => {
      if (!tag) return;
      tagCount[tag] = (tagCount[tag] || 0) + 1;
    });
  });
  return Object.entries(tagCount)
    .sort((a, b) => b[1] - a[1])
    .slice(0, topN)
    .map(([tag]) => tag);
}

const Post = ({ posts, setPosts }) => {
  const [selectedCategory, setSelectedCategory] = useState('전체');
  const [searchType, setSearchType] = useState('title');
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(1);
  const navigate = useNavigate();

  // 1. 모집중인 스터디만 추출
  const studyList = allStudyList.filter(s => s.status === '모집중');

  // 2. 인기 태그 동적 추출
  const popularTags = getPopularTags(posts, 10);

  // 3. 카테고리 필터링
  const categoryFiltered =
    selectedCategory === '전체'
      ? posts
      : posts.filter((post) => post.category === selectedCategory);

  // 4. 검색 필터링
  const filteredPosts = categoryFiltered.filter(post => {
    const term = searchTerm.toLowerCase();
    if (!term) return true;
    if (searchType === 'title') {
      return post.title.toLowerCase().includes(term);
    }
    if (searchType === 'content') {
      return post.content.toLowerCase().includes(term);
    }
    if (searchType === 'title_content') {
      return (
        post.title.toLowerCase().includes(term) ||
        post.content.toLowerCase().includes(term)
      );
    }
    if (searchType === 'author') {
      return post.author && post.author.toLowerCase().includes(term);
    }
    return true;
  });

  // 5. 페이지네이션 계산
  const totalPages = Math.ceil(filteredPosts.length / POSTS_PER_PAGE);
  const paginatedPosts = filteredPosts.slice(
    (page - 1) * POSTS_PER_PAGE,
    page * POSTS_PER_PAGE
  );

  // 6. 인기 게시글 (좋아요 많은 순 Top 5)
  const popularPosts = [...posts]
    .sort((a, b) => b.likes - a.likes)
    .slice(0, 5);

  // 7. 페이지네이션 버튼 배열
  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) pageNumbers.push(i);

  // 8. 검색 시 1페이지로 이동
  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
    setPage(1);
  };
  const handleSearchTypeChange = (e) => {
    setSearchType(e.target.value);
    setPage(1);
  };

  // 9. 태그 클릭 시 검색
  const handleTagClick = (tag) => {
    setSearchType('title_content');
    setSearchTerm(tag);
    setPage(1);
  };

  return (
    <div className="postpage-root">
      <div className="postpage-header">
        <div className="postpage-breadcrumb">커뮤니티 &gt; 게시판</div>
        <button
          className="postpage-write-btn"
          onClick={() => navigate('/write')}
        >
          ✏️ 글쓰기
        </button>
      </div>
      <div className="postpage-main">
        {/* 왼쪽: 게시판 */}
        <div className="postpage-left">
          <div className="postpage-tabs">
            {categories.map((cat) => (
              <button
                key={cat}
                className={`postpage-tab${selectedCategory === cat ? ' selected' : ''}`}
                onClick={() => {
                  setSelectedCategory(cat);
                  setPage(1);
                }}
              >
                {cat}
              </button>
            ))}
          </div>
          {/* 게시글 목록 */}
          <PostList posts={paginatedPosts} setPosts={setPosts} />
          {/* 페이지네이션 + 검색창 */}
          <div className="postpage-bottom-row">
            {/* 페이지네이션 */}
            <div className="post-pagination">
              {pageNumbers.map(num => (
                <button
                  key={num}
                  className={`post-pagination-btn${page === num ? ' active' : ''}`}
                  onClick={() => setPage(num)}
                >
                  {num}
                </button>
              ))}
            </div>
            {/* 검색바 */}
            <div className="post-search-bar">
              <select
                className="post-search-select"
                value={searchType}
                onChange={handleSearchTypeChange}
              >
                {searchOptions.map(opt => (
                  <option key={opt.value} value={opt.value}>{opt.label}</option>
                ))}
              </select>
              <input
                type="text"
                className="post-search-input"
                placeholder={`${searchOptions.find(opt => opt.value === searchType).label}으로 검색`}
                value={searchTerm}
                onChange={handleSearchChange}
              />
            </div>
          </div>
        </div>
        {/* 오른쪽: 인기 게시글, 진행 중인 스터디, 인기 태그 */}
        <div className="postpage-right">
          <div className="postpage-box">
            <div className="postpage-box-title"><span>🔥</span> 인기 게시글</div>
            <ul className="postpage-popular-list">
              {popularPosts.map((p) => (
                <li
                  key={p.id}
                  style={{ cursor: 'pointer' }}
                  onClick={() => navigate(`/community/post/${p.id}`)}
                >
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
                <div
                  className="postpage-study-item"
                  key={i}
                  style={{ background: s.color, cursor: 'pointer' }}
                  onClick={() => navigate(`/study/${s.id}`)}
                >
                  <div className="postpage-study-name">{s.name}</div>
                  <span className="postpage-study-tag" style={{ background: s.tagColor }}>{s.tag}</span>
                  <div className="postpage-study-info">{s.info}</div>
                </div>
              ))}
            </div>
            <button
              style={{
                marginTop: 12,
                background: '#fff',
                color: '#2563eb',
                border: '1.5px solid #2563eb',
                borderRadius: 8,
                padding: '7px 16px',
                fontWeight: 600,
                fontSize: '1rem',
                cursor: 'pointer'
              }}
              onClick={() => navigate('/study')}
            >
              전체 스터디 보기
            </button>
          </div>
          <div className="postpage-box">
            <div className="postpage-box-title">🏷️ 인기 태그</div>
            <div className="postpage-tag-list">
              {popularTags.length === 0 ? (
                <span style={{ color: "#aaa" }}>태그가 없습니다.</span>
              ) : (
                popularTags.map(tag => (
                  <span
                    className="postpage-tag"
                    key={tag}
                    style={{ cursor: 'pointer' }}
                    onClick={() => handleTagClick(tag)}
                  >
                    {tag}
                  </span>
                ))
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Post;
