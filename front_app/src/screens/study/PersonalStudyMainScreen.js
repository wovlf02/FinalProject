import React from 'react';
import { View, Text, StyleSheet, Button } from 'react-native';
import { useNavigation } from '@react-navigation/native';

const PersonalStudyMainScreen = () => {
    const navigation = useNavigation();

    const handlePersonalStudy = () => {
        navigation.navigate('PersonalStudyScreen');  // 개인 공부 페이지로 이동
    };

    const handleTeamStudy = () => {
        navigation.navigate('TeamStudyScreen');  // 팀 공부 페이지로 이동
    };

    return (
        <View style={styles.container}>
            <View style={styles.buttonContainer}>
                <Button title="개인 공부" onPress={handlePersonalStudy} style={styles.button} />
                <Button title="팀 공부" onPress={handleTeamStudy} style={styles.button} />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f5f5f5',
    },
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '80%',
    },
    button: {
        margin: 10,
    },
});

export default PersonalStudyMainScreen;
