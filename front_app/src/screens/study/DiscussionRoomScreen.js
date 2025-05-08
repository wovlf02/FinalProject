import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, TextInput } from 'react-native';

const participants = [
  { id: 1, name: '나 (호스트)', time: '25:00', online: true },
  { id: 2, name: '참여자 1', time: '25:00', online: true },
  { id: 3, name: '참여자 2', time: '25:00', online: true },
  { id: 4, name: '참여자 3', time: '25:00', online: true },
];

export default function DiscussionRoomScreen({ room = { title: '토론방', topic: '지구 온난화가 미치는 영향과 이를 해결하기 위한 방안에 대해 토론해보세요.', participants: 4, total: 6 }, onExit }) {
  const [focusMode, setFocusMode] = useState(true);
  const [chat, setChat] = useState('');
  const [chatList, setChatList] = useState([]);

  const handleSend = () => {
    if (chat.trim()) {
      setChatList([...chatList, { id: Date.now(), text: chat }]);
      setChat('');
    }
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={{ paddingBottom: 30 }}>
      {/* 헤더 */}
      <View style={styles.header}>
        <TouchableOpacity onPress={onExit}>
          <Text style={styles.headerBack}>{'<'} </Text>
        </TouchableOpacity>
        <Text style={styles.headerTitle}>토론방</Text>
        <Text style={styles.headerCount}>{room.participants || 4}/{room.total || 6}명</Text>
      </View>

      {/* 토론 주제 */}
      <View style={styles.topicCard}>
        <Text style={styles.topicLabel}>토론 주제</Text>
        <View style={styles.topicBox}>
          <Text style={styles.topicText}>{room.topic}</Text>
        </View>
        <View style={styles.topicBottomRow}>
          <Text style={styles.timeLeft}>남은 시간: 15:00</Text>
          <TouchableOpacity style={styles.announceBtn}>
            <Text style={styles.announceBtnText}>정답 발표하기</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* 비디오 그리드 */}
      <View style={styles.videoGrid}>
        {[0, 1, 2, 3].map(idx => (
          <View key={idx} style={styles.videoBox}>
            <Text style={styles.videoLabel}>Video</Text>
            <View style={styles.videoUserRow}>
              <Text style={styles.videoName}>{participants[idx]?.name || `참여자 ${idx+1}`}</Text>
              <View style={styles.iconRow}>
                <Text style={styles.icon}>🎤</Text>
                <Text style={styles.icon}>🎥</Text>
              </View>
            </View>
          </View>
        ))}
      </View>

      {/* 타이머/컨트롤 */}
      <View style={styles.timerRow}>
        <Text style={styles.timer}>25:00</Text>
        <TouchableOpacity style={styles.timerStartBtn}>
          <Text style={styles.timerStartText}>시작</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.iconBtn}><Text style={styles.icon}>👥</Text></TouchableOpacity>
        <TouchableOpacity style={styles.iconBtn}><Text style={styles.icon}>⚙️</Text></TouchableOpacity>
      </View>
      <View style={styles.modeRow}>
        <TouchableOpacity
          style={[styles.modeBtn, focusMode && styles.modeBtnActive]}
          onPress={() => setFocusMode(true)}
        >
          <Text style={[styles.modeBtnText, focusMode && styles.modeBtnTextActive]}>집중 모드</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.modeBtn, !focusMode && styles.modeBtnActive]}
          onPress={() => setFocusMode(false)}
        >
          <Text style={[styles.modeBtnText, !focusMode && styles.modeBtnTextActive]}>휴식 모드</Text>
        </TouchableOpacity>
      </View>

      {/* 참여자 리스트 */}
      <View style={styles.participantCard}>
        <Text style={styles.participantTitle}>참여자 ({participants.length}/6)</Text>
        {participants.map(p => (
          <View key={p.id} style={styles.participantRow}>
            <View style={styles.dot} />
            <Text style={styles.participantName}>{p.name}</Text>
            <Text style={styles.participantTime}>{p.time}</Text>
          </View>
        ))}
      </View>

      {/* 실시간 채팅 */}
      <View style={styles.chatCard}>
        <Text style={styles.chatTitle}>실시간 채팅</Text>
        <View style={styles.chatBox}>
          <ScrollView style={{ flex: 1 }}>
            {chatList.map(msg => (
              <Text key={msg.id} style={styles.chatMsg}>{msg.text}</Text>
            ))}
          </ScrollView>
        </View>
        <View style={styles.chatInputRow}>
          <TextInput
            style={styles.chatInput}
            value={chat}
            onChangeText={setChat}
            placeholder="메시지를 입력하세요"
            returnKeyType="send"
            onSubmitEditing={handleSend}
          />
          <TouchableOpacity style={styles.sendBtn} onPress={handleSend}>
            <Text style={styles.sendBtnIcon}>➤</Text>
          </TouchableOpacity>
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#FAFAFA', paddingHorizontal: 12 },
  header: { flexDirection: 'row', alignItems: 'center', paddingVertical: 16, marginBottom: 6 },
  headerBack: { fontSize: 20, color: '#333', marginRight: 8 },
  headerTitle: { fontSize: 18, fontWeight: 'bold', flex: 1 },
  headerCount: { fontSize: 15, color: '#666', fontWeight: 'bold' },
  topicCard: { backgroundColor: '#fff', borderRadius: 10, padding: 16, marginBottom: 12 },
  topicLabel: { fontSize: 15, fontWeight: 'bold', marginBottom: 8 },
  topicBox: { backgroundColor: '#F5F7FA', borderRadius: 8, padding: 12, marginBottom: 10 },
  topicText: { fontSize: 14, color: '#333' },
  topicBottomRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  timeLeft: { color: '#888', fontSize: 13 },
  announceBtn: { backgroundColor: '#111', borderRadius: 6, paddingVertical: 6, paddingHorizontal: 16 },
  announceBtnText: { color: '#fff', fontWeight: 'bold', fontSize: 14 },
  videoGrid: { flexDirection: 'row', flexWrap: 'wrap', justifyContent: 'space-between', marginBottom: 14 },
  videoBox: { width: '48%', aspectRatio: 1.2, backgroundColor: '#ddd', borderRadius: 10, marginBottom: 10, justifyContent: 'space-between', padding: 8 },
  videoLabel: { color: '#888', fontSize: 13, marginBottom: 6 },
  videoUserRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  videoName: { fontWeight: 'bold', color: '#222', fontSize: 14 },
  iconRow: { flexDirection: 'row' },
  icon: { fontSize: 16, marginLeft: 6 },
  timerRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 6 },
  timer: { fontSize: 22, fontWeight: 'bold', marginRight: 12 },
  timerStartBtn: { backgroundColor: '#111', borderRadius: 6, paddingVertical: 7, paddingHorizontal: 18, marginRight: 8 },
  timerStartText: { color: '#fff', fontWeight: 'bold', fontSize: 15 },
  iconBtn: { backgroundColor: '#F5F7FA', borderRadius: 6, padding: 7, marginHorizontal: 3 },
  modeRow: { flexDirection: 'row', marginBottom: 10 },
  modeBtn: { flex: 1, paddingVertical: 9, alignItems: 'center', borderRadius: 6, backgroundColor: '#F5F7FA', marginHorizontal: 2 },
  modeBtnActive: { backgroundColor: '#111' },
  modeBtnText: { color: '#888', fontWeight: 'bold', fontSize: 15 },
  modeBtnTextActive: { color: '#fff' },
  participantCard: { backgroundColor: '#fff', borderRadius: 10, padding: 14, marginBottom: 12 },
  participantTitle: { fontWeight: 'bold', fontSize: 15, marginBottom: 8 },
  participantRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 4 },
  dot: { width: 8, height: 8, borderRadius: 4, backgroundColor: '#22C55E', marginRight: 7 },
  participantName: { flex: 1, color: '#222', fontSize: 14 },
  participantTime: { color: '#888', fontSize: 13 },
  chatCard: { backgroundColor: '#fff', borderRadius: 10, padding: 14, marginBottom: 14 },
  chatTitle: { fontWeight: 'bold', fontSize: 15, marginBottom: 8 },
  chatBox: { backgroundColor: '#F5F7FA', borderRadius: 8, minHeight: 80, maxHeight: 120, padding: 8, marginBottom: 8 },
  chatMsg: { fontSize: 14, color: '#333', marginBottom: 3 },
  chatInputRow: { flexDirection: 'row', alignItems: 'center' },
  chatInput: { flex: 1, borderWidth: 1, borderColor: '#DDD', borderRadius: 8, padding: 10, fontSize: 15, backgroundColor: '#F8FAFC' },
  sendBtn: { marginLeft: 6, backgroundColor: '#111', borderRadius: 8, padding: 10 },
  sendBtnIcon: { color: '#fff', fontSize: 17, fontWeight: 'bold' },
});
