import React, { useState, useEffect } from 'react';
import api from '../../services/api';

const DashboardDday = () => {
    const [examTitle, setExamTitle] = useState('');
    const [examSubject, setExamSubject] = useState('');
    const [examDate, setExamDate] = useState('');
    const [examSchedules, setExamSchedules] = useState([]);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const fetchExamSchedules = async () => {
        try {
            const response = await api.get('/dashboard/exams');
            if (response.status === 200) {
                const schedules = response.data.map(schedule => ({
                    id: schedule.id,
                    title: schedule.title,
                    subject: schedule.subject,
                    examDate: schedule.exam_date,
                    description: schedule.description,
                    location: schedule.location
                }));
                setExamSchedules(schedules);
            }
        } catch (error) {
            console.error('📅 시험 일정 조회 실패:', error);
        }
    };

    useEffect(() => {
        fetchExamSchedules();
    }, []);

    const saveExamSetting = async () => {
        try {
            setError('');
            setSuccess('');

            if (!examTitle || !examDate) {
                setError('시험 제목과 날짜는 필수입니다.');
                return;
            }

            const response = await api.post('/dashboard/exams/register', {
                title: examTitle,
                subject: examSubject,
                exam_date: examDate
            });

            if (response.status === 200) {
                setSuccess('시험 일정이 등록되었습니다.');
                setExamTitle('');
                setExamSubject('');
                setExamDate('');
                await fetchExamSchedules();
            }
        } catch (error) {
            console.error('📅 시험 일정 저장 실패:', error);
            if (error.response?.data?.message) {
                setError(error.response.data.message);
            } else {
                setError('시험 일정 등록에 실패했습니다.');
            }
        }
    };

    return (
        <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-bold mb-4">시험 일정 관리</h2>
            <div className="mb-4">
                <p className="text-sm text-gray-600 mb-2">최대 3개의 시험 일정을 등록할 수 있습니다. (현재 {examSchedules.length}/3)</p>
                {error && <p className="text-red-500 text-sm mb-2">{error}</p>}
                {success && <p className="text-green-500 text-sm mb-2">{success}</p>}
                <div className="grid grid-cols-1 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">시험 제목</label>
                        <input
                            type="text"
                            value={examTitle}
                            onChange={(e) => setExamTitle(e.target.value)}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                            placeholder="예: 중간고사"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">과목</label>
                        <input
                            type="text"
                            value={examSubject}
                            onChange={(e) => setExamSubject(e.target.value)}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                            placeholder="예: 수학"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">시험 날짜</label>
                        <input
                            type="date"
                            value={examDate}
                            onChange={(e) => setExamDate(e.target.value)}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        />
                    </div>
                </div>
                <button
                    onClick={saveExamSetting}
                    disabled={examSchedules.length >= 3}
                    className={`mt-4 w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white 
                        ${examSchedules.length >= 3 
                            ? 'bg-gray-400 cursor-not-allowed' 
                            : 'bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500'}`}
                >
                    {examSchedules.length >= 3 ? '최대 등록 가능한 시험 일정에 도달했습니다' : '시험 일정 등록'}
                </button>
            </div>
            <div className="mt-6">
                <h3 className="text-lg font-medium mb-3">등록된 시험 일정</h3>
                {examSchedules.length === 0 ? (
                    <p className="text-gray-500 text-sm">등록된 시험 일정이 없습니다.</p>
                ) : (
                    <div className="space-y-3">
                        {examSchedules.map((schedule) => (
                            <div key={schedule.id} className="border rounded-lg p-3">
                                <div className="flex justify-between items-start">
                                    <div>
                                        <h4 className="font-medium">{schedule.title}</h4>
                                        <p className="text-sm text-gray-600">{schedule.subject}</p>
                                        <p className="text-sm text-gray-500">{schedule.examDate}</p>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default DashboardDday; 