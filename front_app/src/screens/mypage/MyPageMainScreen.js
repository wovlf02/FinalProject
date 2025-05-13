import React from 'react';
import { View, Text, StyleSheet, Image, TouchableOpacity, ScrollView } from 'react-native';
import { useUser } from '../../contexts/UserContext';

const MyPageMainScreen = () => {
  const { user } = useUser();

  // 예시 뱃지/통계 데이터
  const badges = [
    { id: 1, label: '연속 출석', value: '7일', icon: '🔥' },
    { id: 2, label: '누적 학습', value: '12시간', icon: '⏰' },
    { id: 3, label: '완료한 할 일', value: '21개', icon: '✅' },
  ];

  return (
    <ScrollView style={styles.scroll} contentContainerStyle={styles.container}>
      {/* 프로필 카드 */}
      <View style={styles.profileCard}>
        <Image source={{ uri: user.profileImage }} style={styles.profileImage} />
        <Text style={styles.name}>{user.name}</Text>
        <Text style={styles.email}>{user.email}</Text>
        <Text style={styles.greeting}>{user.greeting}</Text>
      </View>

      {/* 뱃지/통계 카드 */}
      <View style={styles.statsCard}>
        {badges.map(badge => (
          <View key={badge.id} style={styles.statItem}>
            <Text style={styles.statIcon}>{badge.icon}</Text>
            <Text style={styles.statValue}>{badge.value}</Text>
            <Text style={styles.statLabel}>{badge.label}</Text>
          </View>
        ))}
      </View>

      {/* 메뉴 카드 */}
      <View style={styles.menuCard}>
        <TouchableOpacity style={styles.menuItem}>
          <Text style={styles.menuText}>내 정보 수정</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.menuItem}>
          <Text style={styles.menuText}>비밀번호 변경</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.menuItem}>
          <Text style={[styles.menuText, { color: '#e74c3c' }]}>로그아웃</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  scroll: { flex: 1, backgroundColor: '#F8FAFC' },
  container: { alignItems: 'center', padding: 24, paddingBottom: 40 },
  profileCard: {
    width: '100%',
    backgroundColor: '#fff',
    borderRadius: 18,
    alignItems: 'center',
    paddingVertical: 28,
    marginBottom: 22,
    elevation: 3,
    shadowColor: '#222',
    shadowOpacity: 0.07,
    shadowOffset: { width: 0, height: 3 },
    shadowRadius: 8,
  },
  profileImage: {
    width: 92,
    height: 92,
    borderRadius: 46,
    marginBottom: 12,
    borderWidth: 2,
    borderColor: '#007AFF',
  },
  name: { fontSize: 22, fontWeight: 'bold', color: '#222', marginBottom: 2 },
  email: { fontSize: 15, color: '#555', marginBottom: 4 },
  greeting: { fontSize: 14, color: '#888', marginTop: 2 },
  statsCard: {
    width: '100%',
    backgroundColor: '#fff',
    borderRadius: 14,
    flexDirection: 'row',
    justifyContent: 'space-around',
    paddingVertical: 18,
    marginBottom: 22,
    elevation: 2,
    shadowColor: '#222',
    shadowOpacity: 0.05,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 6,
  },
  statItem: { alignItems: 'center', minWidth: 80 },
  statIcon: { fontSize: 26, marginBottom: 4 },
  statValue: { fontWeight: 'bold', fontSize: 17, color: '#007AFF' },
  statLabel: { fontSize: 13, color: '#555', marginTop: 2 },
  menuCard: {
    width: '100%',
    backgroundColor: '#fff',
    borderRadius: 14,
    paddingVertical: 8,
    elevation: 2,
    shadowColor: '#222',
    shadowOpacity: 0.04,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 5,
  },
  menuItem: {
    paddingVertical: 18,
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: '#F1F5F9',
  },
  menuText: { fontSize: 17, color: '#222', fontWeight: '500' },
});

export default MyPageMainScreen;
