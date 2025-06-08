// src/components/chat/FileUploader.js
import React, { useState } from 'react';
import api from '../../api/api';
import '../../css/Chat.css';

const FileUploader = ({ roomId }) => {
    const [selectedFile, setSelectedFile] = useState(null);
    const [uploading, setUploading] = useState(false);

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        setSelectedFile(file);
    };

    const handleUpload = async () => {
        if (!selectedFile) {
            alert('업로드할 파일을 선택해주세요.');
            return;
        }

        const formData = new FormData();
        formData.append('file', selectedFile);
        formData.append('roomId', roomId);

        try {
            setUploading(true);
            await api.post('/study-room/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });

            alert('파일 업로드 성공!');
            setSelectedFile(null);
        } catch (err) {
            console.error('파일 업로드 실패:', err);
            alert('파일 업로드에 실패했습니다.');
        } finally {
            setUploading(false);
        }
    };

    return (
        <div className="file-uploader">
            <input
                type="file"
                onChange={handleFileChange}
                disabled={uploading}
            />
            <button
                onClick={handleUpload}
                disabled={uploading || !selectedFile}
            >
                {uploading ? '업로드 중...' : '파일 업로드'}
            </button>
        </div>
    );
};

export default FileUploader;
