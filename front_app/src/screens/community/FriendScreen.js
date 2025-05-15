import React, { useEffect, useState } from 'react';
import {
    View, Text, StyleSheet, TextInput, FlatList,
    TouchableOpacity, Image, Alert, ActivityIndicator
} from 'react-native';
import FastImage from 'react-native-fast-image';
import { useNavigation } from '@react-navigation/native';
import EncryptedStorage from 'react-native-encrypted-storage';
import { jwtDecode } from 'jwt-decode';
import api from '../../api/api';

const BASE_URL = 'http://192.168.78.59:8080';

const FriendScreen = () => {
    const navigation = useNavigation();
    const [searchQuery, setSearchQuery] = useState('');
    const [users, setUsers] = useState([]);
    const [currentUserId, setCurrentUserId] = useState(null);
    const [menuVisible, setMenuVisible] = useState(false);
    const [activeTab, setActiveTab] = useState('friends'); // friends | requests | blocked
    const [loading, setLoading] = useState(false);
    const [searchMode, setSearchMode] = useState(false); // 검색 결과 여부

    useEffect(() => {
        fetchUserId();
    }, []);

    useEffect(() => {
        if (searchMode) return; // 검색모드면 fetchListByTab 실행 안 함
        fetchListByTab();
    }, [activeTab]);

    const fetchUserId = async () => {
        try {
            const token = await EncryptedStorage.getItem('accessToken');
            if (token) {
                const decoded = jwtDecode(token);
                setCurrentUserId(Number(decoded.sub));
            }
        } catch (err) {
            console.warn('토큰 에러:', err);
        }
    };

    const fetchListByTab = async () => {
        setLoading(true);
        try {
            let res;
            if (activeTab === 'friends') {
                res = await api.get('/friends');
                setUsers(res.data?.friends?.map(u => ({ ...u, isFriend: true })) || []);
            } else if (activeTab === 'requests') {
                res = await api.get('/friends/requests');
                setUsers(res.data?.requests?.map(u => ({ ...u, isRequest: true })) || []);
            } else if (activeTab === 'blocked') {
                res = await api.get('/friends/blocked');
                setUsers(res.data?.blockedUsers?.map(u => ({ ...u, isBlocked: true })) || []);
            }
        } catch (err) {
            console.warn('목록 불러오기 실패', err);
            setUsers([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async () => {
        if (!searchQuery.trim()) return;
        setSearchMode(true);
        setLoading(true);
        try {
            const res = await api.get(`/friends/search?nickname=${searchQuery.trim()}`);
            setUsers(res.data?.results || []);
        } catch (err) {
            Alert.alert('검색 실패', '사용자 검색 중 오류 발생');
            setUsers([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSendRequest = async (targetUserId) => {
        try {
            await api.post('/friends/request', { targetUserId });
            Alert.alert('요청 전송 완료', '친구 요청을 보냈습니다.');
            fetchListByTab();
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
            fetchListByTab();
        } catch (err) {
            Alert.alert('삭제 실패', err.response?.data?.message || '삭제 중 오류');
        }
    };

    const handleBlockUser = async (userId) => {
        try {
            await api.post(`/friends/block/${userId}`);
            Alert.alert('차단 완료', '해당 사용자를 차단했습니다.');
            fetchListByTab();
        } catch (err) {
            Alert.alert('차단 실패', err.response?.data?.message || '차단 중 오류');
        }
    };

    const handleUnblockUser = async (userId) => {
        try {
            await api.delete(`/friends/block/${userId}`);
            Alert.alert('해제 완료', '차단이 해제되었습니다.');
            fetchListByTab();
        } catch (err) {
            Alert.alert('해제 실패', err.response?.data?.message || '차단 해제 실패');
        }
    };

    const handleAcceptRequest = async (requestId, senderId) => {
        try {
            await api.post(`/friends/request/${requestId}/accept`, {
                requestId: senderId
            });
            Alert.alert('수락 완료', '친구 요청을 수락했습니다.');
            fetchListByTab();
        } catch (err) {
            Alert.alert('수락 실패', err.response?.data?.message || '요청 수락 실패');
        }
    };

    const handleRejectRequest = async (requestId, senderId) => {
        try {
            await api.post(`/friends/request/${requestId}/reject`, {
                receiverId: senderId
            });
            Alert.alert('거절 완료', '친구 요청을 거절했습니다.');
            fetchListByTab();
        } catch (err) {
            Alert.alert('거절 실패', err.response?.data?.message || '요청 거절 실패');
        }
    };

    const renderUserCard = ({ item }) => {
        const isRequestTab = activeTab === 'requests';
        const isMeSender = item.senderId === currentUserId;

        const nickname = item.nickname || item.senderNickname || '알 수 없음';
        const profileImage = item.profileImageUrl || '';
        const userId = item.userId || item.senderId;

        const isSearchResult = searchMode;
        const isFriend = item.alreadyFriend || item.isFriend;

        return (
            <View style={styles.card}>
                <FastImage source={{ uri: BASE_URL + profileImage }} style={styles.avatar} />
                <View style={styles.center}>
                    <Text style={styles.nickname}>{nickname}</Text>
                </View>
                <View style={styles.actions}>
                    {/* 검색 결과일 경우 */}
                    {isSearchResult ? (
                        item.blocked ? (
                            <Text style={{ color: '#999' }}>차단 상태</Text>
                        ) : isFriend ? (
                            <Text style={{ color: '#999' }}>이미 친구</Text>
                        ) : item.alreadyRequested ? (
                            <Text style={{ color: '#999' }}>요청 보냄</Text>
                        ) : (
                            <TouchableOpacity onPress={() => handleSendRequest(userId)} style={styles.requestBtn}>
                                <Text style={styles.btnText}>요청</Text>
                            </TouchableOpacity>
                        )
                    ) : (
                        <>
                            {activeTab === 'friends' && (
                                <>
                                    <TouchableOpacity onPress={() => handleStartChat(userId)} style={styles.chatBtn}>
                                        <Text style={styles.btnText}>채팅</Text>
                                    </TouchableOpacity>
                                    <TouchableOpacity onPress={() => handleBlockUser(userId)} style={styles.blockBtn}>
                                        <Text style={styles.btnText}>차단</Text>
                                    </TouchableOpacity>
                                    <TouchableOpacity onPress={() => handleDeleteFriend(userId)} style={styles.deleteBtn}>
                                        <Text style={styles.btnText}>삭제</Text>
                                    </TouchableOpacity>
                                </>
                            )}
                            {isRequestTab && (
                                isMeSender ? (
                                    <Text style={{ color: '#666' }}>보낸 요청</Text>
                                ) : (
                                    <>
                                        <TouchableOpacity
                                            onPress={() => handleAcceptRequest(item.requestId, item.senderId)}
                                            style={styles.chatBtn}
                                        >
                                            <Text style={styles.btnText}>수락</Text>
                                        </TouchableOpacity>
                                        <TouchableOpacity
                                            onPress={() => handleRejectRequest(item.requestId, item.senderId)}
                                            style={styles.deleteBtn}
                                        >
                                            <Text style={styles.btnText}>거절</Text>
                                        </TouchableOpacity>
                                    </>
                                )
                            )}
                            {activeTab === 'blocked' && (
                                <TouchableOpacity onPress={() => handleUnblockUser(userId)} style={styles.unblockBtn}>
                                    <Text style={styles.btnText}>해제</Text>
                                </TouchableOpacity>
                            )}
                        </>
                    )}
                </View>
            </View>
        );
    };


    return (
        <View style={styles.container}>
            {/* 헤더 */}
            <View style={styles.header}>
                <View style={{ flex: 1, alignItems: 'center' }}>
                    <Text style={styles.headerTitle}>친구</Text>
                </View>
                <TouchableOpacity onPress={() => setMenuVisible(prev => !prev)} style={styles.menuButton}>
                    <Image source={require('../../assets/menu.png')} style={styles.menuIcon} />
                </TouchableOpacity>
                {menuVisible && (
                    <View style={styles.dropdownMenu}>
                        <TouchableOpacity onPress={() => navigation.navigate('PostList')}>
                            <Text style={styles.dropdownItem}>게시판</Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => navigation.navigate('ChatRoomList')}>
                            <Text style={styles.dropdownItem}>채팅</Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => navigation.navigate('Friend')}>
                            <Text style={styles.dropdownItem}>친구</Text>
                        </TouchableOpacity>
                    </View>
                )}
            </View>

            {/* 탭 */}
            <View style={styles.tabContainer}>
                {['friends', 'requests', 'blocked'].map(tab => (
                    <TouchableOpacity
                        key={tab}
                        onPress={() => setActiveTab(tab)}
                        style={[styles.tabButton, activeTab === tab && styles.tabActive]}
                    >
                        <Text style={[styles.tabText, activeTab === tab && styles.tabTextActive]}>
                            {tab === 'friends' ? '친구 목록' : tab === 'requests' ? '요청 목록' : '차단 목록'}
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>

            {/* 검색 */}
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

            {/* 목록 */}
            {loading ? (
                <ActivityIndicator size="large" color="#999" style={{ marginTop: 40 }} />
            ) : (
                <FlatList
                    data={users}
                    keyExtractor={(item, index) => item?.userId?.toString() || index.toString()}
                    renderItem={renderUserCard}
                    contentContainerStyle={{ paddingBottom: 100 }}
                    ListEmptyComponent={<Text style={styles.emptyText}>검색 결과가 없습니다.</Text>}
                />
            )}
        </View>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#F8FAFC' },
    header: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingHorizontal: 20,
        paddingTop: 20,
        paddingBottom: 10,
        backgroundColor: '#F8FAFC',
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
    menuIcon: {
        width: 24,
        height: 24,
        resizeMode: 'contain',
    },
    dropdownMenu: {
        position: 'absolute',
        top: 70,
        right: 20,
        backgroundColor: '#FFFDF9',
        borderRadius: 8,
        paddingVertical: 8,
        paddingHorizontal: 16,
        elevation: 6,
        zIndex: 1000,
    },
    dropdownItem: {
        paddingVertical: 8,
        fontSize: 15,
        color: '#5C504A',
    },
    tabContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        backgroundColor: '#FFF',
        paddingVertical: 8,
        marginHorizontal: 16,
        marginBottom: 8,
        borderRadius: 12,
        elevation: 2,
    },
    tabButton: {
        paddingVertical: 6,
        paddingHorizontal: 16,
        borderRadius: 10,
    },
    tabActive: {
        backgroundColor: '#E5EAF3',
    },
    tabText: {
        fontSize: 14,
        color: '#555',
    },
    tabTextActive: {
        fontWeight: 'bold',
        color: '#1D3557',
    },
    searchBar: {
        flexDirection: 'row',
        backgroundColor: '#FFFFFF',
        borderRadius: 12,
        padding: 10,
        marginHorizontal: 16,
        marginBottom: 12,
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
    },
    card: {
        backgroundColor: '#FFFFFF',
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
    center: { flex: 1 },
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
    unblockBtn: {
        backgroundColor: '#6A5ACD',
        paddingHorizontal: 10,
        paddingVertical: 6,
        borderRadius: 8,
    },

});

export default FriendScreen;
