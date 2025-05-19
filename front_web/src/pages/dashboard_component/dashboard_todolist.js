import React, { useState, useEffect } from 'react';
import axios from 'axios';

function DashboardTodoList() {
  const [todos, setTodos] = useState([]);
  const [newTodo, setNewTodo] = useState('');
  const accessToken = localStorage.getItem('accessToken');

  useEffect(() => {
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
      { text: newTodo, done: false },
      { headers: { Authorization: `Bearer ${accessToken}` } }
    )
    .then(res => {
      setTodos(prev => [...prev, res.data]);
      setNewTodo('');
    })
    .catch(err => {
      console.error('투두 추가 실패:', err);
      alert('할 일 추가에 실패했습니다.');
    });
  };

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

  const handleDeleteTodo = (todoId) => {
    axios.delete(`http://localhost:8080/api/todos/${todoId}`, {
      headers: { Authorization: `Bearer ${accessToken}` },
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
    <div className="dashboard-card dashboard-todo-card">
      <div style={{ fontWeight: 600, marginBottom: 8 }}>오늘의 할 일</div>
      <form onSubmit={handleAddTodo} style={{ marginBottom: 12 }}>
        <input
          type="text"
          value={newTodo}
          onChange={e => setNewTodo(e.target.value)}
          placeholder="할 일을 입력하세요"
          style={{
            width: '70%',
            padding: '6px 8px',
            border: '1px solid #ddd',
            borderRadius: '6px',
            marginRight: '8px',
          }}
        />
        <button
          type="submit"
          style={{
            padding: '6px 12px',
            background: '#2563eb',
            color: '#fff',
            border: 'none',
            borderRadius: '6px',
            cursor: 'pointer',
          }}
        >
          추가
        </button>
      </form>
      <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
        {todos.map(todo => (
          <li
            key={todo.id}
            className={`dashboard-todo-item${todo.done ? ' done' : ''}`}
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '6px 8px',
              borderRadius: '6px',
              background: todo.done ? '#f1f5fd' : '#fff',
              marginBottom: '8px',
              boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <input
                type="checkbox"
                checked={todo.done}
                onChange={() => handleToggleTodo(todo.id)}
                style={{ marginRight: '8px' }}
              />
              <span>{todo.text}</span>
            </div>
            <button
              onClick={() => handleDeleteTodo(todo.id)}
              className="dashboard-todo-delete-btn"
              style={{
                background: 'none',
                border: 'none',
                color: '#888',
                cursor: 'pointer',
                fontSize: '16px',
              }}
            >
              🗑️
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default DashboardTodoList;
