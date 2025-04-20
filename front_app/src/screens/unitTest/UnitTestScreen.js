import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const UnitTestScreen = () => {
    return (
        <View style={styles.container}>
            <Text style={styles.text}>단원평가 페이지입니다</Text>
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
    text: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#333',
    },
});

export default UnitTestScreen;
