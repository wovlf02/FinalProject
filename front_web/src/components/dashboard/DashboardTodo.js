import React, {useEffect, useState} from 'react';
import moment from 'moment';
import api from '../../api/api';

const DashboardTodo = ({selectedDate}) => {
    const [todos, setTodos] = useState([]);
    const [newTodo, setNewTodo] = useState('');

    // ✅ 선택된 날짜 기준으로 할 일 목록 불러오기
    useEffect(() => {
        if (!selectedDate) return;
        fetchTodos();
    }, [selectedDate]);

    const fetchTodos = async () => {
        try {
            const res = await api.get('/dashboard/todos', {
                params: {date: moment(selectedDate).format('YYYY-MM-DD')},
            });
            setTodos(res.data);
        } catch (error) {
            console.error('할 일 조회 실패:', error);
        }
    };

    // ✅ 할 일 추가
    const handleAddTodo = async (e) => {
        e.preventDefault();
        if (!newTodo.trim()) return;
        try {
            await api.post('/dashboard/todos', {
                title: newTodo,
                description: '',
                date: moment(selectedDate).format('YYYY-MM-DD'),
                priority: 'NORMAL', // 서버에서는 Enum (LOW/MEDIUM/HIGH/NORMAL 등) 문자열 허용해야 함
            });
            setNewTodo('');
            fetchTodos();
        } catch (error) {
            console.error('할 일 추가 실패:', error);
        }
    };

    // ✅ 완료 상태 토글
    const handleToggle = async (todoId) => {
        try {
            const res = await api.put(`/dashboard/todos/${todoId}/complete`);
            setTodos((prev) =>
                prev.map((todo) => (todo.id === todoId ? res.data : todo))
            );
        } catch (error) {
            console.error('완료 토글 실패:', error);
        }
    };

    // ✅ 할 일 삭제
    const handleDelete = async (todoId) => {
        try {
            await api.delete(`/dashboard/todos/${todoId}`);
            setTodos((prev) => prev.filter((todo) => todo.id !== todoId));
        } catch (error) {
            console.error('삭제 실패:', error);
        }
    };

    return (
        <div className="dashboard-card dashboard-todo-card">
            <div style={{color: '#222', fontWeight: 600, marginBottom: 8}}>
                {moment(selectedDate).format('YYYY년 M월 D일')}의 할 일
            </div>
            <form onSubmit={handleAddTodo} style={{marginBottom: 8}}>
                <input
                    type="text"
                    value={newTodo}
                    onChange={(e) => setNewTodo(e.target.value)}
                    placeholder="할 일을 입력하세요"
                    style={{width: '70%', marginRight: 8}}
                />
                <button type="submit">추가</button>
            </form>
            {todos.map((todo) => (
                <div
                    key={todo.id}
                    className={`dashboard-todo-item${todo.completed ? ' done' : ''}`}
                    style={{cursor: 'pointer'}}
                    onClick={() => handleToggle(todo.id)}
                >
                    <input
                        type="checkbox"
                        checked={todo.completed}
                        readOnly
                        style={{marginRight: 8}}
                        onClick={(e) => e.stopPropagation()}
                    />
                    <span style={{flex: 1}}>{todo.title}</span>
                    <button
                        className="dashboard-todo-delete-btn"
                        onClick={(e) => {
                            e.stopPropagation();
                            handleDelete(todo.id);
                        }}
                        aria-label="삭제"
                        title="삭제"
                    >
                        🗑️
                    </button>
                </div>
            ))}
        </div>
    );
};

export default DashboardTodo;
