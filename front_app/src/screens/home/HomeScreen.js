// import React, { useState } from 'react';
// import { View, Text, StyleSheet, Image, TextInput, TouchableOpacity, ScrollView, SafeAreaView } from 'react-native';
// import { Calendar } from 'react-native-calendars';
// import BouncyCheckbox from "react-native-bouncy-checkbox";

// const getToday = () => {
//   const today = new Date();
//   const yyyy = today.getFullYear();
//   const mm = String(today.getMonth() + 1).padStart(2, '0');
//   const dd = String(today.getDate()).padStart(2, '0');
//   return `${yyyy}-${mm}-${dd}`;
// };

// const userProfile = {
//   name: '김종프님',
//   greeting: '오늘도 화이팅하세요! 💪',
//   profileImage: 'https://randomuser.me/api/portraits/men/1.jpg',
// };

// const initialTodos = [
//   { id: '1', text: '수학 문제 30개 풀기', done: false },
//   { id: '2', text: '영어 단어 50개 암기하기', done: false },
//   { id: '3', text: '국어 독해 연습하기', done: false },
// ];

// const HomeScreen = () => {
//   const today = getToday();
//   const [todos, setTodos] = useState(initialTodos);
//   const [input, setInput] = useState('');
//   const [selectedDate, setSelectedDate] = useState(today);

//   // 할 일 추가
//   const addTodo = () => {
//     if (input.trim() === '') return;
//     setTodos([...todos, { id: Date.now().toString(), text: input, done: false }]);
//     setInput('');
//   };

//   // 체크박스 토글
//   const toggleTodo = (id) => {
//     setTodos(todos.map(todo => todo.id === id ? { ...todo, done: !todo.done } : todo));
//   };

//   // 할 일 삭제
//   const deleteTodo = (id) => {
//     setTodos(todos.filter(todo => todo.id !== id));
//   };

//   // 달력 마킹
//   const markedDates = {
//     [selectedDate]: { selected: true, selectedColor: '#111', selectedTextColor: '#fff' },
//     [today]: { selected: true, selectedColor: '#E0E0E0', selectedTextColor: '#111' },
//     '2025-03-08': { selected: true, selectedColor: '#E0E0E0', selectedTextColor: '#111' },
//     '2025-03-20': { selected: true, selectedColor: '#E0E0E0', selectedTextColor: '#111' },
//     '2025-03-26': { selected: true, selectedColor: '#E0E0E0', selectedTextColor: '#111' },
//   };

//   return (
//     <SafeAreaView style={styles.safeArea}>
//       <ScrollView contentContainerStyle={styles.container} showsVerticalScrollIndicator={false}>
//         {/* 상단 프로필 */}
//         <View style={styles.profileRow}>
//           <Image source={{ uri: userProfile.profileImage }} style={styles.profileImage} />
//           <View>
//             <Text style={styles.profileName}>{userProfile.name}</Text>
//             <Text style={styles.profileGreeting}>{userProfile.greeting}</Text>
//           </View>
//           <TouchableOpacity style={styles.settingBtn}>
//             <Text style={{ fontSize: 20, color: '#888' }}>⚙️</Text>
//           </TouchableOpacity>
//         </View>

//         {/* 달력 */}
//         <View style={styles.calendarBox}>
//           <Calendar
//             current={today}
//             markedDates={markedDates}
//             onDayPress={day => setSelectedDate(day.dateString)}
//             theme={{
//               backgroundColor: '#fff',
//               calendarBackground: '#fff',
//               textSectionTitleColor: '#b6c1cd',
//               selectedDayBackgroundColor: '#111',
//               selectedDayTextColor: '#fff',
//               todayTextColor: '#111',
//               dayTextColor: '#222',
//               textDisabledColor: '#d9e1e8',
//               arrowColor: '#111',
//               monthTextColor: '#111',
//               indicatorColor: '#111',
//               textDayFontWeight: '500',
//               textMonthFontWeight: 'bold',
//               textDayFontSize: 16,
//               textMonthFontSize: 18,
//               textDayHeaderFontSize: 14,
//             }}
//             style={{ borderRadius: 14 }}
//           />
//         </View>

//         {/* 주간 성장률 */}
//         <View style={styles.growthBox}>
//           <View style={{ flexDirection: 'row', alignItems: 'center', marginBottom: 2 }}>
//             <Text style={styles.growthUp}>↑</Text>
//             <Text style={styles.growthRate}>+15%</Text>
//           </View>
//           <Text style={styles.growthDesc}>지난주 대비 학습시간이 증가했어요!</Text>
//         </View>

//         {/* 오늘의 할 일 */}
//         <View style={styles.todoBox}>
//           <Text style={styles.todoTitle}>오늘의 할 일</Text>
//           <View style={styles.todoInputRow}>
//             <TextInput
//               style={styles.todoInput}
//               placeholder="할 일을 입력하세요"
//               value={input}
//               onChangeText={setInput}
//               onSubmitEditing={addTodo}
//               placeholderTextColor="#999"
//             />
//             <TouchableOpacity style={styles.addBtn} onPress={addTodo}>
//               <Text style={{ fontSize: 18, color: '#fff' }}>+</Text>
//             </TouchableOpacity>
//           </View>
//           {todos.map(item => (
//             <View key={item.id} style={styles.todoItem}>
//               <BouncyCheckbox
//                 isChecked={item.done}
//                 onPress={() => toggleTodo(item.id)}
//                 fillColor="#111"
//                 unfillColor="#fff"
//                 iconStyle={{ borderRadius: 4, borderColor: "#111" }}
//                 size={20}
//                 disableText
//                 style={{ marginRight: 8 }}
//               />
//               <Text style={[
//                 styles.todoText,
//                 item.done && { textDecorationLine: 'line-through', color: '#aaa' }
//               ]}>
//                 {item.text}
//               </Text>
//               <TouchableOpacity style={styles.deleteBtn} onPress={() => deleteTodo(item.id)}>
//                 <Text style={styles.deleteBtnText}>✕</Text>
//               </TouchableOpacity>
//             </View>
//           ))}
//         </View>
//       </ScrollView>
//     </SafeAreaView>
//   );
// };

// const styles = StyleSheet.create({
//   safeArea: {
//     flex: 1,
//     backgroundColor: '#F8FAFC',
//   },
//   container: {
//     paddingHorizontal: 16,
//     paddingTop: 12,
//     paddingBottom: 32,
//   },
//   profileRow: {
//     flexDirection: 'row',
//     alignItems: 'center',
//     marginBottom: 10,
//   },
//   profileImage: {
//     width: 44,
//     height: 44,
//     borderRadius: 22,
//     marginRight: 12,
//   },
//   profileName: {
//     fontWeight: 'bold',
//     fontSize: 16,
//     color: '#222',
//   },
//   profileGreeting: {
//     fontSize: 14,
//     color: '#888',
//     marginTop: 2,
//   },
//   settingBtn: {
//     marginLeft: 'auto',
//     padding: 4,
//   },
//   calendarBox: {
//     backgroundColor: '#fff',
//     borderRadius: 14,
//     padding: 10,
//     marginBottom: 12,
//     elevation: 1,
//   },
//   growthBox: {
//     backgroundColor: '#fff',
//     borderRadius: 12,
//     padding: 12,
//     marginBottom: 12,
//   },
//   growthUp: {
//     color: '#22C55E',
//     fontWeight: 'bold',
//     fontSize: 16,
//     marginRight: 2,
//   },
//   growthRate: {
//     color: '#22C55E',
//     fontWeight: 'bold',
//     fontSize: 16,
//     marginRight: 6,
//   },
//   growthDesc: {
//     color: '#444',
//     fontSize: 14,
//   },
//   todoBox: {
//     backgroundColor: '#fff',
//     borderRadius: 12,
//     padding: 12,
//     marginBottom: 16,
//   },
//   todoTitle: {
//     fontWeight: 'bold',
//     fontSize: 16,
//     color: '#222',
//     marginBottom: 12,
//   },
//   todoInputRow: {
//     flexDirection: 'row',
//     alignItems: 'center',
//     marginBottom: 8,
//   },
//   todoInput: {
//     flex: 1,
//     backgroundColor: '#F1F5F9',
//     borderRadius: 8,
//     paddingHorizontal: 12,
//     paddingVertical: 8,
//     fontSize: 15,
//     marginRight: 8,
//     color: '#222',
//   },
//   addBtn: {
//     backgroundColor: '#111',
//     borderRadius: 8,
//     padding: 8,
//     alignItems: 'center',
//     justifyContent: 'center',
//   },
//   todoItem: {
//     flexDirection: 'row',
//     alignItems: 'center',
//     marginBottom: 6,
//   },
//   todoText: {
//     fontSize: 15,
//     color: '#222',
//     marginLeft: 6,
//     flex: 1,
//   },
//   checkbox: {
//     marginRight: 2,
//     width: 18,
//     height: 18,
//   },
//   deleteBtn: {
//     marginLeft: 8,
//     padding: 4,
//   },
//   deleteBtnText: {
//     fontSize: 18,
//     color: '#e74c3c',
//     fontWeight: 'bold',
//   },
// });

// export default HomeScreen;


import React, { useState } from 'react';
import { View, Text, StyleSheet, Image, TextInput, TouchableOpacity, ScrollView, SafeAreaView } from 'react-native';
import { Calendar } from 'react-native-calendars';
import BouncyCheckbox from "react-native-bouncy-checkbox";
import { useUser } from '../../contexts/UserContext'; // Context 사용

const getToday = () => {
  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, '0');
  const dd = String(today.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
};

const initialTodos = [
  { id: '1', text: '수학 문제 30개 풀기', done: false },
  { id: '2', text: '영어 단어 50개 암기하기', done: false },
  { id: '3', text: '국어 독해 연습하기', done: false },
];

const HomeScreen = () => {
  const { user } = useUser(); // Context에서 유저 정보 받기
  const today = getToday();
  const [todos, setTodos] = useState(initialTodos);
  const [input, setInput] = useState('');
  const [selectedDate, setSelectedDate] = useState(today);

  const addTodo = () => {
    if (input.trim() === '') return;
    setTodos([...todos, { id: Date.now().toString(), text: input, done: false }]);
    setInput('');
  };

  const toggleTodo = (id) => {
    setTodos(todos.map(todo => todo.id === id ? { ...todo, done: !todo.done } : todo));
  };

  const deleteTodo = (id) => {
    setTodos(todos.filter(todo => todo.id !== id));
  };

  const markedDates = {
    [selectedDate]: { selected: true, selectedColor: '#111', selectedTextColor: '#fff' },
    [today]: { selected: true, selectedColor: '#E0E0E0', selectedTextColor: '#111' },
    '2025-03-08': { selected: true, selectedColor: '#E0E0E0', selectedTextColor: '#111' },
    '2025-03-20': { selected: true, selectedColor: '#E0E0E0', selectedTextColor: '#111' },
    '2025-03-26': { selected: true, selectedColor: '#E0E0E0', selectedTextColor: '#111' },
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView contentContainerStyle={styles.container} showsVerticalScrollIndicator={false}>
        {/* 상단 프로필 */}
        <View style={styles.profileRow}>
          <Image source={{ uri: user.profileImage }} style={styles.profileImage} />
          <View>
            <Text style={styles.profileName}>{user.name}</Text>
            <Text style={styles.profileGreeting}>{user.greeting}</Text>
          </View>
          <TouchableOpacity style={styles.settingBtn}>
            <Text style={{ fontSize: 20, color: '#888' }}>⚙️</Text>
          </TouchableOpacity>
        </View>

        {/* 달력 */}
        <View style={styles.calendarBox}>
          <Calendar
            current={today}
            markedDates={markedDates}
            onDayPress={day => setSelectedDate(day.dateString)}
            theme={{
              backgroundColor: '#fff',
              calendarBackground: '#fff',
              textSectionTitleColor: '#b6c1cd',
              selectedDayBackgroundColor: '#111',
              selectedDayTextColor: '#fff',
              todayTextColor: '#111',
              dayTextColor: '#222',
              textDisabledColor: '#d9e1e8',
              arrowColor: '#111',
              monthTextColor: '#111',
              indicatorColor: '#111',
              textDayFontWeight: '500',
              textMonthFontWeight: 'bold',
              textDayFontSize: 16,
              textMonthFontSize: 18,
              textDayHeaderFontSize: 14,
            }}
            style={{ borderRadius: 14 }}
          />
        </View>

        {/* 주간 성장률 */}
        <View style={styles.growthBox}>
          <View style={{ flexDirection: 'row', alignItems: 'center', marginBottom: 2 }}>
            <Text style={styles.growthUp}>↑</Text>
            <Text style={styles.growthRate}>+15%</Text>
          </View>
          <Text style={styles.growthDesc}>지난주 대비 학습시간이 증가했어요!</Text>
        </View>

        {/* 오늘의 할 일 */}
        <View style={styles.todoBox}>
          <Text style={styles.todoTitle}>오늘의 할 일</Text>
          <View style={styles.todoInputRow}>
            <TextInput
              style={styles.todoInput}
              placeholder="할 일을 입력하세요"
              value={input}
              onChangeText={setInput}
              onSubmitEditing={addTodo}
              placeholderTextColor="#999"
            />
            <TouchableOpacity style={styles.addBtn} onPress={addTodo}>
              <Text style={{ fontSize: 18, color: '#fff' }}>+</Text>
            </TouchableOpacity>
          </View>
          {todos.map(item => (
            <View key={item.id} style={styles.todoItem}>
              <BouncyCheckbox
                isChecked={item.done}
                onPress={() => toggleTodo(item.id)}
                fillColor="#111"
                unfillColor="#fff"
                iconStyle={{ borderRadius: 4, borderColor: "#111" }}
                size={20}
                disableText
                style={{ marginRight: 8 }}
              />
              <Text style={[
                styles.todoText,
                item.done && { textDecorationLine: 'line-through', color: '#aaa' }
              ]}>
                {item.text}
              </Text>
              <TouchableOpacity style={styles.deleteBtn} onPress={() => deleteTodo(item.id)}>
                <Text style={styles.deleteBtnText}>✕</Text>
              </TouchableOpacity>
            </View>
          ))}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#F8FAFC',
  },
  container: {
    paddingHorizontal: 16,
    paddingTop: 12,
    paddingBottom: 32,
  },
  profileRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  profileImage: {
    width: 44,
    height: 44,
    borderRadius: 22,
    marginRight: 12,
  },
  profileName: {
    fontWeight: 'bold',
    fontSize: 16,
    color: '#222',
  },
  profileGreeting: {
    fontSize: 14,
    color: '#888',
    marginTop: 2,
  },
  settingBtn: {
    marginLeft: 'auto',
    padding: 4,
  },
  calendarBox: {
    backgroundColor: '#fff',
    borderRadius: 14,
    padding: 10,
    marginBottom: 12,
    elevation: 1,
  },
  growthBox: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 12,
    marginBottom: 12,
  },
  growthUp: {
    color: '#22C55E',
    fontWeight: 'bold',
    fontSize: 16,
    marginRight: 2,
  },
  growthRate: {
    color: '#22C55E',
    fontWeight: 'bold',
    fontSize: 16,
    marginRight: 6,
  },
  growthDesc: {
    color: '#444',
    fontSize: 14,
  },
  todoBox: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 12,
    marginBottom: 16,
  },
  todoTitle: {
    fontWeight: 'bold',
    fontSize: 16,
    color: '#222',
    marginBottom: 12,
  },
  todoInputRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  todoInput: {
    flex: 1,
    backgroundColor: '#F1F5F9',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    fontSize: 15,
    marginRight: 8,
    color: '#222',
  },
  addBtn: {
    backgroundColor: '#111',
    borderRadius: 8,
    padding: 8,
    alignItems: 'center',
    justifyContent: 'center',
  },
  todoItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 6,
  },
  todoText: {
    fontSize: 15,
    color: '#222',
    marginLeft: 6,
    flex: 1,
  },
  deleteBtn: {
    marginLeft: 8,
    padding: 4,
  },
  deleteBtnText: {
    fontSize: 18,
    color: '#e74c3c',
    fontWeight: 'bold',
  },
});

export default HomeScreen;
