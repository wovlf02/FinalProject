import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, Modal, TextInput, Alert } from 'react-native';

const scoreData = [
  { month: '1월', score: 70 },
  { month: '2월', score: 75 },
  { month: '3월', score: 80 },
  { month: '4월', score: 85 },
  { month: '5월', score: 90 },
];

const aiFeedback = {
  strengths: [
    "삼각함수의 기본 성질 이해도가 높음",
    "그래프 해석 능력이 우수함",
    "공식 활용 능력이 뛰어남"
  ],
  improvements: [
    "계산 과정에서 부호 실수가 많음",
    "적분 구간 설정에 주의 필요",
    "도함수 문제 풀이 시간이 오래 걸림"
  ],
  suggestions: [
    "기초 계산 연습 30분",
    "도함수 문제 타임어택 연습",
    "유사 문제 반복 학습"
  ]
};

const UnitTestScreen = () => {
  const [modalVisible, setModalVisible] = useState(false);
  const [goalModalVisible, setGoalModalVisible] = useState(false);
  const [goalScore, setGoalScore] = useState(85);
  const [goalInput, setGoalInput] = useState(goalScore.toString());
  const [alertModalVisible, setAlertModalVisible] = useState(false);

  // 그래프
  const maxScore = Math.max(...scoreData.map(d => d.score));
  const graphHeight = 120;

  // 평가 계속하기 버튼
  const handleStartTest = () => {
    setAlertModalVisible(true);
  };

  // 목표 수정 저장
  const handleSaveGoal = () => {
    const num = parseInt(goalInput, 10);
    if (isNaN(num) || num < 0 || num > 100) {
      Alert.alert('올바른 점수를 입력하세요 (0~100)');
      return;
    }
    setGoalScore(num);
    setGoalModalVisible(false);
  };

  return (
    <ScrollView style={styles.scroll} contentContainerStyle={styles.container}>
      {/* 진행 중인 평가 */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>진행 중인 평가</Text>
        <Text style={styles.unitTitle}>수학 II - 삼각함수의 덧셈정리</Text>
        <View style={styles.progressBarBg}>
          <View style={[styles.progressBar, { width: '60%' }]} />
        </View>
        <Text style={styles.progressText}>60% 완료 (마감까지 2일)</Text>
        <TouchableOpacity style={styles.primaryBtn} onPress={handleStartTest}>
          <Text style={styles.primaryBtnText}>평가 계속하기</Text>
        </TouchableOpacity>
      </View>

      {/* 목표 */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>이번 달 목표</Text>
        <View style={styles.goalRow}>
          <Text style={styles.goalText}>목표 점수</Text>
          <Text style={styles.goalValue}>{goalScore}점</Text>
        </View>
        <View style={styles.goalBarBg}>
          <View style={[styles.goalBar, { width: `${goalScore}%` }]} />
        </View>
        <TouchableOpacity style={styles.outlineBtn} onPress={() => setGoalModalVisible(true)}>
          <Text style={styles.outlineBtnText}>목표 수정</Text>
        </TouchableOpacity>
      </View>

      {/* 성적 그래프 */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>성적 추이</Text>
        <View style={styles.graphArea}>
          {scoreData.map((item, idx) => (
            <View key={idx} style={styles.barItem}>
              <View
                style={[
                  styles.bar,
                  {
                    height: (item.score / maxScore) * graphHeight,
                    backgroundColor: idx === scoreData.length - 1 ? '#007AFF' : '#B6D0FA',
                  }
                ]}
              />
              <Text style={styles.barLabel}>{item.month}</Text>
              <Text style={styles.barScore}>{item.score}</Text>
            </View>
          ))}
        </View>
        <TouchableOpacity style={styles.outlineBtn} onPress={() => setModalVisible(true)}>
          <Text style={styles.outlineBtnText}>상세 분석 보기</Text>
        </TouchableOpacity>
      </View>

      {/* AI 피드백 */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>AI 피드백</Text>
        <View style={styles.feedbackBox}>
          <Text style={styles.feedbackItem}>• 삼각함수 기본 개념 이해도 우수</Text>
          <Text style={styles.feedbackItem}>• 적분 계산 과정 실수 주의</Text>
          <Text style={styles.feedbackItem}>• 도함수 문제 풀이 시간 개선 필요</Text>
        </View>
        <TouchableOpacity style={styles.outlineBtn} onPress={() => setModalVisible(true)}>
          <Text style={styles.outlineBtnText}>상세 피드백 보기</Text>
        </TouchableOpacity>
      </View>

      {/* 상세 모달 */}
      <Modal
        visible={modalVisible}
        transparent
        animationType="fade"
        onRequestClose={() => setModalVisible(false)}
      >
        <View style={styles.modalBg}>
          <View style={styles.modalCard}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>AI 상세 피드백</Text>
              <TouchableOpacity onPress={() => setModalVisible(false)}>
                <Text style={{ fontSize: 22, color: '#888' }}>✕</Text>
              </TouchableOpacity>
            </View>
            <View style={styles.modalSection}>
              <Text style={styles.modalSectionTitle}>강점</Text>
              {aiFeedback.strengths.map((item, idx) => (
                <Text key={idx} style={styles.modalBullet}>• {item}</Text>
              ))}
            </View>
            <View style={styles.modalSection}>
              <Text style={styles.modalSectionTitle}>개선점</Text>
              {aiFeedback.improvements.map((item, idx) => (
                <Text key={idx} style={styles.modalBullet}>• {item}</Text>
              ))}
            </View>
            <View style={styles.modalSection}>
              <Text style={styles.modalSectionTitle}>학습 제안</Text>
              {aiFeedback.suggestions.map((item, idx) => (
                <Text key={idx} style={styles.modalBullet}>{idx + 1}. {item}</Text>
              ))}
            </View>
            <TouchableOpacity style={styles.primaryBtn} onPress={() => setModalVisible(false)}>
              <Text style={styles.primaryBtnText}>확인</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      {/* 목표 수정 모달 */}
      <Modal
        visible={goalModalVisible}
        transparent
        animationType="fade"
        onRequestClose={() => setGoalModalVisible(false)}
      >
        <View style={styles.modalBg}>
          <View style={styles.modalCard}>
            <Text style={styles.modalTitle}>목표 점수 수정</Text>
            <TextInput
              style={styles.input}
              keyboardType="number-pad"
              value={goalInput}
              onChangeText={setGoalInput}
              maxLength={3}
              placeholder="0~100"
            />
            <View style={{ flexDirection: 'row', marginTop: 18 }}>
              <TouchableOpacity style={[styles.primaryBtn, { flex: 1, marginRight: 6 }]} onPress={handleSaveGoal}>
                <Text style={styles.primaryBtnText}>저장</Text>
              </TouchableOpacity>
              <TouchableOpacity style={[styles.outlineBtn, { flex: 1, marginLeft: 6 }]} onPress={() => setGoalModalVisible(false)}>
                <Text style={styles.outlineBtnText}>취소</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* 평가 시작 안내 모달 */}
      <Modal
        visible={alertModalVisible}
        transparent
        animationType="fade"
        onRequestClose={() => setAlertModalVisible(false)}
      >
        <View style={styles.modalBg}>
          <View style={styles.modalCard}>
            <Text style={styles.modalTitle}>평가 시작 안내</Text>
            <Text style={{ fontSize: 15, color: '#333', marginVertical: 16, textAlign: 'center' }}>
              평가가 곧 시작됩니다!{'\n'}준비가 되셨나요?
            </Text>
            <TouchableOpacity style={styles.primaryBtn} onPress={() => setAlertModalVisible(false)}>
              <Text style={styles.primaryBtnText}>확인</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  scroll: { flex: 1, backgroundColor: '#F8FAFC' },
  container: { alignItems: 'center', padding: 16, paddingBottom: 40 },
  card: {
    width: '100%',
    backgroundColor: '#fff',
    borderRadius: 14,
    padding: 18,
    marginBottom: 18,
    elevation: 2,
    shadowColor: '#222',
    shadowOpacity: 0.05,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 6,
  },
  cardTitle: { fontSize: 16, fontWeight: 'bold', color: '#222', marginBottom: 8 },
  unitTitle: { fontSize: 15, color: '#333', marginBottom: 10 },
  progressBarBg: {
    width: '100%', height: 8, backgroundColor: '#F1F5F9', borderRadius: 5, marginBottom: 8,
  },
  progressBar: {
    height: 8, backgroundColor: '#007AFF', borderRadius: 5,
  },
  progressText: { color: '#666', fontSize: 13, marginBottom: 10 },
  primaryBtn: {
    backgroundColor: '#007AFF',
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
    marginTop: 8,
  },
  primaryBtnText: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
  outlineBtn: {
    borderWidth: 1, borderColor: '#007AFF', borderRadius: 8, paddingVertical: 10, alignItems: 'center', marginTop: 10,
  },
  outlineBtnText: { color: '#007AFF', fontWeight: 'bold', fontSize: 15 },
  goalRow: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 8 },
  goalText: { fontSize: 15, color: '#555' },
  goalValue: { fontSize: 15, color: '#007AFF', fontWeight: 'bold' },
  goalBarBg: {
    width: '100%', height: 8, backgroundColor: '#F1F5F9', borderRadius: 5, marginBottom: 8,
  },
  goalBar: {
    height: 8, backgroundColor: '#007AFF', borderRadius: 5,
  },
  graphArea: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-end',
    height: 140,
    marginVertical: 12,
    paddingHorizontal: 10,
  },
  barItem: {
    alignItems: 'center',
    justifyContent: 'flex-end',
    flex: 1,
  },
  bar: {
    width: 24,
    borderRadius: 6,
    marginBottom: 4,
  },
  barLabel: {
    fontSize: 12,
    color: '#666',
    marginTop: 2,
  },
  barScore: {
    fontSize: 12,
    color: '#333',
    marginTop: 1,
  },
  feedbackBox: {
    backgroundColor: '#F1F5F9',
    borderRadius: 8,
    padding: 12,
    marginBottom: 6,
  },
  feedbackItem: { fontSize: 14, color: '#333', marginBottom: 2 },
  modalBg: {
    flex: 1, backgroundColor: 'rgba(0,0,0,0.18)', justifyContent: 'center', alignItems: 'center',
  },
  modalCard: {
    backgroundColor: '#fff', borderRadius: 18, padding: 22, width: '90%',
    shadowColor: '#222', shadowOpacity: 0.09, shadowOffset: { width: 0, height: 5 }, shadowRadius: 10,
  },
  modalHeader: {
    flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8,
  },
  modalTitle: { fontSize: 18, fontWeight: 'bold', color: '#222' },
  modalSection: { marginTop: 10, marginBottom: 4 },
  modalSectionTitle: { fontWeight: 'bold', fontSize: 15, color: '#007AFF', marginBottom: 3 },
  modalBullet: { fontSize: 14, color: '#333', marginBottom: 2, marginLeft: 2 },
  input: {
    borderWidth: 1,
    borderColor: '#DDD',
    borderRadius: 8,
    padding: 10,
    fontSize: 16,
    marginTop: 18,
    backgroundColor: '#F8FAFC',
    textAlign: 'center',
  },
});

export default UnitTestScreen;
