import React, { useEffect, useState } from 'react';
import {
    View, Text, TextInput, TouchableOpacity, Image,
    ScrollView, StyleSheet, Alert,
} from 'react-native';
import * as ImagePicker from 'react-native-image-picker';
import api from '../../api/api';

const BASE_URL = 'http://192.168.0.2:8080';

const PostEditScreen = ({ route, navigation }) => {
    const { postId } = route.params;

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [existingImages, setExistingImages] = useState([]); // {id, uri}
    const [deleteFileIds, setDeleteFileIds] = useState([]);
    const [newImages, setNewImages] = useState([]);

    useEffect(() => {
        fetchPost();
    }, []);

    const fetchPost = async () => {
        try {
            const res = await api.get(`/community/posts/${postId}`);
            setTitle(res.data.title);
            setContent(res.data.content);

            const attachRes = await api.get(`/community/posts/${postId}/attachments`);
            const formatted = attachRes.data.attachments.map((a) => ({
                id: a.attachmentId,
                uri: BASE_URL + '/uploads/community/' + a.storedName,
            }));
            setExistingImages(formatted);
        } catch (err) {
            Alert.alert('오류', '게시글 정보를 불러올 수 없습니다.');
        }
    };

    const handlePickImage = () => {
        ImagePicker.launchImageLibrary({ mediaType: 'photo', selectionLimit: 0 }, response => {
            if (!response.didCancel && !response.errorCode && response.assets) {
                setNewImages([...newImages, ...response.assets]);
            }
        });
    };

    const handleDeleteExistingImage = (id) => {
        setExistingImages(existingImages.filter(img => img.id !== id));
        setDeleteFileIds([...deleteFileIds, id]);
    };

    const handleDeleteNewImage = (uri) => {
        setNewImages(newImages.filter(img => img.uri !== uri));
    };

    const handleSubmit = async () => {
        if (!title.trim() || !content.trim()) {
            Alert.alert('알림', '제목과 내용을 입력하세요.');
            return;
        }

        const formData = new FormData();
        formData.append('post', JSON.stringify({ title, content, deleteFileIds }));

        newImages.forEach((img, idx) => {
            formData.append('files', {
                uri: img.uri,
                type: img.type || 'image/jpeg',
                name: img.fileName || `image_${idx}.jpg`,
            });
        });

        try {
            await api.put(`/community/posts/${postId}`, formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            Alert.alert('성공', '게시글이 수정되었습니다.');
            navigation.goBack();
        } catch (err) {
            Alert.alert('오류', '게시글 수정 실패');
        }
    };

    return (
        <ScrollView contentContainerStyle={styles.container}>
            <Text style={styles.label}>제목</Text>
            <TextInput
                style={styles.input}
                value={title}
                onChangeText={setTitle}
            />

            <Text style={styles.label}>내용</Text>
            <TextInput
                style={[styles.input, { height: 120 }]}
                multiline
                value={content}
                onChangeText={setContent}
            />

            <Text style={styles.label}>기존 이미지</Text>
            <ScrollView horizontal>
                {existingImages.map(img => (
                    <View key={img.id} style={styles.imageContainer}>
                        <Image source={{ uri: img.uri }} style={styles.image} />
                        <TouchableOpacity onPress={() => handleDeleteExistingImage(img.id)}>
                            <Text style={styles.remove}>X</Text>
                        </TouchableOpacity>
                    </View>
                ))}
            </ScrollView>

            <Text style={styles.label}>추가 이미지</Text>
            <ScrollView horizontal>
                {newImages.map(img => (
                    <View key={img.uri} style={styles.imageContainer}>
                        <Image source={{ uri: img.uri }} style={styles.image} />
                        <TouchableOpacity onPress={() => handleDeleteNewImage(img.uri)}>
                            <Text style={styles.remove}>X</Text>
                        </TouchableOpacity>
                    </View>
                ))}
            </ScrollView>

            <TouchableOpacity style={styles.button} onPress={handlePickImage}>
                <Text style={styles.buttonText}>📷 이미지 추가</Text>
            </TouchableOpacity>

            <TouchableOpacity style={[styles.button, { backgroundColor: '#4CAF50' }]} onPress={handleSubmit}>
                <Text style={styles.buttonText}>✅ 저장</Text>
            </TouchableOpacity>
        </ScrollView>
    );
};

export default PostEditScreen;

const styles = StyleSheet.create({
    container: {
        padding: 16,
        backgroundColor: '#FFFDF9'
    },
    label: {
        fontSize: 16,
        fontWeight: 'bold',
        marginTop: 12,
        color: '#333',
    },
    input: {
        borderWidth: 1,
        borderColor: '#DDD',
        padding: 10,
        borderRadius: 8,
        backgroundColor: '#fff',
        marginTop: 6,
    },
    imageContainer: {
        position: 'relative',
        marginRight: 10,
        marginTop: 10,
    },
    image: {
        width: 90,
        height: 90,
        borderRadius: 10,
    },
    remove: {
        position: 'absolute',
        top: 2,
        right: 4,
        backgroundColor: '#f44',
        color: '#fff',
        borderRadius: 10,
        paddingHorizontal: 6,
        fontWeight: 'bold',
    },
    button: {
        marginTop: 20,
        backgroundColor: '#A3775C',
        padding: 14,
        borderRadius: 10,
        alignItems: 'center'
    },
    buttonText: {
        color: '#fff',
        fontWeight: '600',
        fontSize: 16
    },
});
