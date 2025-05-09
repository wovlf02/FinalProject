import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  FlatList,
  SafeAreaView,
  ScrollView,
} from 'react-native';

const categories = ['전체', '공지사항', '스터디모집', '자유'];
const initialPosts = [
  {
    id: '1',
    user: '김종프',
    date: '2025.03.19 14:30',
    content: '오늘 단원평가 스터디에서 좋은 성과가 있었습니다! 함께 공부하니 더 효율적이네요 😊',
    likes: 12,
    comments: [
      { id: 'c1', user: '익명', content: '저도 참여하고 싶어요!', date: '2025.03.19 15:00' },
      { id: 'c2', user: '홍길동', content: '다음 스터디 언제인가요?', date: '2025.03.19 15:30' },
    ],
    saves: 8,
    category: '스터디모집',
  },
  {
    id: '2',
    user: '이종프',
    date: '2025.03.19 13:15',
    content: '스터디원 모집합니다! 매주 월/수/금 저녁 8시에 단원평가 준비하실 분~',
    likes: 8,
    comments: [
      { id: 'c3', user: '스터디장', content: '관심 있는 분은 쪽지 주세요!', date: '2025.03.19 14:00' },
    ],
    saves: 6,
    category: '스터디모집',
  },
  {
    id: '3',
    user: '운영자',
    date: '2025.03.18 10:00',
    content: '커뮤니티 이용규칙을 꼭 읽어주세요!',
    likes: 2,
    comments: [],
    saves: 1,
    category: '공지사항',
  },
  {
    id: '4',
    user: '박스터디',
    date: '2025.03.17 22:12',
    content: '오늘 공부 너무 힘들었어요. 다들 화이팅!',
    likes: 5,
    comments: [
      { id: 'c4', user: '응원러', content: '우리 모두 화이팅!!', date: '2025.03.18 09:30' },
    ],
    saves: 0,
    category: '자유',
  },
];

const CURRENT_USER = '익명';

const CommunityMainScreen = ({ navigation, route }) => {
  const [selectedCategory, setSelectedCategory] = useState('전체');
  const [search, setSearch] = useState('');
  const [postList, setPostList] = useState(initialPosts);

  useEffect(() => {
    if (route.params?.newPost) {
      const newPostWithComments = { ...route.params.newPost, comments: [] };
      setPostList(prev => [newPostWithComments, ...prev]);
    }
  }, [route.params?.newPost]);

  const handleDelete = (id) => {
    setPostList(prev => prev.filter(post => post.id !== id));
  };

  const filteredPosts = postList.filter(
    (post) =>
      (selectedCategory === '전체' || post.category === selectedCategory) &&
      post.content.toLowerCase().includes(search.toLowerCase())
  );

  const renderCategory = (cat) => (
    <TouchableOpacity
      key={cat}
      style={[
        styles.categoryBtn,
        selectedCategory === cat && styles.categoryBtnActive,
      ]}
      onPress={() => setSelectedCategory(cat)}
    >
      <Text
        style={[
          styles.categoryText,
          selectedCategory === cat && styles.categoryTextActive,
        ]}
      >
        {cat}
      </Text>
    </TouchableOpacity>
  );

  const renderPost = ({ item }) => (
    <TouchableOpacity 
      style={styles.postCard}
      onPress={() => navigation.navigate('CommunityPost', { 
        post: { ...item, comments: Array.isArray(item.comments) ? item.comments : [] },
        postList,
        setPostList,
      })}
    >
      <View style={styles.postHeader}>
        <Text style={styles.postUser}>{item.user}</Text>
        <Text style={styles.postDate}>{item.date}</Text>
      </View>
      <Text style={styles.postContent}>{item.content}</Text>
      <View style={styles.postFooter}>
        <View style={styles.postFooterItem}>
          <Text style={styles.footerIcon}>👍</Text>
          <Text style={styles.footerText}>{item.likes}</Text>
        </View>
        <View style={styles.postFooterItem}>
          <Text style={styles.footerIcon}>💬</Text>
          <Text style={styles.footerText}>{item.comments.length}</Text>
        </View>
        <View style={styles.postFooterItem}>
          <Text style={styles.footerIcon}>📌</Text>
          <Text style={styles.footerText}>{item.saves}</Text>
        </View>
        {item.user === CURRENT_USER && (
          <TouchableOpacity
            style={styles.deleteBtn}
            onPress={() => handleDelete(item.id)}
          >
            <Text style={styles.deleteBtnText}>삭제</Text>
          </TouchableOpacity>
        )}
      </View>
    </TouchableOpacity>
  );

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.container}>
        <View style={styles.searchBox}>
          <TextInput
            style={styles.searchInput}
            placeholder="검색어를 입력해주세요 (예: 단원평가 스터디)"
            placeholderTextColor="#bdbdbd"
            value={search}
            onChangeText={setSearch}
          />
        </View>
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.categoryScroll}
        >
          {categories.map(renderCategory)}
        </ScrollView>
        <View style={{ flex: 1 }}>
          <Text style={styles.sectionTitle}>최근 게시글</Text>
          <FlatList
            data={filteredPosts}
            renderItem={renderPost}
            keyExtractor={(item) => item.id}
            contentContainerStyle={{ paddingBottom: 100 }}
            showsVerticalScrollIndicator={false}
            ListEmptyComponent={
              <Text style={{ textAlign: 'center', color: '#bbb', marginTop: 40 }}>
                게시글이 없습니다.
              </Text>
            }
          />
        </View>
        <TouchableOpacity
          style={styles.fab}
          onPress={() => navigation.navigate('CommunityWrite')}
        >
          <Text style={styles.fabText}>＋</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: '#fff' },
  container: {
    flex: 1,
    backgroundColor: '#fff',
    paddingHorizontal: 16,
  },
  searchBox: {
    flexDirection: 'row',
    backgroundColor: '#f5f5f7',
    borderRadius: 8,
    alignItems: 'center',
    paddingHorizontal: 12,
    marginBottom: 12,
    height: 44,
  },
  searchInput: {
    flex: 1,
    fontSize: 15,
    color: '#222',
  },
  categoryScroll: {
    flexDirection: 'row',
    paddingVertical: 0,
    marginBottom: 0,
  },
  categoryBtn: {
    backgroundColor: '#f5f5f7',
    paddingHorizontal: 10,
    paddingVertical: 3,
    borderRadius: 10,
    marginRight: 4,
    alignSelf: 'flex-start',
    justifyContent: 'center',
    alignItems: 'center',
  },
  categoryBtnActive: {
    backgroundColor: '#111',
  },
  categoryText: {
    fontSize: 12,
    color: '#888',
    fontWeight: '500',
  },
  categoryTextActive: {
    color: '#fff',
    fontWeight: 'bold',
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginTop: -200,
    marginBottom: 8,
  },
  postCard: {
    backgroundColor: '#f8f8f8',
    borderRadius: 12,
    padding: 16,
    marginBottom: 14,
  },
  postHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 6,
  },
  postUser: {
    fontWeight: 'bold',
    fontSize: 14,
    color: '#222',
  },
  postDate: {
    fontSize: 12,
    color: '#bbb',
  },
  postContent: {
    fontSize: 15,
    color: '#222',
    marginBottom: 12,
  },
  postFooter: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
    alignItems: 'center',
  },
  postFooterItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginRight: 20,
  },
  footerIcon: {
    fontSize: 16,
    marginRight: 3,
    color: '#888',
  },
  footerText: {
    fontSize: 13,
    color: '#888',
  },
  deleteBtn: {
    backgroundColor: '#ff4444',
    borderRadius: 8,
    paddingHorizontal: 10,
    paddingVertical: 4,
    marginLeft: 10,
  },
  deleteBtnText: {
    color: '#fff',
    fontWeight: 'bold',
    fontSize: 13,
  },
  fab: {
    position: 'absolute',
    right: 20,
    bottom: 32,
    backgroundColor: '#111',
    width: 56,
    height: 56,
    borderRadius: 28,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 3,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 3,
  },
  fabText: {
    color: '#fff',
    fontWeight: 'bold',
    fontSize: 32,
    lineHeight: 36,
  },
});

export default CommunityMainScreen;
