import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';

export default function CamStudyRoomScreen({ room, onExit }) {
  // 예시: 4인 비디오 박스, 타이머, 버튼 등
  return (
    <View style={styles.container}>
      {/* 상단 */}
      <View style={styles.header}>
        <Text style={styles.title}>{room.title}</Text>
        <Text style={styles.people}>{room.participants}/{room.total}명</Text>
        <TouchableOpacity onPress={onExit}><Text style={styles.exit}>나가기</Text></TouchableOpacity>
      </View>
      {/* 비디오 박스 (가짜) */}
      <View style={styles.videoGrid}>
        {[1,2,3,4].map(idx => (
          <View key={idx} style={styles.videoBox}>
            <Text style={styles.videoName}>{idx === 1 ? '나(호스트)' : `참여자${idx}`}</Text>
            <Text style={styles.micCam}>🎤 🎥</Text>
          </View>
        ))}
      </View>
      {/* 하단 */}
      <View style={styles.bottomBar}>
        <Text style={styles.timer}>25:00</Text>
        <TouchableOpacity style={styles.startBtn}><Text style={styles.startBtnText}>시작</Text></TouchableOpacity>
        <TouchableOpacity style={styles.modeBtn}><Text style={styles.modeBtnText}>집중모드</Text></TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F8FAFC', padding: 12 },
  header: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 },
  title: { fontSize: 18, fontWeight: 'bold' },
  people: { fontSize: 14, color: '#007AFF' },
  exit: { color: '#e74c3c', fontSize: 15 },
  videoGrid: { flexDirection: 'row', flexWrap: 'wrap', justifyContent: 'space-between', marginTop: 16, marginBottom: 24 },
  videoBox: { width: '47%', aspectRatio: 1, backgroundColor: '#ddd', borderRadius: 12, alignItems: 'center', justifyContent: 'center', marginBottom: 12 },
  videoName: { fontWeight: 'bold', marginBottom: 8 },
  micCam: { fontSize: 18 },
  bottomBar: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-around', marginTop: 'auto', paddingVertical: 16 },
  timer: { fontSize: 22, fontWeight: 'bold', color: '#333' },
  startBtn: { backgroundColor: '#007AFF', borderRadius: 8, paddingHorizontal: 24, paddingVertical: 10 },
  startBtnText: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
  modeBtn: { backgroundColor: '#fff', borderRadius: 8, borderWidth: 1, borderColor: '#007AFF', paddingHorizontal: 16, paddingVertical: 10 },
  modeBtnText: { color: '#007AFF', fontWeight: 'bold', fontSize: 15 },
});
