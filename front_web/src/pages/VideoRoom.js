import React, { useEffect, useRef, useState } from 'react';
import { useParams } from 'react-router-dom';
import io from 'socket.io-client';
import '../css/VideoRoom.css';

// 원격 비디오 컴포넌트
function RemoteVideo({ stream, name }) {
  const videoRef = useRef(null);
  useEffect(() => {
    if (videoRef.current && stream) {
      videoRef.current.srcObject = stream;
    }
  }, [stream]);

  return (
    <div className="video-box">
      <video ref={videoRef} autoPlay playsInline className="video" />
      <div className="name-tag">{name}</div>
    </div>
  );
}

export default function VideoRoom() {
  const { roomId } = useParams();
  const [user, setUser] = useState({ user_id: null, name: '' });

  const localVideoRef = useRef(null);
  const socketRef     = useRef(null);
  const peersRef      = useRef({});
  const localStream   = useRef(null);

  const [remoteStreams, setRemoteStreams] = useState([]);
  const servers = { iceServers: [{ urls: 'stun:stun.l.google.com:19302' }] };

  useEffect(() => {
    const stored = localStorage.getItem('user');
    if (!stored) {
      alert('로그인이 필요합니다.');
      return;
    }
    const parsed = JSON.parse(stored);
    setUser(parsed);

    socketRef.current = io('http://localhost:4000');

    navigator.mediaDevices.getUserMedia({ video: true, audio: true })
      .then(stream => {
        localStream.current = stream;
        localVideoRef.current.srcObject = stream;
        socketRef.current.emit('join-room', {
          roomId,
          userId: parsed.user_id,
          name: parsed.name
        });
      })
      .catch(err => {
        console.error('getUserMedia 실패:', err);
        alert('카메라/마이크 권한이 필요합니다.');
      });

    socketRef.current.on('user-connected', ({ socketId, name }) => {
      createOffer(socketId, name);
    });

    socketRef.current.on('user-disconnected', remoteId => {
      if (peersRef.current[remoteId]) {
        peersRef.current[remoteId].close();
        delete peersRef.current[remoteId];
      }
      setRemoteStreams(list => list.filter(item => item.id !== remoteId));
    });

    socketRef.current.on('signal', async msg => {
      const { from, to, type, payload, name } = msg;
      if (to !== socketRef.current.id) return;

      if (!peersRef.current[from]) {
        await createPeerConnection(from, name);
      }
      const pc = peersRef.current[from];
      try {
        if (type === 'offer') {
          await pc.setRemoteDescription(new RTCSessionDescription(payload));
          const answer = await pc.createAnswer();
          await pc.setLocalDescription(answer);
          socketRef.current.emit('signal', {
            roomId,
            from: socketRef.current.id,
            to: from,
            type: 'answer',
            payload: pc.localDescription
          });
        } else if (type === 'answer') {
          await pc.setRemoteDescription(new RTCSessionDescription(payload));
        } else if (type === 'ice-candidate' && payload) {
          await pc.addIceCandidate(new RTCIceCandidate(payload));
        }
      } catch (e) {
        console.error('signaling 처리 오류:', e);
      }
    });

    return () => {
      socketRef.current.disconnect();
      localStream.current?.getTracks().forEach(t => t.stop());
      Object.values(peersRef.current).forEach(pc => pc.close());
    };
  }, [roomId]);

  const createPeerConnection = async (remoteId, remoteName) => {
    const pc = new RTCPeerConnection(servers);
    localStream.current.getTracks().forEach(track => pc.addTrack(track, localStream.current));

    pc.onicecandidate = e => {
      if (e.candidate) {
        socketRef.current.emit('signal', {
          roomId,
          from: socketRef.current.id,
          to: remoteId,
          type: 'ice-candidate',
          payload: e.candidate
        });
      }
    };

    pc.ontrack = e => {
      setRemoteStreams(list => {
        if (list.some(item => item.id === remoteId)) return list;
        return [...list, { id: remoteId, stream: e.streams[0], name: remoteName }];
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

  const createOffer = async (remoteId, remoteName) => {
    const pc = await createPeerConnection(remoteId, remoteName);
    const offer = await pc.createOffer();
    await pc.setLocalDescription(offer);
    socketRef.current.emit('signal', {
      roomId,
      from: socketRef.current.id,
      to: remoteId,
      type: 'offer',
      payload: pc.localDescription
    });
  };

  return (
    <div className="video-room-container">
      <div className="video-box">
        <video ref={localVideoRef} autoPlay muted playsInline className="video" />
        <div className="name-tag">{user.name || '나'}</div>
      </div>
      {remoteStreams.map(r => <RemoteVideo key={r.id} stream={r.stream} name={r.name} />)}
    </div>
  );
}
