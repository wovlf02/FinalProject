import React, {useEffect, useState, useRef} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import * as faceapi from 'face-api.js';
import '../css/CamStudyPage.css';

const CamStudyPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const {initialTime = 1500, unitName = '개인 공부'} = location.state || {};

    const [secondsLeft, setSecondsLeft] = useState(initialTime);
    const [isPaused, setIsPaused] = useState(false);
    const [isFinished, setIsFinished] = useState(false);
    const [faceDetected, setFaceDetected] = useState(false);
    const [modelsLoaded, setModelsLoaded] = useState(false);
    const videoRef = useRef(null);
    const faceIntervalRef = useRef(null);

    // 모델 로드
    useEffect(() => {
        const loadModels = async () => {
            try {
                await faceapi.nets.ssdMobilenetv1.loadFromUri('/models');
                await faceapi.nets.faceLandmark68Net.loadFromUri('/models');
                await faceapi.nets.faceRecognitionNet.loadFromUri('/models');
                setModelsLoaded(true);
            } catch (error) {
                alert('모델 로드 실패: ' + error);
            }
        };
        loadModels();
    }, []);

    // 웹캠 설정 (해상도 명시)
    useEffect(() => {
        if (!modelsLoaded) return;
        const startVideo = async () => {
            try {
                const stream = await navigator.mediaDevices.getUserMedia({
                    video: {width: {ideal: 960}, height: {ideal: 720}}
                });
                videoRef.current.srcObject = stream;
            } catch (error) {
                alert('카메라 접근에 실패했습니다. 권한을 허용해 주세요.');
            }
        };
        startVideo();
    }, [modelsLoaded]);

    // 얼굴 감지
    useEffect(() => {
        const detectFace = async () => {
            if (!videoRef.current) return;
            const detections = await faceapi.detectAllFaces(videoRef.current);
            setFaceDetected(detections.length > 0);
        };
        if (modelsLoaded && !isFinished) {
            faceIntervalRef.current = setInterval(detectFace, 700);
        }
        return () => clearInterval(faceIntervalRef.current);
    }, [modelsLoaded, isFinished]);

    // 타이머 제어 (얼굴 인식 + 일시정지)
    useEffect(() => {
        let timer;
        if (faceDetected && !isPaused && !isFinished && secondsLeft > 0) {
            timer = setInterval(() => {
                setSecondsLeft(prev => prev - 1);
            }, 1000);
        }
        if ((!faceDetected || isPaused || isFinished) && timer) {
            clearInterval(timer);
        }
        if (secondsLeft === 0 && !isFinished) {
            setIsFinished(true);
            setIsPaused(true);
            // 단원평가 모달
            setTimeout(() => {
                if (window.confirm('공부 시간이 종료되었습니다!\n단원평가를 하시겠습니까?')) {
                    navigate('/unit-evaluation', {state: {unitName}});
                }
            }, 100); // alert와 충돌 방지용
        }
        return () => clearInterval(timer);
    }, [faceDetected, isPaused, secondsLeft, isFinished, unitName, navigate]);

    // 시간 포맷
    const formatTime = sec => {
        const h = String(Math.floor(sec / 3600)).padStart(2, '0');
        const m = String(Math.floor((sec % 3600) / 60)).padStart(2, '0');
        const s = String(sec % 60).padStart(2, '0');
        return `${h}:${m}:${s}`;
    };

    return (
        <div className="camstudy-root">
            <div className="camstudy-container">
                <video
                    ref={videoRef}
                    autoPlay
                    muted
                    className="camstudy-video"
                />
                {!modelsLoaded && <span>모델 로딩 중...</span>}

                {/* 단원명 */}
                <div className="camstudy-unitname">
                    {unitName}
                </div>

                {/* 타이머 및 버튼 */}
                <div className="camstudy-timer-row">
          <span className="camstudy-timer">
            <span className="camstudy-timer-dot"/>
              {formatTime(secondsLeft)}
          </span>
                    <button
                        onClick={() => setIsPaused(false)}
                        disabled={!isPaused || isFinished || !faceDetected}
                        className="camstudy-btn-play"
                    >
                        ▶ 재개
                    </button>
                    <button
                        onClick={() => setIsPaused(true)}
                        disabled={isPaused || isFinished}
                        className="camstudy-btn-pause"
                    >
                        ■ 휴식
                    </button>
                </div>

                {/* 집중 알림 */}
                <div className="camstudy-alert">
                    <span className="camstudy-alert-icon">⚠️</span>
                    <div>
                        <div className="camstudy-alert-title">학습 집중 알림</div>
                        <div className="camstudy-alert-desc">
                            효과적인 학습을 위해 바른 자세를 유지하고, 주변 소음을 차단하여 집중력을 높여주세요.
                        </div>
                    </div>
                </div>
                {/* 얼굴 미인식 안내 */}
                {!faceDetected && modelsLoaded && (
                    <div className="camstudy-warning">
                        얼굴이 인식되지 않으면 타이머가 멈춥니다.
                    </div>
                )}
            </div>
        </div>
    );
};

export default CamStudyPage;
