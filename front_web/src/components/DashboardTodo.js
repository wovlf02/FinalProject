import React, { useState, useEffect } from 'react';

const DashboardTodo = () => {
  const [todos, setTodos] = useState(() => {
    const saved = localStorage.getItem('todos');
    return saved ? JSON.parse(saved) : [
      { text: "수학 모두노트", done: true },
      { text: "1차 사전예측 복습", done: true },
      { text: "영어 듣기 평가+책", done: true },
      { text: "영단어 DAY6 외우기", done: false },
    ];
  });
  const [newTodo, setNewTodo] = useState('');

  useEffect(() => {
    localStorage.setItem('todos', JSON.stringify(todos));
  }, [todos]);

  const handleAddTodo = (e) => {
    e.preventDefault();
    if (newTodo.trim() === '') return;
    setTodos([...todos, { text: newTodo, done: false }]);
    setNewTodo('');
  };

  const handleToggleTodo = (idx) => {
    setTodos(todos.map((todo, i) =>
      i === idx ? { ...todo, done: !todo.done } : todo
    ));
  };

  const handleDeleteTodo = (idx) => {
    setTodos(todos.filter((_, i) => i !== idx));
  };

  return (
    <div className="dashboard-card dashboard-todo-card">
      <div style={{ color: "#222", fontWeight: 600, marginBottom: 8 }}>
        오늘의 할 일
      </div>
      <form onSubmit={handleAddTodo} style={{ marginBottom: 8 }}>
        <input
          type="text"
          value={newTodo}
          onChange={e => setNewTodo(e.target.value)}
          placeholder="할 일을 입력하세요"
          style={{ width: "70%", marginRight: 8 }}
        />
        <button type="submit">추가</button>
      </form>
      {todos.map((todo, i) => (
        <div
          key={i}
          className={`dashboard-todo-item${todo.done ? ' done' : ''}`}
          style={{ cursor: "pointer" }}
          onClick={() => handleToggleTodo(i)}
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
              handleDeleteTodo(i);
            }}
            aria-label="할 일 삭제"
            title="삭제"
          >🗑️</button>
        </div>
      ))}
    </div>
  );
};

export default DashboardTodo;
