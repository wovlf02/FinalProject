import React, {useCallback, useEffect, useState} from 'react';
import {
    View, Text, StyleSheet, ScrollView, TextInput,
    TouchableOpacity, Image, Alert, Platform, FlatList, TouchableWithoutFeedback, Keyboard, Dimensions
} from 'react-native';
import moment from 'moment';
import 'moment/locale/ko';
import EncryptedStorage from 'react-native-encrypted-storage';
import { jwtDecode } from 'jwt-decode';
import { useRoute, useNavigation } from '@react-navigation/native';
import ImageViewing from 'react-native-image-viewing';
import RNFS from 'react-native-fs';
import { request, PERMISSIONS, RESULTS } from 'react-native-permissions';
import FastImage from 'react-native-fast-image';
import api from '../../api/api';

const BASE_URL = 'http://192.168.78.59:8080';
const {width: screenWidth, height: screenHeight} = Dimensions.get('window');
const PostDetailScreen = () => {
    const { postId } = useRoute().params;
    const navigation = useNavigation();

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
    const [popupVisible, setPopupVisible] = useState(false);


    useEffect(() => {
        fetchUser();
        increaseViewCount();
        fetchPost();
    }, [fetchPost, increaseViewCount]);

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


    const increaseViewCount = useCallback(async () => {
        try {
            await api.post(`/community/posts/${postId}/view`);
        } catch (err) {
            console.warn('조회수 증가 실패', err);
        }
    }, [postId]);

    const fetchPost = useCallback(async () => {
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
    }, [postId]);


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
                        navigation.goBack(); // 뒤로가기 추가
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
        setMenuVisible(null);
    };

    const handleReportReply = () => {
        Alert.alert('신고 완료', `대댓글이 신고되었습니다.`);
        setMenuVisible(null);

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
          await api.post(`/community/comments/${replyToCommentId}/replies`, {
            content: commentInput,
          });
        } else {
          await api.post(`/community/posts/${postId}/comments`, {
            content: commentInput,
          });
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
                <ScrollView contentContainerStyle={{...styles.scrollContent, minHeight: screenHeight * 1.2}}>
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
                                        navigation.navigate('PostEdit', { postId: post.id });
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
                                    <TouchableOpacity onPress={() => openEditModal('comment', comment.commentId, comment.content)}><Text style={styles.popupText}>수정</Text></TouchableOpacity>
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
                                            <TouchableOpacity onPress={() => openEditModal('reply', reply.replyId, reply.content)}><Text style={styles.popupText}>수정</Text></TouchableOpacity>
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
        paddingHorizontal: screenWidth * 0.04,
        paddingTop: screenHeight * 0.03,
        paddingBottom: screenHeight * 0.1,
        backgroundColor: '#F8FAFC', // 인트로 색상
    },
    postCard: {
        width: screenWidth * 0.96,
        alignSelf: 'center',
        backgroundColor: '#FFFFFF',
        borderRadius: 20,
        paddingVertical: screenHeight * 0.025,
        paddingHorizontal: screenWidth * 0.05,
        marginTop: screenHeight * 0.02, // 상단 간격
        marginBottom: screenHeight * 0.03,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 3 },
        shadowOpacity: 0.06,
        shadowRadius: 4,
        elevation: 3,
    },
    header: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: '4%',
    },
    avatar: {
        width: screenWidth * 0.12,
        height: screenWidth * 0.12,
        borderRadius: screenWidth * 0.06,
        marginRight: screenWidth * 0.03,
        borderWidth: 1,
        borderColor: '#DDD',
    },
    nickname: {
        fontWeight: 'bold',
        fontSize: 16,
        color: '#333',
    },
    date: {
        fontSize: 12,
        color: '#999',
        marginTop: 2,
    },
    moreButton: {
        marginLeft: 'auto',
        padding: 6,
    },
    moreText: {
        fontSize: 22,
        color: '#888',
        fontWeight: 'bold',
    },
    popupBox: {
        position: 'absolute',
        top: 30,
        right: 0,
        backgroundColor: '#FFFFFF',
        borderRadius: 8,
        paddingHorizontal: 12,
        paddingVertical: 6,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 5,
        elevation: 10,
        zIndex: 9999,
    },
    popupText: {
        color: '#333',
        fontWeight: '500',
        paddingVertical: 6,
    },
    postTitle: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#222',
        marginBottom: 8,
    },
    postContent: {
        fontSize: 15,
        color: '#444',
        lineHeight: 22,
        marginBottom: 14,
    },
    thumbnail: {
        width: screenWidth * 0.24,
        height: screenWidth * 0.24,
        borderRadius: 10,
        marginRight: screenWidth * 0.02,
        backgroundColor: '#EEE',
    },
    downloadButton: {
        position: 'absolute',
        bottom: 30,
        alignSelf: 'center',
        backgroundColor: '#000',
        paddingHorizontal: 20,
        paddingVertical: 10,
        borderRadius: 22,
    },
    downloadButtonText: {
        color: '#fff',
        fontSize: 15,
        fontWeight: 'bold',
    },
    metaRow: {
        flexDirection: 'row',
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: '4%',
        gap: 30,
    },
    infoText: {
        fontSize: 14,
        color: '#7A6E65',
    },
    liked: {
        color: '#E25D5D',
        fontWeight: 'bold',
    },
    commentBox: {
        width: screenWidth * 0.96,
        alignSelf: 'center',
        borderRadius: 16,
        backgroundColor: '#FFFFFF',
        padding: screenWidth * 0.04,
        marginBottom: screenHeight * 0.02,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.06,
        shadowRadius: 3,
        elevation: 2,
    },
    commentHeader: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: 4,
    },
    commentAvatar: {
        width: 34,
        height: 34,
        borderRadius: 17,
        marginRight: 10,
        borderWidth: 1,
        borderColor: '#EEE',
    },
    commentNickname: {
        fontWeight: '600',
        fontSize: 14,
        color: '#222',
    },
    commentTime: {
        fontSize: 12,
        color: '#AAA',
        marginLeft: 'auto',
        marginRight: 6,
    },
    commentMenu: {
        fontSize: 18,
        color: '#888',
    },
    commentText: {
        fontSize: 14,
        color: '#444',
        marginTop: 6,
    },
    commentFooter: {
        flexDirection: 'row',
        marginTop: 6,
        gap: 14,
    },
    commentAction: {
        fontSize: 13,
        color: '#555',
    },
    commentPopup: {
        position: 'absolute',
        top: 30,
        right: 10,
        backgroundColor: '#fff',
        borderRadius: 8,
        paddingVertical: 6,
        paddingHorizontal: 12,
        elevation: 10,
        borderColor: '#DDD',
        borderWidth: 1,
    },
    replyBox: {
        backgroundColor: '#F9F5F1',
        marginTop: screenHeight * 0.01,
        marginLeft: screenWidth * 0.1,
        borderRadius: 14,
        padding: screenWidth * 0.03,
        width: screenWidth * 0.8,
        alignSelf: 'flex-start',
    },
    replyHeader: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: 4,
    },
    replyAvatar: {
        width: 28,
        height: 28,
        borderRadius: 14,
        marginRight: 8,
        borderWidth: 1,
        borderColor: '#DDD',
    },
    replyNickname: {
        fontSize: 13,
        fontWeight: '600',
        color: '#333',
    },
    replyTime: {
        fontSize: 11,
        color: '#999',
        marginLeft: 'auto',
        marginRight: 6,
    },
    replyText: {
        fontSize: 13,
        color: '#444',
        marginTop: 4,
    },
    replyFooter: {
        flexDirection: 'row',
        marginTop: 4,
        gap: 12,
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
        height: screenHeight * 0.08, // 예: 약 64px
        backgroundColor: '#FFFFFF',
        borderTopWidth: 1,
        borderColor: '#E5DED6',
        paddingHorizontal: screenWidth * 0.04, // 예: 약 16px
        flexDirection: 'row',
        alignItems: 'center',
        shadowColor: '#000',
        shadowOffset: { width: 0, height: -2 },
        shadowOpacity: 0.04,
        shadowRadius: 4,
        elevation: 5,
    },
    commentInput: {
        flex: 1,
        height: 44,
        backgroundColor: '#FAFAFA',
        borderColor: '#DDD',
        borderWidth: 1,
        borderRadius: 22,
        paddingHorizontal: 16,
        fontSize: 14,
        color: '#333',
    },
    sendButton: {
        marginLeft: screenWidth * 0.02,
        fontSize: 18,
        color: '#fff',
        backgroundColor: '#000',
        paddingVertical: screenHeight * 0.012,
        paddingHorizontal: screenWidth * 0.04,
        borderRadius: 20,
        fontWeight: 'bold',
    },
    modalOverlay: {
        position: 'absolute',
        top: 0, left: 0, right: 0, bottom: 0,
        backgroundColor: 'rgba(0,0,0,0.5)',
        justifyContent: 'center',
        alignItems: 'center',
        zIndex: 9999,
    },
    modalContainer: {
        width: '80%',
        backgroundColor: '#fff',
        borderRadius: 12,
        padding: 20,
    },
    modalTitle: {
        fontSize: 17,
        fontWeight: 'bold',
        color: '#222',
        marginBottom: 10,
    },
    modalInput: {
        borderWidth: 1,
        borderColor: '#DDD',
        borderRadius: 8,
        padding: 10,
        minHeight: 80,
        textAlignVertical: 'top',
        color: '#333',
        backgroundColor: '#FDFDFD',
    },
    modalButtons: {
        flexDirection: 'row',
        justifyContent: 'flex-end',
        marginTop: 16,
        gap: 12,
    },
    modalButton: {
        fontSize: 16,
        fontWeight: 'bold',
        paddingHorizontal: 12,
        color: '#000',
    },
});
