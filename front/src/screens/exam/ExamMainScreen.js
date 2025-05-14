import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, Alert } from 'react-native';
import api from '../../api/api';

const ExamMainScreen = () => {
    const [subjects, setSubjects] = useState([]);
    const [selectedSubject, setSelectedSubject] = useState(null);
    const [exams, setExams] = useState([]);

    // 과목 목록 가져오기
    useEffect(() => {
        // TODO: API 연동
        setSubjects(['수학', '영어', '국어', '과학', '사회']);
    }, []);

    // 과목 선택 시 해당 과목의 시험 목록 가져오기
    const handleSubjectSelect = async (subject) => {
        try {
            const response = await api.get(`/api/exam?subject=${subject}`);
            setExams(response.data);
            setSelectedSubject(subject);
        } catch (error) {
            console.error('시험 목록 조회 실패:', error);
            Alert.alert('오류', '시험 목록을 가져오는데 실패했습니다.');
        }
    };

    // 새 시험 생성
    const handleCreateExam = () => {
        // TODO: 시험 생성 화면으로 이동
        Alert.alert('알림', '시험 생성 기능은 준비 중입니다.');
    };

    // 시험 시작
    const handleStartExam = (examId) => {
        // TODO: 시험 화면으로 이동
        Alert.alert('알림', '시험 시작 기능은 준비 중입니다.');
    };

    return (
        <View style={styles.container}>
            {/* 헤더 */}
            <View style={styles.header}>
                <Text style={styles.title}>단원평가</Text>
                <TouchableOpacity style={styles.createButton} onPress={handleCreateExam}>
                    <Text style={styles.buttonText}>새 평가 추가</Text>
                </TouchableOpacity>
            </View>

            {/* 과목 선택 */}
            <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.subjectScroll}>
                {subjects.map((subject) => (
                    <TouchableOpacity
                        key={subject}
                        style={[
                            styles.subjectButton,
                            selectedSubject === subject && styles.selectedSubject,
                        ]}
                        onPress={() => handleSubjectSelect(subject)}
                    >
                        <Text
                            style={[
                                styles.subjectText,
                                selectedSubject === subject && styles.selectedSubjectText,
                            ]}
                        >
                            {subject}
                        </Text>
                    </TouchableOpacity>
                ))}
            </ScrollView>

            {/* 시험 목록 */}
            <ScrollView style={styles.examList}>
                {exams.map((exam) => (
                    <TouchableOpacity
                        key={exam.id}
                        style={styles.examCard}
                        onPress={() => handleStartExam(exam.id)}
                    >
                        <Text style={styles.examTitle}>{exam.title}</Text>
                        <Text style={styles.examInfo}>
                            {exam.category} • {exam.difficulty}단계 • {exam.questionCount}문제
                        </Text>
                        <Text style={styles.examDescription}>{exam.description}</Text>
                    </TouchableOpacity>
                ))}
            </ScrollView>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5f5',
    },
    header: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: 20,
        backgroundColor: '#fff',
        borderBottomWidth: 1,
        borderBottomColor: '#e0e0e0',
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        color: '#333',
    },
    createButton: {
        backgroundColor: '#007AFF',
        paddingHorizontal: 15,
        paddingVertical: 8,
        borderRadius: 8,
    },
    buttonText: {
        color: '#fff',
        fontSize: 16,
        fontWeight: '600',
    },
    subjectScroll: {
        maxHeight: 60,
        backgroundColor: '#fff',
        paddingVertical: 10,
    },
    subjectButton: {
        paddingHorizontal: 20,
        paddingVertical: 8,
        marginHorizontal: 5,
        borderRadius: 20,
        backgroundColor: '#f0f0f0',
    },
    selectedSubject: {
        backgroundColor: '#007AFF',
    },
    subjectText: {
        fontSize: 16,
        color: '#666',
    },
    selectedSubjectText: {
        color: '#fff',
    },
    examList: {
        flex: 1,
        padding: 15,
    },
    examCard: {
        backgroundColor: '#fff',
        padding: 15,
        borderRadius: 10,
        marginBottom: 15,
        shadowColor: '#000',
        shadowOffset: {
            width: 0,
            height: 2,
        },
        shadowOpacity: 0.1,
        shadowRadius: 3,
        elevation: 3,
    },
    examTitle: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#333',
        marginBottom: 5,
    },
    examInfo: {
        fontSize: 14,
        color: '#666',
        marginBottom: 5,
    },
    examDescription: {
        fontSize: 14,
        color: '#999',
    },
});

export default ExamMainScreen; 