import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, TextInput, FlatList } from 'react-native';

const CURRENT_USER = '익명';

const CommunityPostScreen = ({ navigation, route }) => {
  const { post, postList, setPostList } = route.params;
  const initialComments = Array.isArray(post.comments) ? post.comments : [];

  // 좋아요는 현재 유저가 눌렀는지 상태로 관리
  const [liked, setLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(Number.isFinite(post.likes) ? post.likes : 0);

  const [newComment, setNewComment] = useState('');
  const [comments, setComments] = useState(initialComments);

  // 좋아요 토글 (전역 postList도 업데이트)
  const handleLike = () => {
    setLiked(prev => {
      const newLiked = !prev;
      setLikeCount(count => {
        const newCount = newLiked ? count + 1 : count - 1;
        if (setPostList && postList) {
          setPostList(posts =>
            posts.map(p =>
              p.id === post.id ? { ...p, likes: newCount } : p
            )
          );
        }
        return newCount;
      });
      return newLiked;
    });
  };

  // 댓글 추가
  const handleAddComment = () => {
    if (!newComment.trim()) return;
    const comment = {
      id: Date.now().toString(),
      user: CURRENT_USER,
      content: newComment,
      date: new Date().toLocaleString('ko-KR'),
    };
    setComments(prev => [...(Array.isArray(prev) ? prev : []), comment]);
    setNewComment('');
    // postList에도 댓글수 반영(옵션)
    if (setPostList && postList) {
      setPostList(posts =>
        posts.map(p =>
          p.id === post.id
            ? { ...p, comments: [...(Array.isArray(p.comments) ? p.comments : []), comment] }
            : p
        )
      );
    }
  };

  return (
    <View style={styles.container}>
      {/* 게시글 본문 */}
      <View style={styles.postContainer}>
        <View style={styles.postHeader}>
          <Text style={styles.postUser}>{post.user}</Text>
          <Text style={styles.postDate}>{post.date}</Text>
        </View>
        <Text style={styles.postContent}>{post.content}</Text>
        <View style={styles.postStats}>
          <TouchableOpacity style={styles.statRow} onPress={handleLike}>
            <Text style={[styles.statIcon, liked && styles.liked]}>👍</Text>
            <Text style={styles.stat}>{likeCount}</Text>
          </TouchableOpacity>
          <Text style={styles.stat}>💬 {comments.length}</Text>
          <Text style={styles.stat}>📌 {post.saves}</Text>
        </View>
      </View>

      {/* 댓글 입력 */}
      <View style={styles.commentInputContainer}>
        <TextInput
          style={styles.commentInput}
          placeholder="댓글을 입력하세요"
          value={newComment}
          onChangeText={setNewComment}
          onSubmitEditing={handleAddComment}
        />
        <TouchableOpacity style={styles.commentButton} onPress={handleAddComment}>
          <Text style={styles.commentButtonText}>등록</Text>
        </TouchableOpacity>
      </View>

      {/* 댓글 목록 */}
      <FlatList
        data={comments}
        keyExtractor={item => item.id}
        renderItem={({ item }) => (
          <View style={styles.commentContainer}>
            <View style={styles.commentHeader}>
              <Text style={styles.commentUser}>{item.user}</Text>
              <Text style={styles.commentDate}>{item.date}</Text>
            </View>
            <Text style={styles.commentContent}>{item.content}</Text>
          </View>
        )}
        ListEmptyComponent={
          <Text style={styles.noComments}>아직 댓글이 없습니다.</Text>
        }
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff', padding: 16 },
  postContainer: { 
    backgroundColor: '#f8f8f8', 
    borderRadius: 12, 
    padding: 16, 
    marginBottom: 16 
  },
  postHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 8 },
  postUser: { fontWeight: 'bold', fontSize: 14, color: '#222' },
  postDate: { fontSize: 12, color: '#bbb' },
  postContent: { fontSize: 15, color: '#222', marginBottom: 12 },
  postStats: { flexDirection: 'row', gap: 16, alignItems: 'center' },
  statRow: { flexDirection: 'row', alignItems: 'center', marginRight: 10 },
  stat: { color: '#666', fontSize: 13, marginLeft: 2 },
  statIcon: { fontSize: 18, marginRight: 2, color: '#666' },
  liked: { color: '#007AFF' },
  commentInputContainer: { 
    flexDirection: 'row', 
    gap: 8, 
    marginBottom: 16,
    backgroundColor: '#f5f5f7',
    borderRadius: 8,
    padding: 8,
  },
  commentInput: { 
    flex: 1, 
    backgroundColor: '#fff', 
    borderRadius: 6, 
    padding: 10, 
    fontSize: 14 
  },
  commentButton: { 
    backgroundColor: '#111', 
    borderRadius: 6, 
    paddingHorizontal: 16, 
    justifyContent: 'center' 
  },
  commentButtonText: { color: '#fff', fontWeight: 'bold' },
  commentContainer: { 
    backgroundColor: '#f8f8f8', 
    borderRadius: 8, 
    padding: 12, 
    marginBottom: 8 
  },
  commentHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 4 },
  commentUser: { fontSize: 13, fontWeight: '500', color: '#444' },
  commentDate: { fontSize: 12, color: '#999' },
  commentContent: { fontSize: 14, color: '#222' },
  noComments: { textAlign: 'center', color: '#bbb', marginTop: 20 },
});

export default CommunityPostScreen;
