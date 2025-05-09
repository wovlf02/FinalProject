import React, { useState } from 'react';
import { View, Text, TextInput, StyleSheet, TouchableOpacity, Image } from 'react-native';
import { Picker } from '@react-native-picker/picker';

const studyTimes = [10, 20, 30, 40, 50, 60];

const PersonalStudyScreen = () => {
  const [unit, setUnit] = useState('');
  const [studyTime, setStudyTime] = useState(30);

  return (
    <View style={styles.container}>
      <Text style={styles.header}>개인 학습 설정</Text>

      <Text style={styles.label}>단원명</Text>
      <TextInput
        style={styles.input}
        placeholder="예: 수학 - 미적분"
        value={unit}
        onChangeText={setUnit}
      />

      <Text style={styles.label}>학습 시간 설정</Text>
      <View style={styles.selectBox}>
        <Picker
          selectedValue={studyTime}
          style={styles.picker}
          onValueChange={(itemValue) => setStudyTime(itemValue)}
        >
          {studyTimes.map((time) => (
            <Picker.Item key={time} label={`${time}분`} value={time} />
          ))}
        </Picker>
      </View>

      <View style={styles.cameraBox}>
        <Text style={styles.cameraLabel}>테스트 캡</Text>
        <Image
          source={{ uri: 'https://dummyimage.com/200x110/cccccc/ffffff&text=Preview' }}
          style={styles.cameraImage}
          resizeMode="cover"
        />
      </View>

      <View style={styles.infoBox}>
        <Text style={styles.infoText}>
          <Text style={{ fontWeight: 'bold', color: '#f5a623' }}>ⓘ </Text>
          시작 전 카메라가 정상적으로 작동하는지 확인해주세요.
        </Text>
      </View>

      <View style={styles.warnBox}>
        <Text style={styles.warnText}>
          <Text style={{ fontWeight: 'bold', color: '#e53935' }}>⚠️ </Text>
          경고: 학습 시작 후에는 휴대폰 터치가 제한되며, 자리를 이탈하면 시간이 중단됩니다.{"\n"}
          집중 학습을 위해 학습 시간 동안 자리를 지켜주세요.
        </Text>
      </View>

      <TouchableOpacity style={styles.button}>
        <Text style={styles.buttonText}>학습 시작하기</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    padding: 24,
  },
  header: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 22,
  },
  label: {
    fontSize: 15,
    fontWeight: '500',
    marginTop: 12,
    marginBottom: 6,
  },
  input: {
    borderWidth: 1,
    borderColor: '#bbb',
    borderRadius: 6,
    padding: 12,
    fontSize: 15,
    marginBottom: 8,
  },
  selectBox: {
    borderWidth: 1,
    borderColor: '#bbb',
    borderRadius: 6,
    padding: 12,
    marginBottom: 18,
    justifyContent: 'center',
    height: 50, // 높이를 늘려줌
  },
  picker: {
    height: 50, // 높이를 늘려줌
    width: '100%',
  },
  cameraBox: {
    alignItems: 'center',
    marginBottom: 20,
  },
  cameraLabel: {
    backgroundColor: '#888',
    color: '#fff',
    width: 200,
    textAlign: 'center',
    paddingVertical: 4,
    borderTopLeftRadius: 8,
    borderTopRightRadius: 8,
    fontSize: 14,
  },
  cameraImage: {
    width: 200,
    height: 110,
    borderBottomLeftRadius: 8,
    borderBottomRightRadius: 8,
    backgroundColor: '#eee',
  },
  infoBox: {
    backgroundColor: '#FFF9E5',
    borderRadius: 6,
    padding: 12,
    marginBottom: 10,
  },
  infoText: {
    color: '#a67c00',
    fontSize: 13,
  },
  warnBox: {
    backgroundColor: '#FFE5E5',
    borderRadius: 6,
    padding: 12,
    marginBottom: 22,
  },
  warnText: {
    color: '#e53935',
    fontSize: 13,
  },
  button: {
    backgroundColor: '#111',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default PersonalStudyScreen;
