import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { Image } from 'react-native';
import HomeScreen from './src/screens/home/HomeScreen';
import CommunityScreen from './src/screens/community/CommunityMainScreen';
import PersonalLearningScreen from './src/screens/personal/PersonalStudyMainScreen';
import GroupLearningScreen from './src/screens/group/GroupStudyMainScreen';
import MyPageScreen from './src/screens/mypage/MyPageMainScreen';
import IntroScreen from './src/screens/IntroScreen';
import LoginScreen from './src/screens/auth/LoginScreen';
import RegisterScreen from './src/screens/auth/RegisterScreen';
import FindAccountScreen from "./src/screens/auth/FindAccountScreen";
import ResetPasswordScreen from "./src/screens/auth/ResetPasswordScreen";

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

const screenOptions = ({ route }) => ({
    tabBarIcon: ({ focused, size }) => {
        let iconPath;
        switch (route.name) {
            case 'Home':
                iconPath = require('./src/assets/home.png');
                break;
            case 'Community':
                iconPath = require('./src/assets/community.png');
                break;
            case 'Personal Learning':
                iconPath = require('./src/assets/personal.png');
                break;
            case 'Group Learning':
                iconPath = require('./src/assets/group.png');
                break;
            case 'My Page':
                iconPath = require('./src/assets/mypage.png');
                break;
        }
        return <Image source={iconPath} style={{ width: size, height: size, tintColor: focused ? '#007AFF' : '#8E8E93' }} />;
    },
    tabBarShowLabel: true,
    tabBarActiveTintColor: '#007AFF',
    tabBarInactiveTintColor: '#8E8E93',
});

const MainTabNavigator = () => (
    <Tab.Navigator screenOptions={screenOptions}>
        <Tab.Screen name="Home" component={HomeScreen} />
        <Tab.Screen name="Community" component={CommunityScreen} />
        <Tab.Screen name="Personal Learning" component={PersonalLearningScreen} />
        <Tab.Screen name="Group Learning" component={GroupLearningScreen} />
        <Tab.Screen name="My Page" component={MyPageScreen} />
    </Tab.Navigator>
);

const App = () => (
    <NavigationContainer>
        <Stack.Navigator initialRouteName="Intro" screenOptions={{ headerShown: false }}>
            <Stack.Screen name="Intro" component={IntroScreen} />
            <Stack.Screen name="Login" component={LoginScreen} />
            <Stack.Screen name="Register" component={RegisterScreen} />
            <Stack.Screen name="FindAccount" component={FindAccountScreen} />
            <Stack.Screen name="ResetPassword" component={ResetPasswordScreen} />
            <Stack.Screen name="Home" component={MainTabNavigator} />
        </Stack.Navigator>
    </NavigationContainer>
);

export default App;
