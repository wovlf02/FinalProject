import React, { useState, useEffect } from 'react';
import {
    View, Text, StyleSheet, TextInput, FlatList, Image,
    TouchableOpacity, Pressable, ActivityIndicator, Alert, Dimensions
} from 'react-native';
import { useNavigation, useIsFocused } from '@react-navigation/native';
import EncryptedStorage from 'react-native-encrypted-storage';
import { jwtDecode } from 'jwt-decode';
import moment from 'moment';
import api from '../../api/api';

const { width: screenWidth, height: screenHeight } = Dimensions.get('window');

const PostListScreen = () => {
    const navigation = useNavigation();
    const isFocused = useIsFocused();

    const [searchQuery, setSearchQuery] = useState('');
    const [popupVisible, setPopupVisible] = useState(null);
    const [postsData, setPostsData] = useState([]);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [loading, setLoading] = useState(false);
    const [searchMode, setSearchMode] = useState(false);
    const [writerId, setWriterId] = useState(null);
    const [menuVisible, setMenuVisible] = useState(false);

    useEffect(() => {
        const fetchUserId = async () => {
            try {
                const token = await EncryptedStorage.getItem('accessToken');
                if (token) {
                    const decoded = jwtDecode(token);
                    setWriterId(Number(decoded.sub));
                }
            } catch (err) {
                console.warn('토큰 디코딩 실패:', err);
                setWriterId(null);
            }
        };
        fetchUserId();
    }, [isFocused]);

    useEffect(() => {
        if (isFocused) {
            fetchPosts(0, true);
        }
    }, [isFocused]);

    const fetchPosts = async (pageToLoad = 0, reset = false) => {
        try {
            setLoading(true);
            const response = await api.get('/community/posts', {
                params: { page: pageToLoad, size: 20 },
            });

            const newPosts = Array.isArray(response.data.posts) ? response.data.posts : [];
            const currentPage = response.data.currentPage ?? 0;
            const totalPages = response.data.totalPages ?? 0;

            setPostsData(prev => reset ? newPosts : [...prev, ...newPosts]);
            setHasMore(currentPage + 1 < totalPages);
            setPage(currentPage);
            setSearchMode(false);
        } catch (err) {
            console.error('게시글 목록 불러오기 실패:', err);
            setPostsData([]);
            setHasMore(false);
        } finally {
            setLoading(false);
        }
    };

    const handleMenuSelect = (screen) => {
        setMenuVisible(false);
        if (screen === 'Chat') navigation.navigate('ChatRoomList');
        if (screen === 'Friend') navigation.navigate('FriendList');
    };

    const handleSearch = async (keyword = searchQuery) => {
        const trimmed = keyword.trim();
        if (!trimmed) {
            fetchPosts(0, true);
            return;
        }
        try {
            setLoading(true);
            const response = await api.get('/community/posts/search', {
                params: { keyword: trimmed }
            });
            const newPosts = Array.isArray(response.data.posts) ? response.data.posts : [];
            setPostsData(newPosts);
            setHasMore(false);
            setSearchMode(true);
        } catch (error) {
            console.error('검색 실패:', error);
            setPostsData([]);
            setHasMore(false);
        } finally {
            setLoading(false);
        }
    };

    const handleLoadMore = () => {
        if (!searchMode && hasMore && !loading) {
            fetchPosts(page + 1);
        }
    };

    const handleReport = (postId) => {
        Alert.alert('신고 완료', '신고가 접수되었습니다.');
        setPopupVisible(null);
    };

    const renderPost = ({ item }) => (
        <TouchableOpacity
            style={styles.postCard}
            activeOpacity={1}
            onPress={() => navigation.navigate('PostDetail', { postId: item.postId })}
        >
            <View style={styles.postHeader}>
                <Text style={styles.postTitle} numberOfLines={1}>{item.title}</Text>
                <TouchableOpacity onPress={() => setPopupVisible(popupVisible === item.postId ? null : item.postId)}>
                    <Text style={styles.moreText}>⋯</Text>
                </TouchableOpacity>
            </View>

            <Text style={styles.postMeta}>
                {item.writerNickname} • {moment(item.createdAt).format('YYYY.MM.DD')}
            </Text>

            <Text style={styles.postContent} numberOfLines={2}>
                {item.content?.trim() || ' '}
            </Text>

            <View style={styles.infoRow}>
                <View style={{ flex: 1 }} />
                <View style={styles.rightInfo}>
                    <Text style={[styles.infoText, item.liked && styles.liked]}>
                        ❤️ {item.likeCount}
                    </Text>
                    <Text style={styles.infoText}>👁️ {item.viewCount}</Text>
                    <Text style={styles.infoText}>💬 {item.commentCount}</Text>
                </View>
            </View>

            {popupVisible === item.postId && (
                <Pressable style={styles.popupBox} onPress={() => handleReport(item.postId)}>
                    <Text style={styles.popupText}>신고</Text>
                </Pressable>
            )}
        </TouchableOpacity>
    );

    return (
        <View style={styles.container}>
            {/* 상단 헤더 */}
            <View style={styles.header}>
                <Text style={styles.headerTitle}>게시판</Text>
                <TouchableOpacity style={styles.menuButton} onPress={() => setMenuVisible(!menuVisible)}>
                    <Text style={styles.menuIcon}>☰</Text>
                </TouchableOpacity>
                {menuVisible && (
                    <View style={styles.dropdownMenu}>
                        <TouchableOpacity onPress={() => handleMenuSelect('Chat')}>
                            <Text style={styles.dropdownItem}>채팅</Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => handleMenuSelect('Friend')}>
                            <Text style={styles.dropdownItem}>친구</Text>
                        </TouchableOpacity>
                    </View>
                )}
            </View>

            {/* 검색창 */}
            <View style={styles.searchBar}>
                <TextInput
                    placeholder="검색어 입력"
                    placeholderTextColor="#9A8E84"
                    value={searchQuery}
                    onChangeText={(text) => {
                        setSearchQuery(text);
                        handleSearch(text);
                    }}
                    style={styles.searchInput}
                />
                <TouchableOpacity onPress={handleSearch}>
                    <Image source={require('../../assets/board_search.png')} style={styles.searchIcon} />
                </TouchableOpacity>
            </View>

            {/* 게시글 목록 */}
            {loading && postsData.length === 0 ? (
                <ActivityIndicator size="large" color="#A3775C" style={{ marginTop: screenHeight * 0.04 }} />
            ) : (
                <FlatList
                    data={postsData}
                    keyExtractor={(item) => item.postId?.toString()}
                    renderItem={renderPost}
                    onEndReached={handleLoadMore}
                    onEndReachedThreshold={0.4}
                    contentContainerStyle={{ paddingBottom: screenHeight * 0.12 }}
                    ListEmptyComponent={<Text style={styles.emptyText}>검색 결과가 없습니다.</Text>}
                />
            )}

            {/* 글쓰기 버튼 */}
            <TouchableOpacity
                style={styles.floatingButton}
                onPress={() => {
                    if (!writerId) {
                        Alert.alert('오류', '로그인이 필요합니다.');
                        return;
                    }
                    navigation.navigate('CreatePost', { writerId });
                }}
            >
                <Image source={require('../../assets/pencil.png')} style={styles.addIcon} />
            </TouchableOpacity>
        </View>
    );
};

export default PostListScreen;

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F8FAFC',
    },
    header: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        paddingTop: screenHeight * 0.025,
        paddingBottom: screenHeight * 0.015,
        position: 'relative',
    },
    headerTitle: {
        fontSize: screenWidth * 0.05,
        fontWeight: 'bold',
        color: '#382F2D',
    },
    menuButton: {
        position: 'absolute',
        right: screenWidth * 0.06,
        top: screenHeight * 0.025,
    },
    menuIcon: {
        fontSize: screenWidth * 0.055,
        color: '#000', // 검정색으로 변경
    },
    dropdownMenu: {
        position: 'absolute',
        top: screenHeight * 0.08,
        right: screenWidth * 0.04,
        backgroundColor: '#FFFFFF',
        borderRadius: 10,
        paddingVertical: 10,
        paddingHorizontal: 14,
        elevation: 6,
        zIndex: 1000,
    },
    dropdownItem: {
        fontSize: 14,
        color: '#444',
        paddingVertical: 6,
    },
    searchBar: {
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: '#FFFFFF',
        borderRadius: 12,
        paddingVertical: screenHeight * 0.015,
        paddingHorizontal: screenWidth * 0.04,
        margin: screenWidth * 0.04,
        elevation: 3,
    },
    searchInput: {
        flex: 1,
        fontSize: 15,
        color: '#3C3C3C',
        paddingHorizontal: 8,
    },
    searchIcon: {
        width: screenWidth * 0.06,
        height: screenWidth * 0.06,
    },
    postCard: {
        backgroundColor: '#FFFFFF',
        marginHorizontal: screenWidth * 0.04,
        marginBottom: screenHeight * 0.02,
        padding: screenWidth * 0.04,
        borderRadius: 16,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 3 },
        shadowOpacity: 0.06,
        shadowRadius: 4,
        elevation: 3,
    },
    postHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 6,
    },
    postTitle: {
        fontSize: 17,
        fontWeight: 'bold',
        color: '#382F2D',
        flex: 1,
    },
    moreText: {
        fontSize: 22,
        color: '#B8A69E',
        fontWeight: 'bold',
        marginLeft: 10,
    },
    postMeta: {
        fontSize: 12,
        color: '#999', // 회색으로 변경
        marginBottom: 6,
    },
    postContent: {
        fontSize: 14,
        color: '#5C504A',
        marginBottom: 10,
    },
    infoRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    rightInfo: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'flex-end',
        gap: 12,
    },
    infoText: {
        fontSize: 13,
        color: '#555',
    },
    liked: {
        color: 'red',
        fontWeight: 'bold',
    },
    popupBox: {
        position: 'absolute',
        top: 30,
        right: 10,
        backgroundColor: '#FFF',
        borderRadius: 8,
        paddingHorizontal: 12,
        paddingVertical: 6,
        elevation: 10,
        zIndex: 999,
    },
    popupText: {
        fontSize: 14,
        color: '#A3775C',
        fontWeight: 'bold',
    },
    floatingButton: {
        position: 'absolute',
        right: screenWidth * 0.06,
        bottom: screenHeight * 0.04,
        backgroundColor: '#333', // ✅ 회색 계열
        width: screenWidth * 0.15,
        height: screenWidth * 0.15,
        borderRadius: screenWidth * 0.075,
        justifyContent: 'center',
        alignItems: 'center',
        elevation: 10,
    },
    addIcon: {
        width: screenWidth * 0.08,
        height: screenWidth * 0.08,
    },
    emptyText: {
        textAlign: 'center',
        color: '#999',
        fontSize: 14,
        marginTop: 40,
    },
});