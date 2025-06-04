import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    StyleSheet,
    ScrollView,
    TouchableOpacity,
    Alert,
} from 'react-native';
import api from '../../api/api';

const ExamResultScreen = ({ route, navigation }) => {
    const { examId, answers, questions } = route.params;
    const [score, setScore] = useState(0);
    const [result, setResult] = useState(null);

    useEffect(() => {
        calculateScore();
    }, []);

    // 점수 계산
    const calculateScore = () => {
        let correctCount = 0;
        questions.forEach((question, index) => {
            if (answers[index] === question.correctAnswer) {
                correctCount++;
            }
        });

        const totalScore = (correctCount / questions.length) * 100;
        setScore(totalScore);

        // 결과 저장
        saveResult(totalScore, correctCount);
    };

    // 결과 저장
    const saveResult = async (totalScore, correctCount) => {
        try {
            const resultData = {
                examId,
                score: totalScore,
                correctCount,
                totalQuestions: questions.length,
                answers,
            };

            const response = await api.post('/api/exam/result', resultData);
            setResult(response.data);
        } catch (error) {
            console.error('결과 저장 실패:', error);
            Alert.alert('오류', '결과 저장에 실패했습니다.');
        }
    };

    // 문제 상세 보기
    const handleViewDetails = () => {
        navigation.navigate('ExamReview', {
            questions,
            answers,
            result,
        });
    };

    // 홈으로 돌아가기
    const handleGoHome = () => {
        navigation.navigate('Main');
    };

    return (
        <View style={styles.container}>
            {/* 결과 헤더 */}
            <View style={styles.header}>
                <Text style={styles.title}>시험 결과</Text>
            </View>

            {/* 점수 표시 */}
            <View style={styles.scoreContainer}>
                <Text style={styles.scoreText}>{Math.round(score)}점</Text>
                <Text style={styles.scoreDescription}>
                    {score >= 90
                        ? '우수'
                        : score >= 80
                        ? '양호'
                        : score >= 70
                        ? '보통'
                        : score >= 60
                        ? '미흡'
                        : '부족'}
                </Text>
            </View>

            {/* 상세 정보 */}
            <ScrollView style={styles.detailsContainer}>
                <View style={styles.detailItem}>
                    <Text style={styles.detailLabel}>정답 수</Text>
                    <Text style={styles.detailValue}>
                        {Object.values(answers).filter(
                            (answer, index) => answer === questions[index].correctAnswer
                        ).length}{' '}
                        / {questions.length}
                    </Text>
                </View>

                <View style={styles.detailItem}>
                    <Text style={styles.detailLabel}>소요 시간</Text>
                    <Text style={styles.detailValue}>{result?.timeSpent || '0'}분</Text>
                </View>

                <View style={styles.detailItem}>
                    <Text style={styles.detailLabel}>시험 일시</Text>
                    <Text style={styles.detailValue}>
                        {new Date(result?.submittedAt).toLocaleString()}
                    </Text>
                </View>
            </ScrollView>

            {/* 버튼 */}
            <View style={styles.buttonContainer}>
                <TouchableOpacity style={styles.button} onPress={handleViewDetails}>
                    <Text style={styles.buttonText}>문제 상세 보기</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    style={[styles.button, styles.homeButton]}
                    onPress={handleGoHome}
                >
                    <Text style={styles.buttonText}>홈으로</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5f5',
    },
    header: {
        padding: 20,
        backgroundColor: '#fff',
        borderBottomWidth: 1,
        borderBottomColor: '#e0e0e0',
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        color: '#333',
        textAlign: 'center',
    },
    scoreContainer: {
        padding: 30,
        backgroundColor: '#fff',
        alignItems: 'center',
        marginTop: 20,
        marginHorizontal: 20,
        borderRadius: 10,
        shadowColor: '#000',
        shadowOffset: {
            width: 0,
            height: 2,
        },
        shadowOpacity: 0.1,
        shadowRadius: 3,
        elevation: 3,
    },
    scoreText: {
        fontSize: 48,
        fontWeight: 'bold',
        color: '#007AFF',
        marginBottom: 10,
    },
    scoreDescription: {
        fontSize: 20,
        color: '#666',
    },
    detailsContainer: {
        flex: 1,
        padding: 20,
    },
    detailItem: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: 15,
        backgroundColor: '#fff',
        borderRadius: 10,
        marginBottom: 10,
    },
    detailLabel: {
        fontSize: 16,
        color: '#666',
    },
    detailValue: {
        fontSize: 16,
        fontWeight: '600',
        color: '#333',
    },
    buttonContainer: {
        padding: 20,
        backgroundColor: '#fff',
        borderTopWidth: 1,
        borderTopColor: '#e0e0e0',
    },
    button: {
        backgroundColor: '#007AFF',
        padding: 15,
        borderRadius: 10,
        marginBottom: 10,
    },
    homeButton: {
        backgroundColor: '#666',
    },
    buttonText: {
        color: '#fff',
        fontSize: 16,
        fontWeight: '600',
        textAlign: 'center',
    },
});

export default ExamResultScreen; 