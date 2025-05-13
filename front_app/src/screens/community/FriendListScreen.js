import React, { useEffect, useState } from 'react';
import {
    View, Text, StyleSheet, TextInput, FlatList,
    TouchableOpacity, Image, Alert
} from 'react-native';
import FastImage from 'react-native-fast-image';
import { useNavigation } from '@react-navigation/native';
import Icon from 'react-native-vector-icons/Entypo';
import EncryptedStorage from 'react-native-encrypted-storage';
import { jwtDecode } from 'jwt-decode';
import api from '../../api/api';

const BASE_URL = 'http://192.168.0.2:8080';

const FriendListScreen = () => {
    const navigation = useNavigation();
    const [searchQuery, setSearchQuery] = useState('');
    const [users, setUsers] = useState([]);
    const [currentUserId, setCurrentUserId] = useState(null);
    const [menuVisible, setMenuVisible] = useState(false);

    useEffect(() => {
        fetchUserId();
        fetchFriendList();
    }, []);

    const fetchUserId = async () => {
        const token = await EncryptedStorage.getItem('accessToken');
        if (token) {
            const decoded = jwtDecode(token);
            setCurrentUserId(Number(decoded.sub));
        }
    };

    const fetchFriendList = async () => {
        try {
            const res = await api.get('/friends/list');
            const friends = res.data.friends.map(u => ({ ...u, isFriend: true }));
            setUsers(friends);
        } catch (err) {
            console.warn('친구 목록 불러오기 실패', err);
        }
    };

    const handleSearch = async () => {
        if (!searchQuery.trim()) return;
        try {
            const res = await api.get(`/friends/search?nickname=${searchQuery.trim()}`);
            const results = res.data.results;
            setUsers(results);
        } catch (err) {
            Alert.alert('검색 실패', '사용자 검색 중 오류 발생');
        }
    };

    const handleSendRequest = async (receiverId) => {
        try {
            await api.post('/friends/request', { receiverId });
            Alert.alert('요청 전송 완료', '친구 요청을 보냈습니다.');
            handleSearch();
        } catch (err) {
            Alert.alert('요청 실패', err.response?.data?.message || '친구 요청 실패');
        }
    };

    const handleStartChat = async (friendId) => {
        try {
            const res = await api.post('/chat/rooms', {
                name: null,
                isPrivate: true,
                targetUserId: friendId
            });
            navigation.navigate('ChatRoom', { roomId: res.data.roomId });
        } catch (err) {
            Alert.alert('실패', '채팅방 생성 실패');
        }
    };

    const handleDeleteFriend = async (friendId) => {
        try {
            await api.delete(`/friends/${friendId}`);
            Alert.alert('삭제 완료', '친구 관계가 삭제되었습니다.');
            fetchFriendList();
        } catch (err) {
            Alert.alert('삭제 실패', err.response?.data?.message || '삭제 중 오류');
        }
    };

    const handleBlockUser = async (userId) => {
        try {
            await api.post(`/friends/block/${userId}`);
            Alert.alert('차단 완료', '해당 사용자를 차단했습니다.');
            fetchFriendList();
        } catch (err) {
            Alert.alert('차단 실패', err.response?.data?.message || '차단 중 오류');
        }
    };

    const renderUserCard = ({ item }) => (
        <View style={styles.card}>
            <FastImage
                source={{ uri: BASE_URL + item.profileImageUrl }}
                style={styles.avatar}
            />
            <View style={styles.center}>
                <Text style={styles.nickname}>{item.nickname}</Text>
            </View>
            <View style={styles.actions}>
                {item.isFriend ? (
                    <>
                        <TouchableOpacity onPress={() => handleStartChat(item.userId)} style={styles.chatBtn}>
                            <Text style={styles.btnText}>채팅</Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => handleBlockUser(item.userId)} style={styles.blockBtn}>
                            <Text style={styles.btnText}>차단</Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => handleDeleteFriend(item.userId)} style={styles.deleteBtn}>
                            <Text style={styles.btnText}>삭제</Text>
                        </TouchableOpacity>
                    </>
                ) : (
                    <TouchableOpacity onPress={() => handleSendRequest(item.userId)} style={styles.requestBtn}>
                        <Text style={styles.btnText}>요청</Text>
                    </TouchableOpacity>
                )}
            </View>
        </View>
    );

    return (
        <View style={styles.container}>
            {/* 헤더 */}
            <View style={styles.header}>
                <View style={{ flex: 1, alignItems: 'center' }}>
                    <Text style={styles.headerTitle}>친구</Text>
                </View>
                <TouchableOpacity onPress={() => setMenuVisible(prev => !prev)} style={styles.menuButton}>
                    <Icon name="menu" size={22} color="#5C504A" />
                </TouchableOpacity>

                {menuVisible && (
                    <View style={styles.dropdownMenu}>
                        <TouchableOpacity onPress={() => navigation.navigate('PostList')}>
                            <Text style={styles.dropdownItem}>게시판</Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => navigation.navigate('ChatList')}>
                            <Text style={styles.dropdownItem}>채팅</Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => navigation.navigate('FriendList')}>
                            <Text style={styles.dropdownItem}>친구</Text>
                        </TouchableOpacity>
                    </View>
                )}
            </View>

            {/* 검색창 */}
            <View style={styles.searchBar}>
                <TextInput
                    value={searchQuery}
                    onChangeText={setSearchQuery}
                    placeholder="닉네임 입력"
                    placeholderTextColor="#9A8E84"
                    style={styles.searchInput}
                />
                <TouchableOpacity onPress={handleSearch}>
                    <Image source={require('../../assets/board_search.png')} style={styles.searchIcon} />
                </TouchableOpacity>
            </View>

            <FlatList
                data={users}
                keyExtractor={(item) => item.userId.toString()}
                renderItem={renderUserCard}
                contentContainerStyle={{ paddingBottom: 100 }}
                ListEmptyComponent={<Text style={styles.emptyText}>검색 결과가 없습니다.</Text>}
            />
        </View>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#F4F1EC' },
    header: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingHorizontal: 20,
        paddingTop: 20,
        paddingBottom: 10,
        backgroundColor: '#F4F1EC',
    },
    headerTitle: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#382F2D',
    },
    menuButton: {
        position: 'absolute',
        right: 20,
        top: 20,
    },
    dropdownMenu: {
        position: 'absolute',
        top: 70,
        right: 20,
        backgroundColor: '#FFFDF9',
        borderRadius: 8,
        paddingVertical: 8,
        paddingHorizontal: 16,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 3 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 6,
        zIndex: 1000,
    },
    dropdownItem: {
        paddingVertical: 8,
        fontSize: 15,
        color: '#5C504A',
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
        marginBottom: 14,
        padding: 12,
        borderRadius: 14,
        flexDirection: 'row',
        alignItems: 'center',
        elevation: 2,
    },
    avatar: {
        width: 44,
        height: 44,
        borderRadius: 22,
        marginRight: 12,
        backgroundColor: '#EEE',
    },
    center: {
        flex: 1,
    },
    nickname: {
        fontSize: 15,
        fontWeight: 'bold',
        color: '#382F2D',
    },
    actions: {
        flexDirection: 'row',
        gap: 6,
    },
    requestBtn: {
        backgroundColor: '#007BFF',
        paddingHorizontal: 10,
        paddingVertical: 6,
        borderRadius: 8,
    },
    chatBtn: {
        backgroundColor: '#28A745',
        paddingHorizontal: 10,
        paddingVertical: 6,
        borderRadius: 8,
    },
    blockBtn: {
        backgroundColor: '#DC3545',
        paddingHorizontal: 10,
        paddingVertical: 6,
        borderRadius: 8,
    },
    deleteBtn: {
        backgroundColor: '#6C757D',
        paddingHorizontal: 10,
        paddingVertical: 6,
        borderRadius: 8,
    },
    btnText: {
        color: '#FFF',
        fontSize: 13,
        fontWeight: 'bold',
    },
    emptyText: {
        textAlign: 'center',
        color: '#999',
        fontSize: 14,
        marginTop: 40,
    },
});

export default FriendListScreen;
