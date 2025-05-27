import React, { useEffect, useRef, useState } from 'react';
import { useParams } from 'react-router-dom';
import io from 'socket.io-client';
import RemoteVideo from './RemoteVideo';

export default function VideoRoom() {
  const { roomId } = useParams();
  const [user, setUser] = useState({ user_id: null, name: '' });

  const localVideoRef = useRef(null);
  const socketRef = useRef(null);
  const peersRef = useRef({}); // { socketId: RTCPeerConnection }
  const localStream = useRef(null);

  const [remoteStreams, setRemoteStreams] = useState([]);

  // STUN/TURN 서버 설정 (TURN 서버는 필요시 추가)
  const servers = {
    iceServers: [
      { urls: 'stun:stun.l.google.com:19302' },
      // { urls: 'turn:your-turn-server.com', username: 'user', credential: 'pass' },
    ],
  };

  useEffect(() => {
    const stored = localStorage.getItem('user');
    if (!stored) {
      alert('로그인이 필요합니다.');
      return;
    }
    const parsed = JSON.parse(stored);
    setUser(parsed);

    socketRef.current = io('http://localhost:4000');

    // 로컬 미디어 스트림 가져오기
    navigator.mediaDevices.getUserMedia({ video: true, audio: true })
      .then(stream => {
        localStream.current = stream;
        if (localVideoRef.current) localVideoRef.current.srcObject = stream;

        // 방 입장
        socketRef.current.emit('join-room', {
          roomId,
          userId: parsed.user_id,
          name: parsed.name,
        });
      })
      .catch(err => {
        console.error('getUserMedia 실패:', err);
        alert('카메라/마이크 권한이 필요합니다.');
      });

    // 기존 참가자 목록 받기 -> 모두에게 offer 생성
    socketRef.current.on('existing-users', (existingUsers) => {
      existingUsers.forEach(remoteId => {
        createOffer(remoteId);
      });
    });

    // 새 사용자 입장 시 offer 생성 (기존 참가자만 처리)
    socketRef.current.on('user-connected', ({ socketId, name }) => {
      // 새로 들어온 사용자에게 offer를 보내지 않습니다.
      // 새로 들어온 사용자가 기존 참가자에게 offer를 보내는 구조이기 때문입니다.
      // (이벤트만 참고로 남겨둡니다.)
    });

    // 시그널 메시지 처리
    socketRef.current.on('signal', async msg => {
      const { from, to, type, payload } = msg;
      if (to !== socketRef.current.id) return;

      if (!peersRef.current[from]) {
        await createPeerConnection(from);
      }
      const pc = peersRef.current[from];
      try {
        if (type === 'offer') {
          await pc.setRemoteDescription(new window.RTCSessionDescription(payload));
          const answer = await pc.createAnswer();
          await pc.setLocalDescription(answer);
          socketRef.current.emit('signal', {
            roomId,
            from: socketRef.current.id,
            to: from,
            type: 'answer',
            payload: pc.localDescription,
          });
        } else if (type === 'answer') {
          await pc.setRemoteDescription(new window.RTCSessionDescription(payload));
        } else if (type === 'ice-candidate' && payload) {
          await pc.addIceCandidate(new window.RTCIceCandidate(payload));
        }
      } catch (e) {
        console.error('signaling 처리 오류:', e);
      }
    });

    // 사용자 퇴장 시
    socketRef.current.on('user-disconnected', remoteId => {
      if (peersRef.current[remoteId]) {
        peersRef.current[remoteId].close();
        delete peersRef.current[remoteId];
      }
      setRemoteStreams(list => list.filter(item => item.id !== remoteId));
    });

    return () => {
      socketRef.current.disconnect();
      localStream.current?.getTracks().forEach(t => t.stop());
      Object.values(peersRef.current).forEach(pc => pc.close());
      setRemoteStreams([]);
    };
    // eslint-disable-next-line
  }, [roomId]);

  // RTCPeerConnection 생성
  const createPeerConnection = async (remoteId) => {
    const pc = new window.RTCPeerConnection(servers);
    localStream.current.getTracks().forEach(track => pc.addTrack(track, localStream.current));

    pc.onicecandidate = e => {
      if (e.candidate) {
        socketRef.current.emit('signal', {
          roomId,
          from: socketRef.current.id,
          to: remoteId,
          type: 'ice-candidate',
          payload: e.candidate,
        });
      }
    };

    pc.ontrack = e => {
      setRemoteStreams(list => {
        if (list.some(item => item.id === remoteId)) return list;
        return [...list, { id: remoteId, stream: e.streams[0], name: remoteId }];
      });
    };

    pc.onconnectionstatechange = () => {
      const state = pc.connectionState;
      if (['disconnected', 'failed', 'closed'].includes(state)) {
        if (peersRef.current[remoteId]) {
          peersRef.current[remoteId].close();
          delete peersRef.current[remoteId];
        }
        setRemoteStreams(list => list.filter(item => item.id !== remoteId));
      }
    };

    peersRef.current[remoteId] = pc;
    return pc;
  };

  // Offer 생성 및 전송
  const createOffer = async (remoteId) => {
    const pc = await createPeerConnection(remoteId);
    const offer = await pc.createOffer();
    await pc.setLocalDescription(offer);
    socketRef.current.emit('signal', {
      roomId,
      from: socketRef.current.id,
      to: remoteId,
      type: 'offer',
      payload: pc.localDescription,
    });
  };

  return (
    <div className="video-room-container">
      <div className="video-box">
        <video ref={localVideoRef} autoPlay muted playsInline className="video" />
        <div className="name-tag">{user.name || '나'}</div>
      </div>
      {remoteStreams.map(r => (
        <RemoteVideo key={r.id} stream={r.stream} name={r.name} />
      ))}
    </div>
  );
}
