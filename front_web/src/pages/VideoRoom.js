import React, { useEffect, useRef, useState } from 'react';
import { useParams } from 'react-router-dom';
import io from 'socket.io-client';
import '../css/VideoRoom.css';

const VideoRoom = () => {
  const { roomId } = useParams();
  const [user, setUser] = useState({ user_id: null, name: '' });
  const myVideoRef = useRef(null);
  const remoteVideoRef = useRef(null);
  const socket = useRef(null);
  const peerConnection = useRef(null);
  const localStream = useRef(null);

  const servers = {
    iceServers: [{ urls: 'stun:stun.l.google.com:19302' }]
  };

  useEffect(() => {
    // 1) localStorage에서 로그인 사용자 정보 꺼내기
    const stored = localStorage.getItem('user');
    if (!stored) {
      alert('로그인이 필요합니다.');
      return;
    }
    const parsed = JSON.parse(stored);
    setUser(parsed);

    // 2) socket.io 연결
    socket.current = io('http://localhost:4000', {
      query: { userId: parsed.user_id }
    });

    // 3) 미디어 스트림 얻어서 내 비디오에 붙이고, 방 참가 이벤트
    navigator.mediaDevices.getUserMedia({ video: true, audio: true })
      .then(stream => {
        localStream.current = stream;
        myVideoRef.current.srcObject = stream;
        socket.current.emit('join-room', roomId);
      })
      .catch(err => {
        console.error('권한 오류:', err);
        alert('카메라/마이크 권한이 필요합니다.');
      });

    // 4) 다른 사용자가 들어오면 Offer 생성
    socket.current.on('user-connected', async () => {
      await createOffer();
    });

    // 5) signal 이벤트 처리
    socket.current.on('signal', async ({ type, data }) => {
      if (!peerConnection.current) {
        await createPeerConnection();
      }
      if (type === 'offer') {
        await peerConnection.current.setRemoteDescription(data.offer);
        const answer = await peerConnection.current.createAnswer();
        await peerConnection.current.setLocalDescription(answer);
        socket.current.emit('signal', { roomId, type: 'answer', data: { answer } });
      } else if (type === 'answer') {
        await peerConnection.current.setRemoteDescription(data.answer);
      } else if (type === 'ice-candidate') {
        await peerConnection.current.addIceCandidate(data.candidate);
      }
    });

    // cleanup
    return () => {
      socket.current.disconnect();
      localStream.current?.getTracks().forEach(t => t.stop());
    };
  }, [roomId]);

  const createPeerConnection = async () => {
    peerConnection.current = new RTCPeerConnection(servers);
    localStream.current.getTracks().forEach(track => {
      peerConnection.current.addTrack(track, localStream.current);
    });
    peerConnection.current.ontrack = e => {
      remoteVideoRef.current.srcObject = e.streams[0];
    };
    peerConnection.current.onicecandidate = e => {
      if (e.candidate) {
        socket.current.emit('signal', {
          roomId,
          type: 'ice-candidate',
          data: { candidate: e.candidate }
        });
      }
    };
  };

  const createOffer = async () => {
    await createPeerConnection();
    const offer = await peerConnection.current.createOffer();
    await peerConnection.current.setLocalDescription(offer);
    socket.current.emit('signal', { roomId, type: 'offer', data: { offer } });
  };

  return (
    <div className="video-room-container">
      <div className="video-box">
        <video ref={myVideoRef} autoPlay muted playsInline className="video" />
        <div className="name-tag">{user.name}</div>
      </div>
      <div className="video-box">
        <video ref={remoteVideoRef} autoPlay playsInline className="video" />
        <div className="name-tag">상대방</div>
      </div>
    </div>
  );
};

export default VideoRoom;
