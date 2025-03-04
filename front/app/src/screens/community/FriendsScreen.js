import React, { useState } from 'react';
import { View, Text, StyleSheet, TextInput, FlatList, Image, TouchableOpacity } from 'react-native';

const FriendScreen = () => {
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);

    // 🔹 하드코딩된 사용자 데이터
    const allUsers = [
        { id: '1', username: 'devUser1', profileImage: require('../../assets/sample1.png'), isFriend: true },
        { id: '2', username: 'frontendDev', profileImage: require('../../assets/sample2.png'), isFriend: false },
        { id: '3', username: 'backendGuru', profileImage: require('../../assets/sample3.png'), isFriend: false }
    ];

    // 🔍 사용자 검색 함수
    const handleSearch = () => {
        const filteredUsers = allUsers.filter(user => user.username.includes(searchQuery));
        setSearchResults(filteredUsers);
    };

    // 🔹 친구 목록 렌더링 함수
    const renderUserCard = ({ item }) => (
        <View style={styles.userCard}>
            <Image source={item.profileImage} style={styles.profileImage} />
            <Text style={styles.username}>{item.username}</Text>
            {item.isFriend ? (
                <View style={styles.friendBadge}>
                    <Text style={styles.friendText}>친구</Text>
                </View>
            ) : (
                <TouchableOpacity style={styles.addFriendButton}>
                    <Text style={styles.addFriendText}>친구 추가</Text>
                </TouchableOpacity>
            )}
        </View>
    );

    return (
        <View style={styles.container}>
            {/* 🔍 검색 바 */}
            <View style={styles.searchBar}>
                <TextInput
                    placeholder="아이디 검색"
                    placeholderTextColor="#888"
                    value={searchQuery}
                    onChangeText={setSearchQuery}
                    style={styles.searchInput}
                />
                <TouchableOpacity style={styles.searchButton} onPress={handleSearch}>
                    <Text style={styles.searchButtonText}>검색</Text>
                </TouchableOpacity>
            </View>

            {/* 📜 친구 검색 결과 */}
            <FlatList
                data={searchResults}
                keyExtractor={(item) => item.id}
                renderItem={renderUserCard}
                contentContainerStyle={styles.friendList}
            />
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F8F9FA',
    },
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
    searchInput: {
        flex: 1,
        marginLeft: 8,
    },
    searchButton: {
        backgroundColor: '#007BFF',
        paddingHorizontal: 15,
        paddingVertical: 8,
        borderRadius: 5,
    },
    searchButtonText: {
        color: '#FFF',
        fontWeight: 'bold',
    },
    friendList: {
        paddingBottom: 80,
    },
    userCard: {
        flexDirection: 'row',
        alignItems: 'center',
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
    profileImage: {
        width: 50,
        height: 50,
        borderRadius: 25,
        marginRight: 15,
    },
    username: {
        fontSize: 16,
        flex: 1,
    },
    friendBadge: {
        backgroundColor: '#CCC',
        paddingHorizontal: 10,
        paddingVertical: 5,
        borderRadius: 5,
    },
    friendText: {
        color: '#333',
    },
    addFriendButton: {
        backgroundColor: '#007BFF',
        paddingHorizontal: 10,
        paddingVertical: 5,
        borderRadius: 5,
    },
    addFriendText: {
        color: '#FFF',
        fontWeight: 'bold',
    },
});

export default FriendScreen;
