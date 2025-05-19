import React, { useState, useEffect } from 'react';
import api from '../../api/api';

function DashboardTodoList() {
    const [todos, setTodos] = useState([]);
    const [newTodo, setNewTodo] = useState('');

    // ✅ 오늘 날짜
    const today = new Date().toISOString().slice(0, 10);

    // ✅ 오늘 할 일 불러오기
    useEffect(() => {
        const fetchTodos = async () => {
            try {
                const res = await api.get(`/dashboard/todos?date=${today}`);
                setTodos(res.data);
            } catch (err) {
                console.error('Todo 리스트 불러오기 실패:', err);
                alert('할 일 목록을 불러오는데 실패했습니다.');
            }
        };
        fetchTodos();
    }, [today]);

    // ✅ 새 할 일 추가
    const handleAddTodo = async (e) => {
        e.preventDefault();
        if (newTodo.trim() === '') return;

        try {
            await api.post('/dashboard/todos', {
                title: newTodo,
                description: '',
                todoDate: today,
                priority: 2,
            });
            setNewTodo('');
            const res = await api.get(`/dashboard/todos?date=${today}`);
            setTodos(res.data);
        } catch (err) {
            console.error('할 일 추가 실패:', err);
            alert('할 일을 추가하는 데 실패했습니다.');
        }
    };

    // ✅ 할 일 완료 상태 토글
    const handleToggleTodo = async (todoId) => {
        try {
            const res = await api.put(`/dashboard/todos/${todoId}/complete`);
            setTodos(prev => prev.map(t => t.id === todoId ? res.data : t));
        } catch (err) {
            console.error('상태 변경 실패:', err);
            alert('할 일 상태 변경에 실패했습니다.');
        }
    };

    // ✅ 할 일 삭제
    const handleDeleteTodo = async (todoId) => {
        try {
            await api.delete(`/dashboard/todos/${todoId}`);
            setTodos(prev => prev.filter(t => t.id !== todoId));
        } catch (err) {
            console.error('삭제 실패:', err);
            alert('할 일을 삭제하는 데 실패했습니다.');
        }
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
                        className={`dashboard-todo-item${todo.completed ? ' done' : ''}`}
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'space-between',
                            padding: '6px 8px',
                            borderRadius: '6px',
                            background: todo.completed ? '#f1f5fd' : '#fff',
                            marginBottom: '8px',
                            boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)',
                        }}
                    >
                        <div style={{ display: 'flex', alignItems: 'center' }}>
                            <input
                                type="checkbox"
                                checked={todo.completed}
                                onChange={() => handleToggleTodo(todo.id)}
                                style={{ marginRight: '8px' }}
                            />
                            <span>{todo.title}</span>
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
