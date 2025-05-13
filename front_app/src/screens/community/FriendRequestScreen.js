import React, { useEffect, useState } from 'react';
import {
    View, Text, StyleSheet, FlatList, TouchableOpacity,
    Image, Alert, ActivityIndicator
} from 'react-native';
import api from '../../api/api';
import FastImage from 'react-native-fast-image';

const BASE_URL = 'http://192.168.0.2:8080';

const FriendRequestScreen = () => {
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        fetchRequests();
    }, []);

    const fetchRequests = async () => {
        try {
            setLoading(true);
            const res = await api.get('/friends/requests');
            setRequests(res.data.requests || []);
        } catch (err) {
            Alert.alert('오류', '친구 요청 목록을 불러오지 못했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleAccept = async (requestId) => {
        try {
            await api.post('/friends/accept', { requestId });
            Alert.alert('친구 수락', '친구 요청을 수락했습니다.');
            fetchRequests();
        } catch (err) {
            Alert.alert('실패', err.response?.data?.message || '수락 실패');
        }
    };

    const handleReject = async (requestId) => {
        try {
            await api.post('/friends/reject', { requestId });
            Alert.alert('거절 완료', '친구 요청을 거절했습니다.');
            fetchRequests();
        } catch (err) {
            Alert.alert('실패', err.response?.data?.message || '거절 실패');
        }
    };

    const renderRequestCard = ({ item }) => (
        <View style={styles.card}>
            <FastImage
                source={{ uri: BASE_URL + item.profileImageUrl }}
                style={styles.avatar}
            />
            <View style={styles.userInfo}>
                <Text style={styles.nickname}>{item.nickname}</Text>
            </View>
            <View style={styles.buttons}>
                <TouchableOpacity style={styles.acceptBtn} onPress={() => handleAccept(item.requestId)}>
                    <Text style={styles.acceptText}>수락</Text>
                </TouchableOpacity>
                <TouchableOpacity style={styles.rejectBtn} onPress={() => handleReject(item.requestId)}>
                    <Text style={styles.rejectText}>거절</Text>
                </TouchableOpacity>
            </View>
        </View>
    );

    return (
        <View style={styles.container}>
            <Text style={styles.header}>받은 친구 요청</Text>

            {loading ? (
                <ActivityIndicator size="large" color="#A3775C" style={{ marginTop: 40 }} />
            ) : (
                <FlatList
                    data={requests}
                    keyExtractor={(item) => item.requestId.toString()}
                    renderItem={renderRequestCard}
                    ListEmptyComponent={<Text style={styles.emptyText}>받은 친구 요청이 없습니다.</Text>}
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
    userInfo: {
        flex: 1,
    },
    nickname: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#382F2D',
    },
    buttons: {
        flexDirection: 'row',
        gap: 10,
    },
    acceptBtn: {
        backgroundColor: '#A3775C',
        borderRadius: 8,
        paddingHorizontal: 10,
        paddingVertical: 6,
    },
    rejectBtn: {
        backgroundColor: '#DDD',
        borderRadius: 8,
        paddingHorizontal: 10,
        paddingVertical: 6,
    },
    acceptText: {
        color: '#FFF',
        fontWeight: 'bold',
    },
    rejectText: {
        color: '#444',
        fontWeight: 'bold',
    },
    emptyText: {
        textAlign: 'center',
        marginTop: 40,
        fontSize: 14,
        color: '#999',
    },
});

export default FriendRequestScreen;
