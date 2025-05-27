// src/hooks/useWebRTC.js
import { useEffect, useRef, useState } from 'react';
import io from 'socket.io-client';

export default function useWebRTC(roomId, localVideoRef) {
  const [peers, setPeers] = useState({});               // { socketId: RTCPeerConnection }
  const [remoteStreams, setRemoteStreams] = useState([]); // [{ id: socketId, stream }]
  const socketRef = useRef();
  const localStreamRef = useRef();

  useEffect(() => {
    socketRef.current = io('http://localhost:4000');

    // 1) 로컬 카메라/마이크 가져오기
    navigator.mediaDevices.getUserMedia({ video: true, audio: true })
      .then(stream => {
        localStreamRef.current = stream;
        localVideoRef.current.srcObject = stream;

        // 2) 룸에 조인
        socketRef.current.emit('join-room', roomId);

        // 3) 다른 사람이 들어왔을 때 (offerer 역할)
        socketRef.current.on('user-connected', remoteId => {
          // 새로운 PeerConnection 준비
          const pc = createPeerConnection(remoteId, socketRef.current);
          // 내 트랙을 pc 에 추가
          localStreamRef.current.getTracks().forEach(t => pc.addTrack(t, localStreamRef.current));
          setPeers(prev => ({ ...prev, [remoteId]: pc }));
          // offer 생성 및 전송
          pc.createOffer()
            .then(offer => pc.setLocalDescription(offer))
            .then(() => {
              socketRef.current.emit('signal', { roomId, data: { to: remoteId, sdp: pc.localDescription } });
            });
        });

        // 4) 시그널 메시지 처리: offer/answer/ICE
        socketRef.current.on('signal', ({ data }) => {
          const { from, sdp, candidate } = data;
          let pc = peers[from];
          if (!pc) {
            // offer 받는 쪽(answerer)
            pc = createPeerConnection(from, socketRef.current);
            localStreamRef.current.getTracks().forEach(t => pc.addTrack(t, localStreamRef.current));
            setPeers(prev => ({ ...prev, [from]: pc }));
          }

          if (sdp) {
            pc.setRemoteDescription(new RTCSessionDescription(sdp))
              .then(() => {
                if (sdp.type === 'offer') {
                  // answer 생성
                  return pc.createAnswer()
                    .then(answer => pc.setLocalDescription(answer))
                    .then(() => {
                      socketRef.current.emit('signal', {
                        roomId,
                        data: { to: from, sdp: pc.localDescription }
                      });
                    });
                }
              });
          }
          if (candidate) {
            pc.addIceCandidate(new RTCIceCandidate(candidate));
          }
        });
      });

    return () => {
      // cleanup
      socketRef.current.disconnect();
      Object.values(peers).forEach(pc => pc.close());
    };
  }, [roomId, peers]);

  // helper: RTCPeerConnection 세팅
  function createPeerConnection(socketId, socket) {
    const pc = new RTCPeerConnection({
      iceServers: [
        { urls: 'stun:stun.l.google.com:19302' }
      ]
    });

    // ICE candidate 감지 시 서버로 전송
    pc.onicecandidate = e => {
      if (e.candidate) {
        socket.emit('signal', {
          roomId,
          data: { to: socketId, candidate: e.candidate }
        });
      }
    };

    // remote track 수신 시
    pc.ontrack = e => {
      setRemoteStreams(streams => {
        // 이미 있으면 건너뛰기
        if (streams.find(s => s.id === socketId)) return streams;
        return [...streams, { id: socketId, stream: e.streams[0] }];
      });
    };

    return pc;
  }

  return remoteStreams;
}
