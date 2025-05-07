import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, Modal } from 'react-native';
import MakeRoomModal from './MakeRoomModal';
import CamStudyRoomScreen from './CamStudyRoomScreen';
import DiscussionRoomScreen from './DiscussionRoomScreen';

// 예시 데이터
const initialCamStudyRooms = [
  { id: 1, title: '수학 집중 스터디', status: '공개', participants: 3, total: 6 },
];
const initialDiscussionRooms = [
  { id: 1, title: '수학 문제 토론방', status: '공개', participants: 4, total: 8 },
];

export default function TeamStudyScreen() {
  // 탭 상태
  const [leftActiveTab, setLeftActiveTab] = useState('전체');
  const [rightActiveTab, setRightActiveTab] = useState('전체');
  // 방 목록
  const [camStudyRooms, setCamStudyRooms] = useState(initialCamStudyRooms);
  const [discussionRooms, setDiscussionRooms] = useState(initialDiscussionRooms);
  // 방 만들기 모달
  const [modalVisible, setModalVisible] = useState(false);
  const [makingCategory, setMakingCategory] = useState('캠스터디'); // '캠스터디' or '토론'
  // 입장중인 방
  const [enteredRoom, setEnteredRoom] = useState(null); // {category, room}

  // 방 만들기 버튼 클릭
  const openMakeRoomModal = (category) => {
    setMakingCategory(category);
    setModalVisible(true);
  };

  // 방 만들기 완료
  const handleCreateRoom = (room) => {
    if (makingCategory === '캠스터디') {
      setCamStudyRooms(prev => [...prev, room]);
      setEnteredRoom({ category: '캠스터디', room });
      setLeftActiveTab('전체');
    } else {
      setDiscussionRooms(prev => [...prev, room]);
      setEnteredRoom({ category: '토론', room });
      setRightActiveTab('전체');
    }
    setModalVisible(false);
  };

  // 방 목록 필터링
  const filterRooms = (rooms, tab) =>
    tab === '전체' ? rooms : rooms.filter(r => r.status === tab);

  // 방 카드
  const renderRoomCard = (room, category) => (
    <TouchableOpacity
      key={room.id}
      style={styles.roomCard}
      onPress={() => setEnteredRoom({ category, room })}
      activeOpacity={0.8}
    >
      <Text style={styles.roomTitle}>{room.title}</Text>
      <Text style={styles.participantsText}>참여인원: {room.participants}/{room.total}</Text>
      <Text style={styles.statusText}>{room.status}</Text>
    </TouchableOpacity>
  );

  // 방 입장 화면
  if (enteredRoom) {
    if (enteredRoom.category === '캠스터디') {
      return (
        <CamStudyRoomScreen
          room={enteredRoom.room}
          onExit={() => setEnteredRoom(null)}
        />
      );
    }
    if (enteredRoom.category === '토론') {
      return (
        <DiscussionRoomScreen
          room={enteredRoom.room}
          onExit={() => setEnteredRoom(null)}
        />
      );
    }
  }

  // 메인 화면
  return (
    <View style={styles.container}>
      <View style={styles.content}>
        {/* 캠스터디 */}
        <View style={styles.halfScreen}>
          <Text style={styles.categoryTitle}>📷 캠스터디</Text>
          <View style={styles.tabRow}>
            {['전체', '공개', '비공개'].map(tab => (
              <TouchableOpacity
                key={tab}
                style={[styles.tab, leftActiveTab === tab && styles.activeTab]}
                onPress={() => setLeftActiveTab(tab)}
              >
                <Text style={[styles.tabText, leftActiveTab === tab && styles.activeTabText]}>{tab}</Text>
              </TouchableOpacity>
            ))}
          </View>
          <ScrollView style={styles.roomList}>
            {filterRooms(camStudyRooms, leftActiveTab).map(r => renderRoomCard(r, '캠스터디'))}
          </ScrollView>
          <TouchableOpacity
            style={styles.createRoomButton}
            onPress={() => openMakeRoomModal('캠스터디')}
          >
            <Text style={styles.createRoomText}>방 만들기</Text>
          </TouchableOpacity>
        </View>
        {/* 토론 */}
        <View style={styles.halfScreen}>
          <Text style={styles.categoryTitle}>💬 토론</Text>
          <View style={styles.tabRow}>
            {['전체', '공개', '비공개'].map(tab => (
              <TouchableOpacity
                key={tab}
                style={[styles.tab, rightActiveTab === tab && styles.activeTab]}
                onPress={() => setRightActiveTab(tab)}
              >
                <Text style={[styles.tabText, rightActiveTab === tab && styles.activeTabText]}>{tab}</Text>
              </TouchableOpacity>
            ))}
          </View>
          <ScrollView style={styles.roomList}>
            {filterRooms(discussionRooms, rightActiveTab).map(r => renderRoomCard(r, '토론'))}
          </ScrollView>
          <TouchableOpacity
            style={styles.createRoomButton}
            onPress={() => openMakeRoomModal('토론')}
          >
            <Text style={styles.createRoomText}>방 만들기</Text>
          </TouchableOpacity>
        </View>
      </View>
      {/* 방 만들기 모달 */}
      <MakeRoomModal
        visible={modalVisible}
        category={makingCategory}
        onClose={() => setModalVisible(false)}
        onCreate={handleCreateRoom}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F8FAFC' },
  content: { flex: 1, flexDirection: 'row' },
  halfScreen: { flex: 1, borderRightWidth: 0.5, borderLeftWidth: 0.5, borderColor: '#ddd', padding: 8 },
  categoryTitle: { fontSize: 16, fontWeight: 'bold', marginBottom: 8 },
  tabRow: { flexDirection: 'row', marginBottom: 8 },
  tab: { flex: 1, alignItems: 'center', paddingVertical: 8, borderRadius: 8, backgroundColor: '#eee', marginHorizontal: 2 },
  activeTab: { backgroundColor: '#007AFF' },
  tabText: { color: '#333', fontWeight: 'bold' },
  activeTabText: { color: '#fff' },
  roomList: { flex: 1 },
  roomCard: { backgroundColor: '#fff', borderRadius: 10, padding: 10, marginBottom: 8, elevation: 1 },
  roomTitle: { fontSize: 15, fontWeight: 'bold' },
  participantsText: { fontSize: 13, color: '#555', marginTop: 2 },
  statusText: { fontSize: 12, color: '#888', marginTop: 2 },
  createRoomButton: { backgroundColor: '#007AFF', borderRadius: 8, paddingVertical: 12, alignItems: 'center', marginTop: 8 },
  createRoomText: { color: '#fff', fontWeight: 'bold', fontSize: 15 },
});
