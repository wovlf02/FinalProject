import React, { useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, Dimensions } from 'react-native';
import { TabView, SceneMap, TabBar } from 'react-native-tab-view';

// 더미 데이터
const chatRooms = [
    { id: '1', name: 'React Native 스터디' },
    { id: '2', name: '알고리즘 공부방' },
    { id: '3', name: 'CS 지식 공유' },
];

const posts = [
    { id: '1', title: 'React Navigation 사용법', author: '김개발' },
    { id: '2', title: '코딩 테스트 팁 공유', author: '이프로그' },
    { id: '3', title: '학습 관리 앱 개발기', author: '박디버그' },
];

const friends = [
    { id: '1', name: '김코딩' },
    { id: '2', name: '이디자인' },
    { id: '3', name: '박프로그' },
];

// 채팅 탭 UI
const ChatList = () => (
    <FlatList
        data={chatRooms}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
            <TouchableOpacity style={styles.card}>
                <Text style={styles.cardTitle}>{item.name}</Text>
            </TouchableOpacity>
        )}
    />
);

// 게시판 탭 UI
const PostList = () => (
    <FlatList
        data={posts}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
            <TouchableOpacity style={styles.card}>
                <Text style={styles.cardTitle}>{item.title}</Text>
                <Text style={styles.cardSubtitle}>작성자: {item.author}</Text>
            </TouchableOpacity>
        )}
    />
);

// 친구관리 탭 UI
const FriendList = () => (
    <FlatList
        data={friends}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
            <TouchableOpacity style={styles.card}>
                <Text style={styles.cardTitle}>{item.name}</Text>
            </TouchableOpacity>
        )}
    />
);

// 탭 구조 설정
const initialLayout = { width: Dimensions.get('window').width };

const CommunityMainScreen = () => {
    const [index, setIndex] = useState(0);
    const [routes] = useState([
        { key: 'chat', title: '채팅' },
        { key: 'board', title: '게시판' },
        { key: 'friends', title: '친구관리' },
    ]);

    const renderScene = SceneMap({
        chat: ChatList,
        board: PostList,
        friends: FriendList,
    });

    return (
        <TabView
            navigationState={{ index, routes }}
            renderScene={renderScene}
            onIndexChange={setIndex}
            initialLayout={initialLayout}
            renderTabBar={(props) => (
                <TabBar {...props} style={styles.tabBar} indicatorStyle={styles.indicator} />
            )}
        />
    );
};

// 스타일
const styles = StyleSheet.create({
    tabBar: {
        backgroundColor: '#007AFF',
    },
    indicator: {
        backgroundColor: 'white',
    },
    card: {
        backgroundColor: '#fff',
        padding: 15,
        marginVertical: 8,
        marginHorizontal: 10,
        borderRadius: 10,
        shadowColor: '#000',
        shadowOpacity: 0.1,
        shadowRadius: 5,
        elevation: 3,
    },
    cardTitle: {
        fontSize: 16,
        fontWeight: 'bold',
    },
    cardSubtitle: {
        fontSize: 12,
        color: 'gray',
    },
});

export default CommunityMainScreen;
