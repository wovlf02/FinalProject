import React, { useEffect, useState } from 'react';
import {
    View, Text, StyleSheet, FlatList,
    TouchableOpacity, Alert, ActivityIndicator,
} from 'react-native';
import api from '../../api/api';
import FastImage from 'react-native-fast-image';

const BASE_URL = 'http://192.168.0.2:8080';

const FriendBlockListScreen = () => {
    const [blockedUsers, setBlockedUsers] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        fetchBlockedUsers();
    }, []);

    const fetchBlockedUsers = async () => {
        try {
            setLoading(true);
            const res = await api.get('/friends/blocked');
            setBlockedUsers(res.data.blockedUsers || []);
        } catch (err) {
            Alert.alert('오류', '차단된 사용자 목록을 불러올 수 없습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleUnblock = (userId) => {
        Alert.alert(
            '차단 해제',
            '정말 이 사용자의 차단을 해제하시겠습니까?',
            [
                { text: '취소', style: 'cancel' },
                {
                    text: '해제',
                    onPress: async () => {
                        try {
                            await api.delete(`/friends/block/${userId}`);
                            Alert.alert('해제 완료', '차단을 해제했습니다.');
                            fetchBlockedUsers();
                        } catch (err) {
                            Alert.alert('실패', err.response?.data?.message || '차단 해제 실패');
                        }
                    }
                }
            ]
        );
    };

    const renderBlockedUser = ({ item }) => (
        <View style={styles.card}>
            <FastImage
                source={{ uri: BASE_URL + item.profileImageUrl }}
                style={styles.avatar}
            />
            <Text style={styles.nickname}>{item.nickname}</Text>
            <TouchableOpacity
                style={styles.unblockButton}
                onPress={() => handleUnblock(item.userId)}
            >
                <Text style={styles.unblockText}>차단 해제</Text>
            </TouchableOpacity>
        </View>
    );

    return (
        <View style={styles.container}>
            <Text style={styles.header}>차단된 사용자 목록</Text>

            {loading ? (
                <ActivityIndicator size="large" color="#A3775C" style={{ marginTop: 40 }} />
            ) : (
                <FlatList
                    data={blockedUsers}
                    keyExtractor={(item) => item.userId.toString()}
                    renderItem={renderBlockedUser}
                    ListEmptyComponent={
                        <Text style={styles.emptyText}>차단된 사용자가 없습니다.</Text>
                    }
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
        paddingTop: 20,
    },
    header: {
        fontSize: 20,
        fontWeight: 'bold',
        textAlign: 'center',
        marginBottom: 12,
        color: '#382F2D',
    },
    card: {
        backgroundColor: '#FFFDF9',
        flexDirection: 'row',
        alignItems: 'center',
        marginHorizontal: 16,
        marginVertical: 8,
        padding: 14,
        borderRadius: 14,
        elevation: 2,
    },
    avatar: {
        width: 48,
        height: 48,
        borderRadius: 24,
        marginRight: 12,
        backgroundColor: '#EEE',
    },
    nickname: {
        flex: 1,
        fontSize: 16,
        fontWeight: 'bold',
        color: '#382F2D',
    },
    unblockButton: {
        backgroundColor: '#A3775C',
        borderRadius: 8,
        paddingHorizontal: 12,
        paddingVertical: 6,
    },
    unblockText: {
        color: '#FFF',
        fontWeight: 'bold',
        fontSize: 13,
    },
    emptyText: {
        textAlign: 'center',
        marginTop: 40,
        fontSize: 14,
        color: '#999',
    },
});

export default FriendBlockListScreen;
