import React, { useState, useEffect } from 'react';
import { getStudyTime, updateStudyTime } from '../../api/studyTime';

const DashboardTimeDetail = () => {
    const [studyTime, setStudyTime] = useState({
        weeklyGoalMinutes: 0,
        todayGoalMinutes: 0,
        todayStudyMinutes: 0
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        fetchStudyTime();
    }, []);

    const fetchStudyTime = async () => {
        try {
            const response = await getStudyTime();
            if (response) {
                setStudyTime({
                    weeklyGoalMinutes: response.weeklyGoalMinutes || 0,
                    todayGoalMinutes: response.todayGoalMinutes || 0,
                    todayStudyMinutes: response.todayStudyMinutes || 0
                });
            }
        } catch (error) {
            console.error('공부 시간 조회 실패:', error);
            setError('공부 시간을 불러오는데 실패했습니다.');
        }
    };

    const saveStudyGoal = async () => {
        try {
            setError('');
            setSuccess('');

            const requestData = {
                weeklyGoalMinutes: studyTime.weeklyGoalMinutes || 0,
                todayGoalMinutes: studyTime.todayGoalMinutes || 0,
                todayStudyMinutes: studyTime.todayStudyMinutes || 0
            };

            const response = await updateStudyTime(requestData);

            if (response) {
                setSuccess('목표 시간이 저장되었습니다.');
                await fetchStudyTime();
            }
        } catch (error) {
            console.error('목표 시간 저장 실패:', error);
            setError('목표 시간 저장에 실패했습니다.');
        }
    };

    const handleWeeklyChange = (e) => {
        const value = e.target.value;
        if (value === '' || /^\d*$/.test(value)) {
            setStudyTime(prev => ({
                ...prev,
                weeklyGoalMinutes: value === '' ? 0 : parseInt(value)
            }));
        }
    };

    const handleTodayChange = (e) => {
        const value = e.target.value;
        if (value === '' || /^\d*$/.test(value)) {
            setStudyTime(prev => ({
                ...prev,
                todayGoalMinutes: value === '' ? 0 : parseInt(value)
            }));
        }
    };

    const handleStudyTimeChange = (e) => {
        const value = e.target.value;
        if (value === '' || /^\d*$/.test(value)) {
            setStudyTime(prev => ({
                ...prev,
                todayStudyMinutes: value === '' ? 0 : parseInt(value)
            }));
        }
    };

    return (
        <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-bold mb-4">공부 시간 관리</h2>
            {error && <p className="text-red-500 text-sm mb-2">{error}</p>}
            {success && <p className="text-green-500 text-sm mb-2">{success}</p>}
            <div className="grid grid-cols-1 gap-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700">주간 목표 시간 (분)</label>
                    <input
                        type="text"
                        inputMode="numeric"
                        pattern="[0-9]*"
                        value={studyTime.weeklyGoalMinutes}
                        onChange={handleWeeklyChange}
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        placeholder="주간 목표 시간을 입력하세요"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">오늘 목표 시간 (분)</label>
                    <input
                        type="text"
                        inputMode="numeric"
                        pattern="[0-9]*"
                        value={studyTime.todayGoalMinutes}
                        onChange={handleTodayChange}
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        placeholder="오늘 목표 시간을 입력하세요"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">오늘 공부 시간 (분)</label>
                    <input
                        type="text"
                        inputMode="numeric"
                        pattern="[0-9]*"
                        value={studyTime.todayStudyMinutes}
                        onChange={handleStudyTimeChange}
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        placeholder="오늘 공부 시간을 입력하세요"
                    />
                </div>
            </div>
            <button
                onClick={saveStudyGoal}
                className="mt-4 w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
                목표 시간 저장
            </button>
        </div>
    );
};

export default DashboardTimeDetail; 