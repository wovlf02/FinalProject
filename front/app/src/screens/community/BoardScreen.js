import React, { useState } from 'react';
import {
    View, Text, StyleSheet, TextInput, FlatList, Image, TouchableOpacity
} from 'react-native';
import { useNavigation } from '@react-navigation/native';

const BoardScreen = () => {
    const navigation = useNavigation();
    const [searchQuery, setSearchQuery] = useState('');

    // 🔹 하드코딩된 게시글 데이터
    const posts = [
        {
            id: '1',
            title: 'React Native로 커뮤니티 앱 만들기',
            username: 'devUser1',
            content: 'React Native를 사용하여 커뮤니티 앱을 개발하는 방법을 공유합니다.',
            createdAt: '2025-02-22 14:30',
            images: [
                require('../../assets/sample1.png'),
                require('../../assets/sample2.png'),
                require('../../assets/sample3.png')
            ]
        },
        {
            id: '2',
            title: 'JavaScript 최신 문법 정리',
            username: 'frontendDev',
            content: 'ES6+에서 추가된 유용한 문법들을 정리해봤습니다.',
            createdAt: '2025-02-21 18:45',
            images: [
                require('../../assets/sample4.png'),
                require('../../assets/sample5.png')
            ]
        },
        {
            id: '3',
            title: '백엔드 개발자가 알아야 할 SQL 최적화',
            username: 'backendGuru',
            content: 'SQL 쿼리를 최적화하는 다양한 기법들을 소개합니다.',
            createdAt: '2025-02-20 09:10',
            images: []
        }
    ];

    // 🔹 게시글 카드 렌더링 함수
    const renderPost = ({ item }) => (
        <TouchableOpacity style={styles.postCard} onPress={() => navigation.navigate('PostDetail', { postId: item.id })}>
            <View style={styles.postHeader}>
                <Text style={styles.postTitle}>{item.title}</Text>
                <Text style={styles.postMeta}>{item.username} • {item.createdAt}</Text>
            </View>
            <Text style={styles.postContent} numberOfLines={2}>{item.content}</Text>
            {item.images.length > 0 && (
                <View style={styles.imageContainer}>
                    {item.images.slice(0, 3).map((img, index) => (
                        <Image key={index} source={img} style={styles.postImage} />
                    ))}
                </View>
            )}
        </TouchableOpacity>
    );

    return (
        <View style={styles.container}>
            {/* 📌 상단 헤더 (커뮤니티 제거, 게시판만 유지) */}
            <View style={styles.headerContainer}>
                <Text style={styles.headerTitle}>게시판</Text>
                <TouchableOpacity onPress={() => navigation.openDrawer()}>
                    <Image source={require('../../assets/menu.png')} style={styles.menuIcon} />
                </TouchableOpacity>
            </View>

            {/* 🔍 검색 바 */}
            <View style={styles.searchBar}>
                <Image source={require('../../assets/board_search.png')} style={styles.searchIcon} />
                <TextInput
                    placeholder="게시글 검색"
                    placeholderTextColor="#888"
                    value={searchQuery}
                    onChangeText={setSearchQuery}
                    style={styles.searchInput}
                />
            </View>

            {/* 📜 게시글 리스트 */}
            <FlatList
                data={posts.filter(post => post.title.includes(searchQuery))}
                keyExtractor={(item) => item.id}
                renderItem={renderPost}
                contentContainerStyle={styles.postList}
            />

            {/* 📝 게시글 작성 버튼 */}
            <TouchableOpacity style={styles.floatingButton} onPress={() => navigation.navigate('CreatePost')}>
                <Image source={require('../../assets/add.png')} style={styles.addIcon} />
            </TouchableOpacity>
        </View>
    );
};

export default BoardScreen;

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F8F9FA',
    },
    /* 📌 헤더 스타일 */
    headerContainer: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
        paddingHorizontal: 15,
        paddingVertical: 15,
        backgroundColor: '#FFF',
        elevation: 3,
        shadowColor: '#000',
        shadowOpacity: 0.1,
        shadowRadius: 5,
    },
    headerTitle: {
        fontSize: 20,
        fontWeight: 'bold',
        textAlign: 'center',
        flex: 1,
    },
    menuIcon: {
        width: 28,
        height: 28,
        resizeMode: 'contain',
    },
    /* 🔍 검색 바 */
    searchBar: {
        flexDirection: 'row',
        backgroundColor: '#FFF',
        padding: 10,
        marginHorizontal: 15,
        marginTop: 10,
        borderRadius: 10,
        alignItems: 'center',
        shadowColor: '#000',
        shadowOpacity: 0.1,
        shadowRadius: 5,
        elevation: 3,
    },
    searchIcon: {
        width: 20,
        height: 20,
        tintColor: '#888',
    },
    searchInput: {
        flex: 1,
        marginLeft: 8,
    },
    /* 📜 게시글 목록 */
    postList: {
        paddingBottom: 80,
    },
    postCard: {
        backgroundColor: '#FFF',
        padding: 15,
        marginHorizontal: 15,
        marginVertical: 10,
        borderRadius: 10,
        shadowColor: '#000',
        shadowOpacity: 0.1,
        shadowRadius: 5,
        elevation: 3,
    },
    postHeader: {
        marginBottom: 5,
    },
    postTitle: {
        fontWeight: 'bold',
        fontSize: 16,
    },
    postMeta: {
        fontSize: 12,
        color: '#666',
    },
    postContent: {
        marginTop: 5,
        fontSize: 14,
        color: '#444',
    },
    imageContainer: {
        flexDirection: 'row',
        marginTop: 8,
    },
    postImage: {
        width: 70,
        height: 70,
        marginRight: 5,
        borderRadius: 5,
    },
    /* 📝 게시글 작성 버튼 */
    floatingButton: {
        position: 'absolute',
        right: 20,
        bottom: 20,
        backgroundColor: '#007BFF',
        width: 60,
        height: 60,
        borderRadius: 30,
        alignItems: 'center',
        justifyContent: 'center',
        elevation: 5,
    },
    addIcon: {
        width: 30,
        height: 30,
        tintColor: '#FFF',
    },
});
