import React, { useState, useEffect } from 'react';
import axios from 'axios';

function DashboardTodoList() {
  const [todos, setTodos] = useState([]);
  const [newTodo, setNewTodo] = useState('');
  const accessToken = localStorage.getItem('accessToken');

  useEffect(() => {
    console.log('Access Token:', accessToken);

    if (!accessToken) {
      alert('로그인이 필요합니다.');
      return;
    }

    axios.get('http://localhost:8080/api/todos', {
      headers: { Authorization: `Bearer ${accessToken}` },
    })
    .then(res => setTodos(res.data))
    .catch(err => {
      console.error('투두리스트 불러오기 실패:', err);
      alert('투두리스트를 불러오는데 실패했습니다.');
    });
  }, [accessToken]);

  const handleAddTodo = (e) => {
    e.preventDefault();
    if (newTodo.trim() === '') return;

    axios.post(
      'http://localhost:8080/api/todos',
      { text: newTodo, done: false }, // 요청 데이터
      { headers: { Authorization: `Bearer ${accessToken}` } }
    )
    .then(res => {
      setTodos(prev => [...prev, res.data]); // 새로 추가된 할 일을 리스트에 추가
      setNewTodo('');
    })
    .catch(err => {
      console.error('투두 추가 실패:', err);
      alert('할 일 추가에 실패했습니다.');
    });
  };

  // todoId를 직접 받아 처리하도록 수정
  const handleToggleTodo = (todoId) => {
    const todo = todos.find(t => t.id === todoId);
    if (!todo) return;

    axios.put(`http://localhost:8080/api/todos/${todoId}/toggle`, {}, {
      headers: { Authorization: `Bearer ${accessToken}` }
    })
    .then(res => {
      setTodos(todos.map(t => t.id === todoId ? res.data : t));
    })
    .catch(err => {
      console.error('투두 상태 변경 실패:', err);
      alert('상태 변경에 실패했습니다.');
    });
  };

  // todoId를 직접 받아 처리하도록 수정
  const handleDeleteTodo = (todoId) => {
    axios.delete(`http://localhost:8080/api/todos/${todoId}`, {
      headers: { Authorization: `Bearer ${accessToken}` }
    })
    .then(() => {
      setTodos(todos.filter(t => t.id !== todoId));
    })
    .catch(err => {
      console.error('투두 삭제 실패:', err);
      alert('삭제에 실패했습니다.');
    });
  };

  return (
    <div>
      <h3>오늘의 할 일</h3>
      <form onSubmit={handleAddTodo}>
        <input
          type="text"
          value={newTodo}
          onChange={e => setNewTodo(e.target.value)}
          placeholder="할 일을 입력하세요"
        />
        <button type="submit">추가</button>
      </form>
      <ul>
        {todos.map(todo => (
          <li key={todo.id} style={{ textDecoration: todo.done ? 'line-through' : 'none' }}>
            <input
              type="checkbox"
              checked={todo.done}
              onChange={() => handleToggleTodo(todo.id)}
            />
            {todo.text}
            <button onClick={() => handleDeleteTodo(todo.id)}>삭제</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default DashboardTodoList;
