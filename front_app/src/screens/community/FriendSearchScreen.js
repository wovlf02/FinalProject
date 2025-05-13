import React, { useState } from 'react';
import {
    View, Text, StyleSheet, TextInput, FlatList, TouchableOpacity,
    Image, Alert, ActivityIndicator, Dimensions,
} from 'react-native';
import api from '../../api/api';
import FastImage from 'react-native-fast-image';

const { width } = Dimensions.get('window');
const BASE_URL = 'http://192.168.0.2:8080';

const FriendSearchScreen = () => {
    const [searchQuery, setSearchQuery] = useState('');
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(false);

    const handleSearch = async () => {
        if (!searchQuery.trim()) return;

        try {
            setLoading(true);
            const res = await api.get(`/api/friends/search?nickname=${searchQuery.trim()}`);
            setResults(res.data.results || []);
        } catch (error) {
            console.warn('검색 오류', error);
            Alert.alert('검색 실패', '검색 중 오류가 발생했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleSendRequest = async (receiverId) => {
        try {
            await api.post('/api/friends/request', { receiverId });
            Alert.alert('성공', '친구 요청을 보냈습니다.');
            handleSearch(); // 다시 새로고침
        } catch (error) {
            Alert.alert('실패', error.response?.data?.message || '요청 전송 실패');
        }
    };

    const renderUserCard = ({ item }) => (
        <View style={styles.card}>
            <FastImage
                source={{ uri: BASE_URL + item.profileImageUrl }}
                style={styles.avatar}
            />
            <View style={styles.userInfo}>
                <Text style={styles.username}>{item.username}</Text>
                <Text style={styles.nickname}>{item.nickname}</Text>
            </View>

            {item.isFriend ? (
                <View style={styles.friendBadge}>
                    <Text style={styles.friendBadgeText}>친구 관계입니다</Text>
                </View>
            ) : (
                <TouchableOpacity
                    style={styles.requestButton}
                    onPress={() => handleSendRequest(item.userId)}
                >
                    <Text style={styles.requestButtonText}>요청 전송</Text>
                </TouchableOpacity>
            )}
        </View>
    );

    return (
        <View style={styles.container}>
            <View style={styles.header}>
                <Text style={styles.headerTitle}>친구 검색</Text>
            </View>

            <View style={styles.searchBar}>
                <TextInput
                    placeholder="닉네임 입력"
                    placeholderTextColor="#9A8E84"
                    value={searchQuery}
                    onChangeText={setSearchQuery}
                    style={styles.searchInput}
                />
                <TouchableOpacity onPress={handleSearch}>
                    <Image source={require('../../assets/board_search.png')} style={styles.searchIcon} />
                </TouchableOpacity>
            </View>

            {loading ? (
                <ActivityIndicator size="large" color="#A3775C" style={{ marginTop: 40 }} />
            ) : (
                <FlatList
                    data={results}
                    keyExtractor={(item) => item.userId.toString()}
                    renderItem={renderUserCard}
                    ListEmptyComponent={<Text style={styles.emptyText}>해당 닉네임을 가진 사용자가 없습니다.</Text>}
                    contentContainerStyle={{ paddingBottom: 100 }}
                />
            )}
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F4F1EC',
    },
    header: {
        paddingTop: 20,
        paddingBottom: 10,
        alignItems: 'center',
        backgroundColor: '#F4F1EC',
    },
    headerTitle: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#382F2D',
    },
    searchBar: {
        flexDirection: 'row',
        backgroundColor: '#FFFDF9',
        borderRadius: 12,
        padding: 10,
        margin: 16,
        elevation: 3,
        alignItems: 'center',
    },
    searchInput: {
        flex: 1,
        fontSize: 15,
        color: '#3C3C3C',
        paddingHorizontal: 8,
    },
    searchIcon: {
        width: 22,
        height: 22,
        tintColor: '#A3775C',
    },
    card: {
        backgroundColor: '#FFFDF9',
        marginHorizontal: 16,
        marginBottom: 16,
        padding: 14,
        borderRadius: 14,
        flexDirection: 'row',
        alignItems: 'center',
        elevation: 2,
    },
    avatar: {
        width: 48,
        height: 48,
        borderRadius: 24,
        marginRight: 12,
        backgroundColor: '#EEE',
    },
    userInfo: {
        flex: 1,
    },
    username: {
        fontSize: 15,
        fontWeight: 'bold',
        color: '#382F2D',
    },
    nickname: {
        fontSize: 13,
        color: '#9A8E84',
    },
    requestButton: {
        backgroundColor: '#A3775C',
        paddingHorizontal: 12,
        paddingVertical: 6,
        borderRadius: 8,
    },
    requestButtonText: {
        color: '#FFF',
        fontWeight: 'bold',
        fontSize: 13,
    },
    friendBadge: {
        backgroundColor: '#E0DED9',
        paddingHorizontal: 10,
        paddingVertical: 6,
        borderRadius: 8,
    },
    friendBadgeText: {
        color: '#5C504A',
        fontSize: 13,
    },
    emptyText: {
        textAlign: 'center',
        color: '#999',
        fontSize: 14,
        marginTop: 40,
    },
});

export default FriendSearchScreen;
