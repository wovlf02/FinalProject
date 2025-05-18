// src/main/frontend/src/components/DashboardTodo.jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

export default function DashboardTodo() {
  const [todos, setTodos] = useState([]);
  const [newTodo, setNewTodo] = useState('');

  // JWT 토큰
  const token = localStorage.getItem('accessToken');
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  // 1) 할 일 목록 불러오기
  useEffect(() => {
    console.log('[Todo] Fetching todos from backend...');
    axios.get('/api/todos', headers)
      .then(res => {
        console.log('[Todo] GET /api/todos response:', res.data);
        setTodos(res.data);
      })
      .catch(err => console.error('[Todo] GET error:', err));
  }, []);

  // 2) 할 일 추가
  const handleAddTodo = e => {
    e.preventDefault();
    if (!newTodo.trim()) return;
    console.log('[Todo] Posting new todo:', newTodo);
    axios.post('/api/todos', { text: newTodo }, headers)
      .then(res => {
        console.log('[Todo] POST /api/todos response:', res.data);
        setTodos(prev => [...prev, res.data]);
        setNewTodo('');
      })
      .catch(err => console.error('[Todo] POST error:', err));
  };

  // 3) 완료 토글
  const handleToggleTodo = id => {
    console.log('[Todo] Toggling todo id:', id);
    axios.put(`/api/todos/${id}/toggle`, {}, headers)
      .then(res => {
        console.log('[Todo] PUT /api/todos/' + id + '/toggle response:', res.data);
        setTodos(prev => prev.map(t => t.id === id ? res.data : t));
      })
      .catch(err => console.error('[Todo] PUT error:', err));
  };

  // 4) 할 일 삭제
  const handleDeleteTodo = id => {
    console.log('[Todo] Deleting todo id:', id);
    axios.delete(`/api/todos/${id}`, headers)
      .then(res => {
        console.log('[Todo] DELETE /api/todos/' + id + ' status:', res.status);
        setTodos(prev => prev.filter(t => t.id !== id));
      })
      .catch(err => console.error('[Todo] DELETE error:', err));
  };

  return (
    <div className="dashboard-card dashboard-todo-card">
      <div style={{ fontWeight: 600, marginBottom: 8 }}>오늘의 할 일</div>
      <form onSubmit={handleAddTodo} style={{ marginBottom: 8 }}>
        <input
          type="text"
          value={newTodo}
          onChange={e => setNewTodo(e.target.value)}
          placeholder="할 일을 입력하세요"
          style={{ width: '70%', marginRight: 8 }}
        />
        <button type="submit">추가</button>
      </form>
      {todos.map((todo) => (
        <div
          key={todo.id}
          className={`dashboard-todo-item${todo.done ? ' done' : ''}`}
          style={{ cursor: 'pointer' }}
          onClick={() => handleToggleTodo(todo.id)}
        >
          <input
            type="checkbox"
            checked={todo.done}
            readOnly
            style={{ marginRight: 8 }}
            onClick={e => e.stopPropagation()}
          />
          <span style={{ flex: 1 }}>{todo.text}</span>
          <button
            className="dashboard-todo-delete-btn"
            onClick={e => {
              e.stopPropagation();
              handleDeleteTodo(todo.id);
            }}
            aria-label="할 일 삭제"
            title="삭제"
          >🗑️</button>
        </div>
      ))}
    </div>
  );
}
