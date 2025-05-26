import React, { useEffect, useState } from 'react';
import PostList from './components/PostList';
import '../../css/Post.css';
import { useNavigate } from 'react-router-dom';
import api from '../../api/api';

const categories = ['전체', '질문', '정보공유', '스터디', '익명', '일반', '공지사항'];

const CATEGORY_ENUM = {
    '질문': 'QUESTION',
    '정보공유': 'INFO',
    '스터디': 'STUDY',
    '익명': 'ANONYMOUS',
    '일반': 'GENERAL',
    '공지사항': 'NOTICE',
};

const searchOptions = [
    { value: 'title', label: '제목' },
    { value: 'content', label: '내용' },
    { value: 'title_content', label: '제목+내용' },
    { value: 'author', label: '작성자' },
];

const POSTS_PER_PAGE = 10;

const Post = () => {
    const navigate = useNavigate();

    const [posts, setPosts] = useState([]);
    const [popularPosts, setPopularPosts] = useState([]);
    const [popularTags, setPopularTags] = useState([]);
    const [studyList, setStudyList] = useState([]);

    const [selectedCategory, setSelectedCategory] = useState('전체');
    const [searchType, setSearchType] = useState('title');
    const [searchTerm, setSearchTerm] = useState('');
    const [page, setPage] = useState(1);

    // 🔹 게시글 목록
    const fetchPosts = async () => {
        try {
            const requestData = {
                page: 0,
                size: 100,
                ...(selectedCategory !== '전체' && {
                    category: CATEGORY_ENUM[selectedCategory],
                }),
            };

            const res = await api.post('/community/posts/list', requestData);

            const mappedPosts = (res.data.posts || []).map((post) => ({
                postId: post.post_id,
                title: post.title,
                content: post.content, // 필터링용으로만 사용
                category: post.category,
                viewCount: post.view_count,
                createdAt: post.created_at,
                author: post.writer_nickname,
                likeCount: post.like_count,
                commentCount: post.comment_count,
            }));

            setPosts(mappedPosts);
        } catch (err) {
            console.error('❌ 게시글 목록 조회 실패:', err);
        }
    };

    // 🔹 인기 게시글
    const fetchPopularPosts = async () => {
        try {
            const res = await api.get('/community/posts/popular');
            const mapped = (res.data.posts || []).map((post) => ({
                postId: post.post_id,
                title: post.title,
                authorNickname: post.writer_nickname,
                likeCount: post.like_count,
            }));
            setPopularPosts(mapped);
        } catch (err) {
            console.error('❌ 인기 게시글 조회 실패:', err);
        }
    };

    // 🔹 인기 태그
    const fetchPopularTags = async () => {
        try {
            const res = await api.get('/community/posts/sidebar/tags');
            setPopularTags(res.data.tags || []);
        } catch (err) {
            console.error('❌ 인기 태그 조회 실패:', err);
        }
    };

    // 🔹 진행 중인 스터디
    const fetchStudyList = async () => {
        try {
            const res = await api.get('/community/posts/sidebar/studies');
            const ongoing = res.data.studies?.filter((s) => s.status === '모집중') || [];
            setStudyList(ongoing);
        } catch (err) {
            console.error('❌ 스터디 목록 조회 실패:', err);
        }
    };

    useEffect(() => {
        fetchPopularPosts();
        fetchPopularTags();
        fetchStudyList();
    }, []);

    useEffect(() => {
        fetchPosts();
    }, [selectedCategory]);

    // 🔹 검색 필터링
    const filteredPosts = posts.filter((post) => {
        const term = searchTerm.toLowerCase();
        if (!term) return true;
        const target = {
            title: post.title,
            content: post.content,
            title_content: `${post.title} ${post.content}`,
            author: post.author,
        }[searchType] || '';
        return target.toLowerCase().includes(term);
    });

    // 🔹 페이지네이션
    const totalPages = Math.ceil(filteredPosts.length / POSTS_PER_PAGE);
    const paginatedPosts = filteredPosts.slice(
        (page - 1) * POSTS_PER_PAGE,
        page * POSTS_PER_PAGE
    );
    const pageNumbers = Array.from({ length: totalPages }, (_, i) => i + 1);

    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
        setPage(1);
    };

    const handleSearchTypeChange = (e) => {
        setSearchType(e.target.value);
        setPage(1);
    };

    const handleTagClick = (tag) => {
        setSearchType('title_content');
        setSearchTerm(tag);
        setPage(1);
    };

    return (
        <div className="postpage-root">
            <div className="postpage-header">
                <div className="postpage-breadcrumb">커뮤니티 &gt; 게시판</div>
                <button className="postpage-write-btn" onClick={() => navigate('/write')}>
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
                    <PostList posts={paginatedPosts} />
                    <div className="postpage-bottom-row">
                        <div className="post-pagination">
                            {pageNumbers.map((num) => (
                                <button
                                    key={num}
                                    className={`post-pagination-btn${page === num ? ' active' : ''}`}
                                    onClick={() => setPage(num)}
                                >
                                    {num}
                                </button>
                            ))}
                        </div>
                        <div className="post-search-bar">
                            <select
                                className="post-search-select"
                                value={searchType}
                                onChange={handleSearchTypeChange}
                            >
                                {searchOptions.map((opt) => (
                                    <option key={opt.value} value={opt.value}>
                                        {opt.label}
                                    </option>
                                ))}
                            </select>
                            <input
                                type="text"
                                className="post-search-input"
                                placeholder={`${searchOptions.find((opt) => opt.value === searchType).label}으로 검색`}
                                value={searchTerm}
                                onChange={handleSearchChange}
                            />
                        </div>
                    </div>
                </div>

                {/* 오른쪽: 인기 게시글, 스터디, 태그 */}
                <div className="postpage-right">
                    <div className="postpage-box">
                        <div className="postpage-box-title">
                            <span>🔥</span> 인기 게시글
                        </div>
                        <ul className="postpage-popular-list">
                            {popularPosts.map((p) => (
                                <li
                                    key={p.postId}
                                    style={{ cursor: 'pointer' }}
                                    onClick={() => navigate(`/community/post/${p.postId}`)}
                                >
                                    <div className="postpage-popular-title">{p.title}</div>
                                    <div className="postpage-popular-meta">
                                        <span>{p.authorNickname}</span>
                                        <span>좋아요 {p.likeCount}</span>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    </div>
                    <div className="postpage-box">
                        <div className="postpage-box-title" style={{ color: '#7c3aed' }}>
                            💡 진행 중인 스터디
                        </div>
                        <div className="postpage-study-list">
                            {studyList.map((s) => (
                                <div
                                    className="postpage-study-item"
                                    key={s.studyId}
                                    style={{ background: s.color, cursor: 'pointer' }}
                                    onClick={() => navigate(`/study/${s.studyId}`)}
                                >
                                    <div className="postpage-study-name">{s.name}</div>
                                    <span className="postpage-study-tag" style={{ background: s.tagColor }}>
                                        {s.tag}
                                    </span>
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
                                cursor: 'pointer',
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
                                <span style={{ color: '#aaa' }}>태그가 없습니다.</span>
                            ) : (
                                popularTags.map((tag) => (
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
