import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, SafeAreaView } from 'react-native';

const categories = ['공지사항', '스터디모집', '자유'];

const CommunityWriteScreen = ({ navigation }) => {
  const [content, setContent] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('자유');

  const handleSubmit = () => {
    if (!content.trim()) {
      alert('내용을 입력해 주세요!');
      return;
    }
    const newPost = {
      id: Date.now().toString(),
      user: '익명',
      date: new Date().toISOString().slice(0, 16).replace('T', ' '),
      content,
      likes: 0,
      comments: 0,
      saves: 0,
      category: selectedCategory,
    };
    navigation.navigate('Main', {
      screen: '커뮤니티',
      params: { newPost }
    });
  };

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.container}>
        <Text style={styles.title}>글쓰기</Text>
        <View style={styles.categoryRow}>
          {categories.map(cat => (
            <TouchableOpacity
              key={cat}
              style={[
                styles.categoryBtn,
                selectedCategory === cat && styles.categoryBtnActive
              ]}
              onPress={() => setSelectedCategory(cat)}
            >
              <Text
                style={[
                  styles.categoryText,
                  selectedCategory === cat && styles.categoryTextActive
                ]}
              >
                {cat}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
        <TextInput
          style={styles.input}
          placeholder="내용을 입력하세요"
          multiline
          value={content}
          onChangeText={setContent}
        />
        <TouchableOpacity style={styles.button} onPress={handleSubmit}>
          <Text style={styles.buttonText}>등록</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: '#fff' },
  container: { flex: 1, padding: 20 },
  title: { fontSize: 20, fontWeight: 'bold', marginBottom: 20 },
  categoryRow: {
    flexDirection: 'row',
    marginBottom: 16,
  },
  categoryBtn: {
    backgroundColor: '#f5f5f7',
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderRadius: 14,
    marginRight: 8,
  },
  categoryBtnActive: {
    backgroundColor: '#111',
  },
  categoryText: {
    fontSize: 14,
    color: '#888',
    fontWeight: '500',
  },
  categoryTextActive: {
    color: '#fff',
    fontWeight: 'bold',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    padding: 12,
    minHeight: 120,
    fontSize: 15,
    marginBottom: 20,
    backgroundColor: '#fafafa',
    textAlignVertical: 'top',
  },
  button: {
    backgroundColor: '#111',
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  buttonText: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
});

export default CommunityWriteScreen;
