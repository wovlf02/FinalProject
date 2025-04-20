import React, { useState, useEffect } from 'react';
import { View, Text, TextInput, StyleSheet, TouchableOpacity, Alert } from 'react-native';
import { Camera, useCameraDevices } from 'react-native-vision-camera';
import { Picker } from '@react-native-picker/picker';

const STUDY_TIMES = [10, 20, 30, 40, 50, 60];

const PersonalStudyScreen = () => {
  const [unit, setUnit] = useState('');
  const [studyTime, setStudyTime] = useState(30);
  const [hasPermission, setHasPermission] = useState(false);
  const devices = useCameraDevices();
  const device = devices?.back;

  useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'authorized');
    })();
  }, []);

  if (!hasPermission) {
    return <Text>카메라 권한이 필요합니다.</Text>;
  }

  if (!device) {
    return <Text>카메라를 사용할 수 없습니다.</Text>;
  }

  return (
    <View style={styles.wrapper}>
      <Text style={styles.header}>개인 학습 설정</Text>
      <Text style={styles.label}>단원명</Text>
      <TextInput
        style={styles.input}
        placeholder="예: 수학 - 미적분"
        value={unit}
        onChangeText={setUnit}
      />
      <Text style={styles.label}>학습 시간 설정</Text>
      <View style={styles.pickerWrapper}>
        <Picker
          selectedValue={studyTime}
          style={styles.picker}
          onValueChange={(itemValue) => setStudyTime(itemValue)}
        >
          {STUDY_TIMES.map((time) => (
            <Picker.Item key={time} label={`${time}분`} value={time} />
          ))}
        </Picker>
      </View>
      <View style={styles.cameraSection}>
        <Text style={styles.cameraLabel}>테스트 캡</Text>
        <Camera
          style={styles.camera}
          device={device}
          isActive={true}
          photo={true}
        />
        <TouchableOpacity
          style={styles.captureButton}
          onPress={() => Alert.alert('사진 찍기 기능은 추가 구현이 필요합니다.')}
        >
          <Text style={styles.buttonText}>사진 찍기</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  wrapper: {
    flex: 1,
    padding: 24,
    backgroundColor: '#fff',
  },
  header: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 18,
  },
  label: {
    fontSize: 15,
    marginTop: 10,
    marginBottom: 6,
  },
  input: {
    borderWidth: 1,
    borderColor: '#bbb',
    borderRadius: 6,
    padding: 10,
    fontSize: 15,
    marginBottom: 10,
  },
  pickerWrapper: {
    borderWidth: 1,
    borderColor: '#bbb',
    borderRadius: 6,
    marginBottom: 16,
  },
  picker: {
    height: 40,
    width: '100%',
  },
  cameraSection: {
    alignItems: 'center',
    marginVertical: 16,
  },
  cameraLabel: {
    fontSize: 14,
    marginBottom: 4,
    color: '#555',
  },
  camera: {
    width: 200,
    height: 120,
    borderRadius: 8,
    backgroundColor: '#eee',
  },
  captureButton: {
    position: 'absolute',
    bottom: 20,
    alignSelf: 'center',
    backgroundColor: '#fff',
    padding: 10,
    borderRadius: 5,
  },
  buttonText: {
    color: '#000',
  },
});

export default PersonalStudyScreen;
