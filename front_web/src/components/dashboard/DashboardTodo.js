import React, { useState, useEffect } from 'react';
import moment from 'moment';
import api from '../../api/api';
import './DashboardTodo.css';

const DashboardTodo = () => {
    const [todos, setTodos] = useState([]);
    const [newTodo, setNewTodo] = useState('');
    const [selectedDate, setSelectedDate] = useState(moment().format('YYYY-MM-DD'));
    const [priority, setPriority] = useState('NORMAL');
    const [isModalOpen, setIsModalOpen] = useState(false);

    useEffect(() => {
        fetchTodos();
    }, []);

    const fetchTodos = async () => {
        try {
            const response = await api.get('/dashboard/todos');
            setTodos(response.data);
        } catch (error) {
            console.error('Error fetching todos:', error);
            alert('할일 목록을 불러오는 중 오류가 발생했습니다.');
        }
    };

    const handleAddTodo = async () => {
        if (!newTodo.trim()) {
            alert('할일을 입력해주세요.');
            return;
        }

        try {
            // 선택된 날짜를 YYYY-MM-DD 형식으로 변환
            const formattedDate = moment(selectedDate).format('YYYY-MM-DD');
            console.log('Selected date:', selectedDate);
            console.log('Formatted date:', formattedDate);

            const todoData = {
                title: newTodo,
                description: '',
                date: formattedDate,
                priority: priority
            };

            console.log('Sending todo data:', todoData);

            const response = await api.post('/dashboard/todos', todoData);
            console.log('Todo added successfully:', response.data);

            setNewTodo('');
            setIsModalOpen(false);
            fetchTodos();
        } catch (error) {
            console.error('Error adding todo:', error);
            alert('할일 추가 중 오류가 발생했습니다.');
        }
    };

    const handleToggleTodo = async (todoId) => {
        try {
            const requestData = { todoId: Number(todoId) };
            console.log('Sending toggle request:', requestData);
            const response = await api.put('/dashboard/todos/complete', requestData);
            console.log('Toggle response:', response.data);
            fetchTodos();
        } catch (error) {
            console.error('Error toggling todo:', error);
            console.error('Request data:', { todoId });
            console.error('Error details:', error.response?.data);
            alert('할일 상태 변경 중 오류가 발생했습니다.');
        }
    };

    return (
        <div className="dashboard-todo">
            <div className="dashboard-todo-header">
                <h3>할일 목록</h3>
                <button onClick={() => setIsModalOpen(true)}>+ 새 할일</button>
            </div>

            {isModalOpen && (
                <div className="dashboard-modal">
                    <h3>할일 추가</h3>
                    <div className="dashboard-modal-content">
                        <div className="dashboard-modal-input-group">
                            <input
                                type="text"
                                value={newTodo}
                                onChange={(e) => setNewTodo(e.target.value)}
                                placeholder="할 일을 입력하세요"
                            />
                            <select
                                value={priority}
                                onChange={(e) => setPriority(e.target.value)}
                                className="priority-select"
                            >
                                <option value="LOW">낮음</option>
                                <option value="NORMAL">보통</option>
                                <option value="HIGH">높음</option>
                            </select>
                        </div>
                        <div className="date-picker">
                            <input
                                type="date"
                                value={selectedDate}
                                onChange={(e) => {
                                    console.log('Date selected:', e.target.value);
                                    setSelectedDate(e.target.value);
                                }}
                            />
                        </div>
                        <div className="modal-buttons">
                            <button onClick={handleAddTodo}>추가</button>
                            <button onClick={() => setIsModalOpen(false)}>취소</button>
                        </div>
                    </div>
                </div>
            )}

            <div className="todo-list">
                {todos.length === 0 ? (
                    <div className="no-todos">등록된 할일이 없습니다.</div>
                ) : (
                    todos.map((todo) => (
                        <div key={todo.id} className={`todo-item ${todo.completed ? 'completed' : ''}`}>
                            <input
                                type="checkbox"
                                checked={todo.completed}
                                onChange={() => handleToggleTodo(todo.id)}
                            />
                            <span className="todo-title">{todo.title}</span>
                            <span className={`priority-badge ${todo.priority.toLowerCase()}`}>
                                {todo.priority}
                            </span>
                            <span className="todo-date">{moment(todo.date).format('YYYY-MM-DD')}</span>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default DashboardTodo;
