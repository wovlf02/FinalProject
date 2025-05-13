import React, { useEffect, useState } from 'react';
import {
    View, Text, StyleSheet, ScrollView, TextInput,
    TouchableOpacity, Image, Alert, Platform, FlatList, Pressable, TouchableWithoutFeedback, Keyboard
} from 'react-native';
import moment from 'moment';
import 'moment/locale/ko';
import EncryptedStorage from 'react-native-encrypted-storage';
import { jwtDecode } from 'jwt-decode';
import { useRoute } from '@react-navigation/native';
import ImageViewing from 'react-native-image-viewing';
import RNFS from 'react-native-fs';
import { request, PERMISSIONS, RESULTS } from 'react-native-permissions';
import FastImage from 'react-native-fast-image';
import api from '../../api/api';

const BASE_URL = 'http://192.168.0.2:8080';

const PostDetailScreen = () => {
    const { postId } = useRoute().params;

    const [post, setPost] = useState(null);
    const [writerProfileImageUrl, setWriterProfileImageUrl] = useState('');
    const [loading, setLoading] = useState(true);
    const [isImageViewerVisible, setImageViewerVisible] = useState(false);
    const [selectedImageIndex, setSelectedImageIndex] = useState(0);
    const [imageViewerImages, setImageViewerImages] = useState([]);
    const [viewerKey, setViewerKey] = useState(0);

    const [commentInput, setCommentInput] = useState('');
    const [comments, setComments] = useState([]);
    const [userId, setUserId] = useState(null);

    const [replyToCommentId, setReplyToCommentId] = useState(null);
    const [menuVisible, setMenuVisible] = useState(null);

    const [reportModalVisible, setReportModalVisible] = useState(false);
    const [reportTarget, setReportTarget] = useState({ type: '', id: null });
    const [reportReason, setReportReason] = useState('');

    const [editModalVisible, setEditModalVisible] = useState(false);
    const [editContent, setEditContent] = useState('');
    const [editTarget, setEditTarget] = useState({ type: '', id: null });


    useEffect(() => {
        fetchUser();
        increaseViewCount();
        fetchPost();
    }, []);

    const openReportModal = (type, id) => {
        setReportTarget({ type, id });
        setReportReason('');
        setReportModalVisible(true);
    };

    const openEditModal = (type, id, oldContent) => {
        setEditTarget({ type, id });
        setEditContent(oldContent);
        setEditModalVisible(true);
        setMenuVisible(null); // 팝업 닫기
    };

    const submitEdit = async () => {
        try {
            if (!editContent.trim()) {
                Alert.alert('입력 오류', '내용을 입력해주세요.');
                return;
            }

            const { type, id } = editTarget;

            if (type === 'comment') {
                await api.put(`/community/comments/${id}/update`, {
                    content: editContent,
                });
            } else if (type === 'reply') {
                await api.put(`/community/comments/${id}/update`, {
                    content: editContent,
                });
            }

            setEditModalVisible(false);
            setEditTarget({ type: '', id: null });
            setEditContent('');
            fetchPost();
        } catch (err) {
            Alert.alert('오류', '수정에 실패했습니다.');
        }
    };

    const fetchUser = async () => {
        try {
            const token = await EncryptedStorage.getItem('accessToken');
            if (!token) {
                Alert.alert('인증 오류', '로그인 정보가 없습니다.');
                return;
            }
            const decoded = jwtDecode(token);
            setUserId(Number(decoded.sub));
        } catch (err) {
            Alert.alert('인증 오류', '사용자 정보를 불러오지 못했습니다.');
        }
    };

    const handleReplyPress = (commentOrReplyId, parentCommentId = null) => {
        // parentCommentId가 있으면 그걸 저장
        if (replyToCommentId === (parentCommentId || commentOrReplyId)) {
            setReplyToCommentId(null);
            setCommentInput('');
        } else {
            setReplyToCommentId(parentCommentId || commentOrReplyId);
            setCommentInput('');
        }
    };


    const increaseViewCount = async () => {
        try {
            await api.post(`/community/posts/${postId}/view`);
        } catch (err) {
            console.warn('조회수 증가 실패', err);
        }
    };

    const fetchPost = async () => {
        try {
            const res = await api.get(`/community/posts/${postId}`);
            setPost(res.data);

            const userRes = await api.get(`/users/${res.data.writerId}`);
            const profilePath = userRes.data.profileImageUrl?.startsWith('/')
                ? userRes.data.profileImageUrl
                : '/profile/' + userRes.data.profileImageUrl;
            setWriterProfileImageUrl(`${BASE_URL}${profilePath}`);

            const imageList = res.data.attachmentUrls?.map((url) => ({
                uri: `${BASE_URL}${url}?t=${Date.now()}`
            })) || [];
            setImageViewerImages(imageList);

            const commentRes = await api.get(`/community/posts/${postId}/comments`);
            setComments(commentRes.data.comments || []);
        } catch (err) {
            Alert.alert('오류', '게시글 정보를 불러오는 데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleDownloadImage = async (url) => {
        try {
            if (Platform.OS === 'android') {
                const permission = await request(
                    Platform.Version >= 33
                        ? PERMISSIONS.ANDROID.READ_MEDIA_IMAGES
                        : PERMISSIONS.ANDROID.WRITE_EXTERNAL_STORAGE
                );
                if (permission !== RESULTS.GRANTED) {
                    Alert.alert('권한 필요', '사진 저장 권한을 허용해주세요.');
                    return;
                }
            }

            const timestamp = Date.now();
            const originalName = url.split('/').pop().split('?')[0];
            const filename = `${timestamp}_${originalName}`;
            const dest = Platform.OS === 'android'
                ? `${RNFS.DownloadDirectoryPath}/${filename}`
                : `${RNFS.DocumentDirectoryPath}/${filename}`;

            await RNFS.mkdir(RNFS.DownloadDirectoryPath);
            if (await RNFS.exists(dest)) await RNFS.unlink(dest);

            const result = await RNFS.downloadFile({ fromUrl: url, toFile: dest }).promise;

            if (result.statusCode === 200) {
                if (Platform.OS === 'android') await RNFS.scanFile(dest);
                Alert.alert('✅ 다운로드 완료', `사진이 저장되었습니다.\n\n경로:\n${dest}`);
            } else {
                throw new Error(`다운로드 실패: 상태 코드 ${result.statusCode}`);
            }
        } catch (err) {
            Alert.alert('오류', '사진 다운로드 중 문제가 발생했습니다.');
        }
    };

    const handleLikePost = async () => {
        try {
            await api.post(`/community/likes/posts/${postId}`);  // 서버에서 toggle 처리
            fetchPost(); // 다시 불러와서 상태 반영
        } catch (err) {
            Alert.alert('오류', '게시글 좋아요 처리 실패');
        }
    };

    const handleLikeComment = async (commentId) => {
        try {
            await api.post(`/community/likes/comments/${commentId}`);
            fetchPost();
        } catch (err) {
            Alert.alert('오류', '댓글 좋아요 처리 실패');
        }
    };

    const handleLikeReply = async (replyId) => {
        try {
            await api.post(`/community/likes/replies/${replyId}`);
            fetchPost();
        } catch (err) {
            Alert.alert('오류', '대댓글 좋아요 처리 실패');
        }
    };

    // ==== 수정 요청 ====

    const handleEditPost = () => {
        Alert.prompt('게시글 수정', '새로운 내용을 입력하세요.', [
            {
                text: '취소',
                style: 'cancel',
            },
            {
                text: '수정',
                onPress: async (newContent) => {
                    try {
                        const formData = new FormData();

                        // 1. JSON 문자열 구성
                        const postUpdate = {
                            title: post.title,
                            content: newContent,
                            deleteFileIds: [], // 삭제할 첨부파일 ID가 있다면 여기에 추가
                        };
                        formData.append('post', JSON.stringify(postUpdate));

                        // 2. 파일이 있다면 함께 추가 (선택)
                        // 예: selectedFiles.forEach((file) => formData.append('files', file));

                        await api.put(`/community/posts/${postId}`, formData, {
                            headers: {
                                'Content-Type': 'multipart/form-data',
                            },
                        });

                        setPopupVisible(false);
                        fetchPost();
                    } catch (err) {
                        console.error(err);
                        Alert.alert('오류', '게시글 수정 실패');
                    }
                },
            },
        ]);
    };


    const handleDeletePost = async () => {
        Alert.alert('게시글 삭제', '정말 삭제하시겠습니까?', [
            { text: '취소', style: 'cancel' },
            {
                text: '삭제',
                style: 'destructive',
                onPress: async () => {
                    try {
                        await api.delete(`/community/posts/${postId}`);
                        Alert.alert('삭제 완료', '게시글이 삭제되었습니다.');
                        // TODO: 뒤로 가기 또는 목록 이동 필요
                    } catch (err) {
                        Alert.alert('오류', '게시글 삭제 실패');
                    }
                },
            },
        ]);
    };

    const handleDeleteComment = (commentId) => {
        Alert.alert('댓글 삭제', '정말 삭제하시겠습니까?', [
            { text: '취소', style: 'cancel' },
            {
                text: '삭제',
                style: 'destructive',
                onPress: async () => {
                    try {
                        await api.delete(`/community/comments/${commentId}/delete`);
                        fetchPost();
                    } catch (err) {
                        Alert.alert('오류', '댓글 삭제 실패');
                    }
                },
            },
        ]);
    };

    const handleDeleteReply = (replyId) => {
        Alert.alert('대댓글 삭제', '정말 삭제하시겠습니까?', [
            { text: '취소', style: 'cancel' },
            {
                text: '삭제',
                style: 'destructive',
                onPress: async () => {
                    try {
                        await api.delete(`/community/comments/${replyId}/delete`);
                        fetchPost();
                    } catch (err) {
                        Alert.alert('오류', '대댓글 삭제 실패');
                    }
                },
            },
        ]);
    };



    const handleReportComment = () => {
        Alert.alert('신고 완료', `댓글이 신고되었습니다.`);
        setMenuVisibleCommentId(null);
    };

    const handleReportReply = () => {
        Alert.alert('신고 완료', `대댓글이 신고되었습니다.`);
        setMenuVisibleReplyId(null);
    };

    const handleReport = () => {
        Alert.alert('신고 완료', '게시글이 신고되었습니다.');
        setPopupVisible(false);
    };

    const handleStartChat = (targetUserId) => {
        Alert.alert('채팅 시작', `사용자(${targetUserId})와 채팅을 시작합니다.`);
    };

    const submitReport = async () => {
        const { type, id } = reportTarget;
        if (!reportReason.trim()) {
            Alert.alert('입력 오류', '신고 사유를 입력해주세요.');
            return;
        }

        try {
            await api.post(`/community/${type}s/${id}/report`, {
                reporterId: userId,
                reason: reportReason,
            });
            Alert.alert('신고 완료', `${type === 'post' ? '게시글' : type === 'comment' ? '댓글' : '대댓글'}이 신고되었습니다.`);
        } catch (err) {
            Alert.alert('오류', '신고 처리 중 문제가 발생했습니다.');
        } finally {
            setReportModalVisible(false);
            setMenuVisible(null);
        }
    };



    const handleAction = async (type, action, target) => {
        switch (type) {
            case 'post':
                if (action === 'edit') handleEditPost(); // 게시글 수정은 추후 커스텀화 가능
                else if (action === 'delete') handleDeletePost();
                else if (action === 'report') handleReport();
                else if (action === 'chat') handleStartChat(post.writerId);
                break;
            case 'comment':
                if (action === 'edit') openEditModal('comment', target.commentId, target.content);
                else if (action === 'delete') handleDeleteComment(target.commentId);
                else if (action === 'report') handleReportComment();
                else if (action === 'chat') handleStartChat(target.writerId);
                break;
            case 'reply':
                if (action === 'edit') openEditModal('reply', target.replyId, target.content);
                else if (action === 'delete') handleDeleteReply(target.replyId);
                else if (action === 'report') handleReportReply();
                else if (action === 'chat') handleStartChat(target.writerId);
                break;
        }
        setMenuVisible(null);
    };



    const handleSubmitComment = async () => {
        if (!commentInput.trim()) return;

        try {
            if (replyToCommentId) {
                await api.post(`/community/comments/${replyToCommentId}/replies`, { content: commentInput });
            } else {
                await api.post(`/community/posts/${postId}/comments`, { content: commentInput });
            }

            setCommentInput('');
            setReplyToCommentId(null);
            fetchPost();
        } catch (err) {
            Alert.alert('오류', '댓글 또는 대댓글 등록 실패');
        }
    };





    if (loading || !post) return <Text style={{ padding: 20 }}>로딩 중...</Text>;

    return (
        <TouchableWithoutFeedback onPress={() => {
            setMenuVisible(null);
            Keyboard.dismiss();
        }}>
            <View style={{ flex: 1, backgroundColor: '#F4F1EC' }}>
                <ScrollView contentContainerStyle={styles.scrollContent}>
                    {/* ===== 게시글 카드 ===== */}
                    <View style={styles.postCard}>
                        <View style={styles.header}>
                            <Image source={{ uri: writerProfileImageUrl }} style={styles.avatar} />
                            <View>
                                <Text style={styles.nickname}>{post.writerNickname}</Text>
                                <Text style={styles.date}>{moment(post.createdAt).format('YYYY년 M월 D일 H:mm')}</Text>
                            </View>
                            <TouchableOpacity style={styles.moreButton} onPress={() => setMenuVisible({ type: 'post', id: post.id })}>
                                <Text style={styles.moreText}>⋯</Text>
                            </TouchableOpacity>
                            {menuVisible?.type === 'post' && menuVisible?.id === post.id && (
                                <View style={[styles.popupBox, { zIndex: 1000 }]}>
                                    <TouchableOpacity onPress={() => {
                                        setMenuVisible(null); // 팝업 닫기
                                        navigation.navigate('PostEditScreen', { postId: post.id });
                                    }}>
                                        <Text style={styles.popupText}>수정</Text>
                                    </TouchableOpacity>
                                    <TouchableOpacity onPress={handleDeletePost}><Text style={styles.popupText}>삭제</Text></TouchableOpacity>
                                    <TouchableOpacity onPress={() => openReportModal('post', post.id)}><Text style={styles.popupText}>신고</Text></TouchableOpacity>
                                    <TouchableOpacity onPress={() => handleStartChat(post.writerId)}><Text style={styles.popupText}>채팅 시작</Text></TouchableOpacity>
                                </View>
                            )}
                        </View>

                        <Text style={styles.postTitle}>{post.title}</Text>
                        <Text style={styles.postContent}>{post.content}</Text>

                        {imageViewerImages.length > 0 && (
                            <FlatList
                                data={imageViewerImages}
                                keyExtractor={(_, idx) => idx.toString()}
                                horizontal
                                renderItem={({ item, index }) => (
                                    <TouchableOpacity
                                        onPress={() => {
                                            setViewerKey(Date.now());
                                            setSelectedImageIndex(index);
                                            setImageViewerVisible(true);
                                        }}
                                    >
                                        <FastImage source={{ uri: item.uri }} style={styles.thumbnail} />
                                    </TouchableOpacity>
                                )}
                                showsHorizontalScrollIndicator={false}
                                style={{ marginTop: 10 }}
                            />
                        )}

                        <ImageViewing
                            key={viewerKey}
                            images={imageViewerImages}
                            imageIndex={selectedImageIndex}
                            visible={isImageViewerVisible}
                            onRequestClose={() => setImageViewerVisible(false)}
                            FooterComponent={({ imageIndex }) => (
                                <TouchableOpacity
                                    onPress={() => handleDownloadImage(imageViewerImages[imageIndex].uri)}
                                    style={styles.downloadButton}
                                >
                                    <Text style={styles.downloadButtonText}>📥 다운로드</Text>
                                </TouchableOpacity>
                            )}
                        />

                        <View style={styles.metaRow}>
                            <TouchableOpacity onPress={handleLikePost}>
                                <Text style={[styles.infoText, post.liked && styles.liked]}>
                                    ❤️ {post.likeCount}
                                </Text>
                            </TouchableOpacity>
                            <Text style={styles.infoText}>👁️ {post.viewCount}</Text>
                            <Text style={styles.infoText}>
                                💬 {comments.reduce((sum, c) => sum + 1 + (c.replies?.length || 0), 0)}
                            </Text>
                        </View>
                    </View>

                    {/* ===== 댓글 + 대댓글 ===== */}
                    {comments.map((comment) => (
                        <View key={comment.commentId} style={[styles.commentBox, replyToCommentId === comment.commentId && styles.highlightedBox]}>
                            <View style={styles.commentHeader}>
                                <Image source={{ uri: BASE_URL + comment.profileImageUrl }} style={styles.commentAvatar} />
                                <View style={{ flex: 1 }}>
                                    <Text style={styles.commentNickname}>{comment.writerNickname}</Text>
                                </View>
                                <Text style={styles.commentTime}>{moment(comment.createdAt).fromNow()}</Text>
                                <TouchableOpacity onPress={() => setMenuVisible({ type: 'comment', id: comment.commentId })}>
                                    <Text style={styles.commentMenu}>⋯</Text>
                                </TouchableOpacity>
                            </View>
                            <Text style={styles.commentText}>{comment.content}</Text>
                            <View style={styles.commentFooter}>
                                <TouchableOpacity onPress={() => handleLikeComment(comment.commentId)}>
                                    <Text style={styles.commentAction}>❤️ {comment.likeCount}</Text>
                                </TouchableOpacity>
                                <TouchableOpacity onPress={() => handleReplyPress(comment.commentId)}>
                                    <Text style={styles.commentAction}>Reply</Text>
                                </TouchableOpacity>
                            </View>

                            {menuVisible?.type === 'comment' && menuVisible?.id === comment.commentId && (
                                <View style={[styles.commentPopup, { zIndex: 1000 }]}>
                                    <TouchableOpacity onPress={() => handleEditComment(comment.commentId, comment.content)}><Text style={styles.popupText}>수정</Text></TouchableOpacity>
                                    <TouchableOpacity onPress={() => handleDeleteComment(comment.commentId)}><Text style={styles.popupText}>삭제</Text></TouchableOpacity>
                                    <TouchableOpacity onPress={() => openReportModal('comment', comment.commentId)}><Text style={styles.popupText}>신고</Text></TouchableOpacity>
                                    <TouchableOpacity onPress={() => handleStartChat(comment.writerId)}><Text style={styles.popupText}>채팅 시작</Text></TouchableOpacity>
                                </View>
                            )}

                            {comment.replies.map(reply => (
                                <View key={reply.replyId} style={[styles.replyBox, replyToCommentId === reply.replyId && styles.highlightedBox]}>
                                    <View style={styles.replyHeader}>
                                        <Image source={{ uri: BASE_URL + reply.profileImageUrl }} style={styles.replyAvatar} />
                                        <Text style={styles.replyNickname}>{reply.writerNickname}</Text>
                                        <Text style={styles.replyTime}>{moment(reply.createdAt).fromNow()}</Text>
                                        <TouchableOpacity onPress={() => setMenuVisible({ type: 'reply', id: reply.replyId })}>
                                            <Text style={styles.commentMenu}>⋯</Text>
                                        </TouchableOpacity>
                                    </View>
                                    <Text style={styles.replyText}>{reply.content}</Text>
                                    <View style={styles.replyFooter}>
                                        <TouchableOpacity onPress={() => handleLikeReply(reply.replyId)}>
                                            <Text style={styles.commentAction}>❤️ {reply.likeCount}</Text>
                                        </TouchableOpacity>
                                        <TouchableOpacity onPress={() => handleReplyPress(comment.commentId)}>
                                            <Text style={styles.commentAction}>Reply</Text>
                                        </TouchableOpacity>
                                    </View>

                                    {menuVisible?.type === 'reply' && menuVisible?.id === reply.replyId && (
                                        <View style={[styles.commentPopup, { zIndex: 1000 }]}>
                                            <TouchableOpacity onPress={() => handleEditReply(reply)}><Text style={styles.popupText}>수정</Text></TouchableOpacity>
                                            <TouchableOpacity onPress={() => handleDeleteReply(reply.replyId)}><Text style={styles.popupText}>삭제</Text></TouchableOpacity>
                                            <TouchableOpacity onPress={() => openReportModal('reply', reply.replyId)}><Text style={styles.popupText}>신고</Text></TouchableOpacity>
                                            <TouchableOpacity onPress={() => handleStartChat(reply.writerId)}><Text style={styles.popupText}>채팅 시작</Text></TouchableOpacity>
                                        </View>
                                    )}
                                </View>
                            ))}
                        </View>
                    ))}
                </ScrollView>

                {/* 댓글 입력창 */}
                <View style={styles.commentInputContainer}>
                    <TextInput
                        placeholder={replyToCommentId ? '대댓글을 입력하세요...' : '댓글을 입력하세요...'}
                        style={styles.commentInput}
                        placeholderTextColor="#aaa"
                        value={commentInput}
                        onChangeText={setCommentInput}
                    />
                    <TouchableOpacity onPress={handleSubmitComment}>
                        <Text style={styles.sendButton}>➤</Text>
                    </TouchableOpacity>
                </View>

                {/* ===== 신고 팝업 ===== */}
                {reportModalVisible && (
                    <View style={styles.modalOverlay}>
                        <View style={styles.modalContainer}>
                            <Text style={styles.modalTitle}>신고 사유</Text>
                            <TextInput
                                style={styles.modalInput}
                                placeholder="신고 사유를 입력하세요"
                                value={reportReason}
                                multiline
                                onChangeText={setReportReason}
                            />
                            <View style={styles.modalButtons}>
                                <TouchableOpacity onPress={() => setReportModalVisible(false)}>
                                    <Text style={styles.modalButton}>취소</Text>
                                </TouchableOpacity>
                                <TouchableOpacity onPress={submitReport}>
                                    <Text style={[styles.modalButton, { color: 'red' }]}>신고</Text>
                                </TouchableOpacity>
                            </View>
                        </View>
                    </View>
                )}

                {/* ===== 댓글/대댓글 수정 팝업 ===== */}
                {editModalVisible && (
                    <View style={styles.modalOverlay}>
                        <View style={styles.modalContainer}>
                            <Text style={styles.modalTitle}>내용 수정</Text>
                            <TextInput
                                style={styles.modalInput}
                                placeholder="수정할 내용을 입력하세요"
                                value={editContent}
                                multiline
                                onChangeText={setEditContent}
                            />
                            <View style={styles.modalButtons}>
                                <TouchableOpacity onPress={() => setEditModalVisible(false)}>
                                    <Text style={styles.modalButton}>취소</Text>
                                </TouchableOpacity>
                                <TouchableOpacity onPress={submitEdit}>
                                    <Text style={[styles.modalButton, { color: '#A3775C' }]}>수정</Text>
                                </TouchableOpacity>
                            </View>
                        </View>
                    </View>
                )}
            </View>
        </TouchableWithoutFeedback>
    );
};

export default PostDetailScreen;

const styles = StyleSheet.create({
    scrollContent: {
        padding: 16,
        paddingBottom: 100,
    },
    postCard: {
        borderRadius: 20,
        backgroundColor: '#FFFDF9',
        padding: 20,
        marginBottom: 26,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 4 },
        shadowOpacity: 0.05,
        shadowRadius: 6,
        elevation: 4,
    },
    header: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: 14,
    },
    avatar: {
        width: 46,
        height: 46,
        borderRadius: 23,
        marginRight: 14,
        borderWidth: 1,
        borderColor: '#E6DAD1',
    },
    nickname: {
        fontWeight: '800',
        fontSize: 17,
        color: '#4E3F36',
    },
    date: {
        fontSize: 12,
        color: '#9A8E84',
        marginTop: 3,
    },
    moreButton: {
        marginLeft: 'auto',
        padding: 6,
    },
    moreText: {
        fontSize: 22,
        color: '#B8A69E',
        fontWeight: 'bold',
    },
    popupBox: {
        position: 'absolute',
        top: 30,
        right: 0,
        backgroundColor: '#FFF',
        borderRadius: 8,
        paddingHorizontal: 12,
        paddingVertical: 6,
        elevation: 20,
        zIndex: 9999,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.15,
        shadowRadius: 5,
    },
    popupText: {
        color: '#333',
        fontWeight: '500',
        paddingVertical: 6,
    },
    postTitle: {
        fontSize: 24,
        fontWeight: 'bold',
        marginBottom: 10,
        color: '#382F2D',
    },
    postContent: {
        fontSize: 16,
        color: '#5C504A',
        marginBottom: 14,
        lineHeight: 25,
    },
    thumbnail: {
        width: 90,
        height: 90,
        borderRadius: 14,
        marginRight: 10,
        backgroundColor: '#EFE8E2',
    },
    downloadButton: {
        position: 'absolute',
        bottom: 30,
        left: '25%',
        backgroundColor: 'rgba(0,0,0,0.6)',
        paddingHorizontal: 20,
        paddingVertical: 10,
        borderRadius: 20,
    },
    downloadButtonText: {
        color: '#fff',
        fontSize: 16,
        fontWeight: '600',
    },
    metaRow: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        borderTopWidth: 1,
        borderColor: '#EEE0D5',
        paddingTop: 12,
        marginTop: 14,
    },
    infoText: {
        fontSize: 14,
        color: '#555',
    },
    liked: {
        fontWeight: 'bold',
        color: 'red',
    },
    commentBox: {
        backgroundColor: '#fff',
        borderRadius: 14,
        padding: 12,
        marginBottom: 20,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.05,
        shadowRadius: 3,
        elevation: 2,
        position: 'relative',
        zIndex: 1,
    },
    commentHeader: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: 6,
    },
    commentAvatar: {
        width: 36,
        height: 36,
        borderRadius: 18,
        marginRight: 10,
        borderWidth: 1,
        borderColor: '#eee',
    },
    commentNickname: {
        fontWeight: '600',
        fontSize: 14,
        color: '#333',
    },
    commentTime: {
        fontSize: 12,
        color: '#888',
        marginRight: 8,
    },
    commentMenu: {
        fontSize: 20,
        paddingHorizontal: 6,
        color: '#888',
    },
    commentText: {
        fontSize: 15,
        color: '#444',
        marginVertical: 6
    },
    commentFooter: {
        flexDirection: 'row',
        gap: 12,
    },
    commentAction: {
        fontSize: 13,
        color: '#666',
    },
    commentPopup: {
        position: 'absolute',
        top: 30,
        right: 10,
        backgroundColor: '#fff',
        borderRadius: 8,
        paddingVertical: 6,
        paddingHorizontal: 12,
        elevation: 20,
        zIndex: 9999,
        borderColor: '#DDD',
        borderWidth: 1,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.15,
        shadowRadius: 5,
    },
    replyBox: {
        backgroundColor: '#F7F6F3',
        marginTop: 10,
        marginLeft: 40,
        borderRadius: 12,
        padding: 10,
        position: 'relative',
        alignSelf: 'stretch',
    },
    replyHeader: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: 4,
    },
    replyAvatar: {
        width: 30,
        height: 30,
        borderRadius: 15,
        marginRight: 10,
        borderWidth: 1,
        borderColor: '#ddd',
    },
    replyNickname: {
        fontSize: 14,
        fontWeight: '600',
        color: '#333',
    },
    replyTime: {
        fontSize: 12,
        color: '#999',
        marginLeft: 'auto',
        marginRight: 4,
    },
    replyText: {
        fontSize: 14,
        color: '#444',
        marginVertical: 6,
        marginLeft: 4,
    },
    replyFooter: {
        flexDirection: 'row',
        gap: 12,
        marginLeft: 4,
    },
    highlightedBox: {
        borderWidth: 2,
        borderColor: '#A3775C',
    },
    commentInputContainer: {
        position: 'absolute',
        bottom: 0,
        left: 0,
        right: 0,
        height: 64,
        paddingHorizontal: 16,
        backgroundColor: '#FDF9F4',
        borderTopWidth: 1,
        borderColor: '#EDE3DA',
        flexDirection: 'row',
        alignItems: 'center',
        shadowColor: '#000',
        shadowOffset: { width: 0, height: -2 },
        shadowOpacity: 0.06,
        shadowRadius: 4,
        elevation: 10,
    },
    commentInput: {
        flex: 1,
        height: 44,
        borderWidth: 1,
        borderColor: '#D7CCC8',
        borderRadius: 22,
        paddingHorizontal: 16,
        backgroundColor: '#FFFFFF',
        fontSize: 15,
        color: '#3C3C3C',
    },
    sendButton: {
        marginLeft: 10,
        fontSize: 20,
        color: '#FFFFFF',
        backgroundColor: '#A3775C',
        paddingVertical: 10,
        paddingHorizontal: 16,
        borderRadius: 20,
        fontWeight: 'bold',
    },

    // === 신고 모달 ===
    modalOverlay: {
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0,0,0,0.5)',
        justifyContent: 'center',
        alignItems: 'center',
        zIndex: 99999,
    },
    modalContainer: {
        backgroundColor: 'white',
        borderRadius: 12,
        width: '80%',
        padding: 20,
    },
    modalTitle: {
        fontSize: 18,
        fontWeight: 'bold',
        marginBottom: 10,
        color: '#333',
    },
    modalInput: {
        borderWidth: 1,
        borderColor: '#ccc',
        borderRadius: 8,
        padding: 10,
        minHeight: 80,
        textAlignVertical: 'top',
        marginBottom: 16,
        color: '#333',
    },
    modalButtons: {
        flexDirection: 'row',
        justifyContent: 'flex-end',
        gap: 12,
    },
    modalButton: {
        fontSize: 16,
        fontWeight: 'bold',
        paddingHorizontal: 10,
    },
});
