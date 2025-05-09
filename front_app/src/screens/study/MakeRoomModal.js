import React, { useState } from 'react';
import { View, Text, Modal, StyleSheet, TouchableOpacity, TextInput } from 'react-native';

export default function MakeRoomModal({ visible, category, onClose, onCreate }) {
  // 공통
  const [title, setTitle] = useState('');
  const [status, setStatus] = useState('공개');
  const [total, setTotal] = useState('4');
  // 캠스터디 전용
  const [goalTime, setGoalTime] = useState('25'); // 분
  // 토론 전용
  const [topic, setTopic] = useState('');

  const reset = () => {
    setTitle('');
    setStatus('공개');
    setTotal('4');
    setGoalTime('25');
    setTopic('');
  };

  const handleCreate = () => {
    if (!title.trim()) return;
    let room = {
      id: Date.now(),
      title,
      status,
      participants: 1,
      total: parseInt(total) || 4,
    };
    if (category === '캠스터디') {
      room.goalTime = parseInt(goalTime) || 25;
    } else {
      room.topic = topic;
    }
    onCreate(room);
    reset();
  };

  return (
    <Modal visible={visible} transparent animationType="fade" onRequestClose={onClose}>
      <View style={styles.modalBg}>
        <View style={styles.modalCard}>
          <Text style={styles.modalTitle}>{category} 방 만들기</Text>
          <TextInput
            style={styles.input}
            placeholder="방 이름"
            value={title}
            onChangeText={setTitle}
            maxLength={20}
          />
          <View style={styles.row}>
            <Text style={styles.label}>공개여부</Text>
            <TouchableOpacity
              style={[styles.radio, status === '공개' && styles.radioActive]}
              onPress={() => setStatus('공개')}
            >
              <Text style={status === '공개' ? styles.radioTextActive : styles.radioText}>공개</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.radio, status === '비공개' && styles.radioActive]}
              onPress={() => setStatus('비공개')}
            >
              <Text style={status === '비공개' ? styles.radioTextActive : styles.radioText}>비공개</Text>
            </TouchableOpacity>
          </View>
          <View style={styles.row}>
            <Text style={styles.label}>최대 인원</Text>
            <TextInput
              style={[styles.input, { width: 60, marginLeft: 8 }]}
              keyboardType="number-pad"
              value={total}
              onChangeText={setTotal}
              maxLength={2}
            />
            <Text style={{ marginLeft: 4 }}>명</Text>
          </View>
          {category === '캠스터디' && (
            <View style={styles.row}>
              <Text style={styles.label}>목표 시간</Text>
              <TextInput
                style={[styles.input, { width: 60, marginLeft: 8 }]}
                keyboardType="number-pad"
                value={goalTime}
                onChangeText={setGoalTime}
                maxLength={3}
              />
              <Text style={{ marginLeft: 4 }}>분</Text>
            </View>
          )}
          {category === '토론' && (
            <View style={styles.row}>
              <Text style={styles.label}>토론 주제</Text>
              <TextInput
                style={[styles.input, { flex: 1, marginLeft: 8 }]}
                value={topic}
                onChangeText={setTopic}
                maxLength={20}
                placeholder="예: 수학의 미래"
              />
            </View>
          )}
          <View style={{ flexDirection: 'row', marginTop: 18 }}>
            <TouchableOpacity
              style={[styles.createBtn, { flex: 1, marginRight: 6 }]}
              onPress={handleCreate}
            >
              <Text style={styles.createBtnText}>만들기</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.cancelBtn, { flex: 1, marginLeft: 6 }]}
              onPress={() => { onClose(); reset(); }}
            >
              <Text style={styles.cancelBtnText}>취소</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  modalBg: { flex: 1, backgroundColor: 'rgba(0,0,0,0.18)', justifyContent: 'center', alignItems: 'center' },
  modalCard: { backgroundColor: '#fff', borderRadius: 14, padding: 22, width: '85%' },
  modalTitle: { fontSize: 18, fontWeight: 'bold', textAlign: 'center', marginBottom: 16 },
  input: { borderWidth: 1, borderColor: '#DDD', borderRadius: 8, padding: 10, fontSize: 15, backgroundColor: '#F8FAFC', marginTop: 8 },
  row: { flexDirection: 'row', alignItems: 'center', marginTop: 14 },
  label: { fontSize: 15, width: 70, color: '#333' },
  radio: { borderWidth: 1, borderColor: '#DDD', borderRadius: 8, paddingVertical: 6, paddingHorizontal: 12, marginLeft: 10 },
  radioActive: { backgroundColor: '#007AFF', borderColor: '#007AFF' },
  radioText: { color: '#333' },
  radioTextActive: { color: '#fff', fontWeight: 'bold' },
  createBtn: { backgroundColor: '#007AFF', borderRadius: 8, paddingVertical: 12, alignItems: 'center' },
  createBtnText: { color: '#fff', fontWeight: 'bold', fontSize: 15 },
  cancelBtn: { backgroundColor: '#eee', borderRadius: 8, paddingVertical: 12, alignItems: 'center' },
  cancelBtnText: { color: '#333', fontWeight: 'bold', fontSize: 15 },
});
