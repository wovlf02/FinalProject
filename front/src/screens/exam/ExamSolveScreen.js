import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    ScrollView,
    Alert,
    ActivityIndicator,
} from 'react-native';
import api from '../../api/api';

const ExamSolveScreen = ({ route, navigation }) => {
    const { examId, subject } = route.params;
    const [questions, setQuestions] = useState([]);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [selectedAnswer, setSelectedAnswer] = useState(null);
    const [answers, setAnswers] = useState({});
    const [timeLeft, setTimeLeft] = useState(3600); // 1시간
    const [loading, setLoading] = useState(true);

    // 시험 문제 가져오기
    useEffect(() => {
        fetchQuestions();
    }, []);

    // 타이머
    useEffect(() => {
        if (timeLeft > 0) {
            const timer = setInterval(() => {
                setTimeLeft((prev) => prev - 1);
            }, 1000);
            return () => clearInterval(timer);
        } else {
            handleSubmitExam();
        }
    }, [timeLeft]);

    // 시험 문제 가져오기
    const fetchQuestions = async () => {
        try {
            const response = await api.get(`/api/exam?subject=${subject}`);
            setQuestions(response.data);
            setLoading(false);
        } catch (error) {
            console.error('시험 문제 조회 실패:', error);
            Alert.alert('오류', '시험 문제를 가져오는데 실패했습니다.');
            navigation.goBack();
        }
    };

    // 답안 선택
    const handleAnswerSelect = (answerIndex) => {
        setSelectedAnswer(answerIndex);
        setAnswers((prev) => ({
            ...prev,
            [currentQuestionIndex]: answerIndex,
        }));
    };

    // 이전 문제로 이동
    const handlePrevQuestion = () => {
        if (currentQuestionIndex > 0) {
            setCurrentQuestionIndex((prev) => prev - 1);
            setSelectedAnswer(answers[currentQuestionIndex - 1] || null);
        }
    };

    // 다음 문제로 이동
    const handleNextQuestion = () => {
        if (currentQuestionIndex < questions.length - 1) {
            setCurrentQuestionIndex((prev) => prev + 1);
            setSelectedAnswer(answers[currentQuestionIndex + 1] || null);
        }
    };

    // 시험 제출
    const handleSubmitExam = () => {
        Alert.alert(
            '시험 종료',
            '시험을 종료하시겠습니까?',
            [
                {
                    text: '취소',
                    style: 'cancel',
                },
                {
                    text: '종료',
                    onPress: () => {
                        // TODO: 답안 제출 API 호출
                        navigation.replace('ExamResult', {
                            examId,
                            answers,
                            questions,
                        });
                    },
                },
            ]
        );
    };

    if (loading) {
        return (
            <View style={styles.loadingContainer}>
                <ActivityIndicator size="large" color="#007AFF" />
            </View>
        );
    }

    const currentQuestion = questions[currentQuestionIndex];

    return (
        <View style={styles.container}>
            {/* 헤더 */}
            <View style={styles.header}>
                <Text style={styles.timer}>
                    {Math.floor(timeLeft / 60)}:{String(timeLeft % 60).padStart(2, '0')}
                </Text>
                <TouchableOpacity style={styles.submitButton} onPress={handleSubmitExam}>
                    <Text style={styles.buttonText}>시험 종료</Text>
                </TouchableOpacity>
            </View>

            {/* 문제 번호 */}
            <View style={styles.questionNumber}>
                <Text style={styles.questionNumberText}>
                    문제 {currentQuestionIndex + 1} / {questions.length}
                </Text>
            </View>

            {/* 문제 내용 */}
            <ScrollView style={styles.questionContainer}>
                <Text style={styles.questionText}>{currentQuestion.question}</Text>

                {/* 보기 */}
                {[1, 2, 3, 4].map((index) => (
                    <TouchableOpacity
                        key={index}
                        style={[
                            styles.optionButton,
                            selectedAnswer === index && styles.selectedOption,
                        ]}
                        onPress={() => handleAnswerSelect(index)}
                    >
                        <Text
                            style={[
                                styles.optionText,
                                selectedAnswer === index && styles.selectedOptionText,
                            ]}
                        >
                            {index}. {currentQuestion[`option${index}`]}
                        </Text>
                    </TouchableOpacity>
                ))}
            </ScrollView>

            {/* 네비게이션 버튼 */}
            <View style={styles.navigationButtons}>
                <TouchableOpacity
                    style={[styles.navButton, currentQuestionIndex === 0 && styles.disabledButton]}
                    onPress={handlePrevQuestion}
                    disabled={currentQuestionIndex === 0}
                >
                    <Text style={styles.buttonText}>이전</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    style={[
                        styles.navButton,
                        currentQuestionIndex === questions.length - 1 && styles.disabledButton,
                    ]}
                    onPress={handleNextQuestion}
                    disabled={currentQuestionIndex === questions.length - 1}
                >
                    <Text style={styles.buttonText}>다음</Text>
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
    loadingContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
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
    timer: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#333',
    },
    submitButton: {
        backgroundColor: '#FF3B30',
        paddingHorizontal: 15,
        paddingVertical: 8,
        borderRadius: 8,
    },
    buttonText: {
        color: '#fff',
        fontSize: 16,
        fontWeight: '600',
    },
    questionNumber: {
        padding: 15,
        backgroundColor: '#fff',
        borderBottomWidth: 1,
        borderBottomColor: '#e0e0e0',
    },
    questionNumberText: {
        fontSize: 16,
        color: '#666',
    },
    questionContainer: {
        flex: 1,
        padding: 20,
    },
    questionText: {
        fontSize: 18,
        color: '#333',
        marginBottom: 20,
        lineHeight: 24,
    },
    optionButton: {
        backgroundColor: '#fff',
        padding: 15,
        borderRadius: 10,
        marginBottom: 10,
        borderWidth: 1,
        borderColor: '#e0e0e0',
    },
    selectedOption: {
        backgroundColor: '#007AFF',
        borderColor: '#007AFF',
    },
    optionText: {
        fontSize: 16,
        color: '#333',
    },
    selectedOptionText: {
        color: '#fff',
    },
    navigationButtons: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        padding: 20,
        backgroundColor: '#fff',
        borderTopWidth: 1,
        borderTopColor: '#e0e0e0',
    },
    navButton: {
        backgroundColor: '#007AFF',
        paddingHorizontal: 30,
        paddingVertical: 10,
        borderRadius: 8,
    },
    disabledButton: {
        backgroundColor: '#ccc',
    },
});

export default ExamSolveScreen; 