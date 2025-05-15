// ✅ 채팅, 참여자 리스트, WebRTC 다대다 연결 + 디버깅 로그 VideoRoom.js

import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { io } from "socket.io-client";
import api from "../utils/axios";
import "../css/VideoRoom.css";

const socket = io(process.env.REACT_APP_SOCKET_URL);


const VideoRoom = () => {
  const { roomId } = useParams();
  const myVideoRef = useRef();
  const socketRef = useRef();
  const peerConnections = useRef({});
  const localStream = useRef();

  const [user, setUser] = useState({ name: "" });
  const [remoteStreams, setRemoteStreams] = useState([]);
  const [userCount, setUserCount] = useState(0);
  const [chatMessages, setChatMessages] = useState([]);
  const [message, setMessage] = useState("");
  const [participants, setParticipants] = useState([]);

  const servers = { iceServers: [{ urls: "stun:stun.l.google.com:19302" }] };

  useEffect(() => {
    const token = localStorage.getItem("accessToken");

    api.get("/api/user/me", {
      headers: { Authorization: `Bearer ${token}` },
    }).then(async (res) => {
      setUser(res.data);
      socketRef.current = socket;

      const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
      localStream.current = stream;
      myVideoRef.current.srcObject = stream;

      socket.emit("join-room", { roomId, userName: res.data.name });

      socket.on("all-users", async (users) => {
        console.log("📥 all-users 수신:", users);
        setParticipants(users.map(u => u.userName));
        for (const { socketId } of users) {
          if (!peerConnections.current[socketId]) {
            await createPeerConnection(socketId);
            const offer = await peerConnections.current[socketId].createOffer();
            await peerConnections.current[socketId].setLocalDescription(offer);
            socket.emit("signal", {
              roomId,
              target: socketId,
              data: { type: "offer", offer },
            });
            console.log("📤 offer 전송 to", socketId);
          }
        }
      });

      socket.on("user-connected", async ({ socketId, userName }) => {
        console.log("➕ user-connected:", socketId, userName);
        setParticipants(prev => [...prev, userName]);
        if (!peerConnections.current[socketId]) {
          await createPeerConnection(socketId);
          const offer = await peerConnections.current[socketId].createOffer();
          await peerConnections.current[socketId].setLocalDescription(offer);
          socket.emit("signal", {
            roomId,
            target: socketId,
            data: { type: "offer", offer },
          });
          console.log("📤 offer 전송 to", socketId);
        }
      });

      socket.on("user-disconnected", ({ socketId, userName }) => {
        console.log("➖ user-disconnected:", socketId);
        peerConnections.current[socketId]?.close();
        delete peerConnections.current[socketId];
        setRemoteStreams((prev) => prev.filter((s) => s.id !== socketId));
        setParticipants((prev) => prev.filter(name => name !== userName));
      });

      socket.on("signal", async ({ sender, data }) => {
        console.log("📡 signal 수신:", sender, data.type);
        if (!peerConnections.current[sender]) {
          console.log("🛠 PeerConnection 없음 → 생성:", sender);
          await createPeerConnection(sender);
        }
        const pc = peerConnections.current[sender];

        if (data.type === "offer") {
          console.log("🟡 offer 수신");
          await pc.setRemoteDescription(new RTCSessionDescription(data.offer));
          const answer = await pc.createAnswer();
          await pc.setLocalDescription(answer);
          socket.emit("signal", {
            roomId,
            target: sender,
            data: { type: "answer", answer },
          });
          console.log("📤 answer 전송 to", sender);
        } else if (data.type === "answer") {
          console.log("🟢 answer 수신");
          await pc.setRemoteDescription(new RTCSessionDescription(data.answer));
        } else if (data.type === "ice-candidate") {
          console.log("🧊 ICE 수신");
          await pc.addIceCandidate(new RTCIceCandidate(data.candidate));
        }
      });

      socket.on("chat", ({ message, senderId }) => {
        setChatMessages(prev => [...prev, { senderId, message }]);
      });

      socket.on("user-count", (count) => setUserCount(count));
    });

    return () => {
      socket.emit("leave-room", { roomId, userName: user.name });
      socket.disconnect();
      Object.values(peerConnections.current).forEach((pc) => pc.close());
      localStream.current?.getTracks().forEach((track) => track.stop());
    };
  }, [roomId]);

  const createPeerConnection = async (socketId) => {
    console.log("🔧 createPeerConnection:", socketId);
    const pc = new RTCPeerConnection(servers);

    localStream.current.getTracks().forEach((track) => {
      pc.addTrack(track, localStream.current);
      console.log("🎙 트랙 추가됨:", track.kind);
    });

    pc.ontrack = (event) => {
      console.log("🎥 ontrack 발생:", event.streams[0]);
      setRemoteStreams((prev) => {
        const exists = prev.find((s) => s.id === event.streams[0].id);
        if (exists) return prev;
        return [...prev, event.streams[0]];
      });
    };

    pc.onicecandidate = (event) => {
      if (event.candidate) {
        console.log("📤 ICE 전송 to", socketId);
        socket.emit("signal", {
          roomId,
          target: socketId,
          data: { type: "ice-candidate", candidate: event.candidate },
        });
      }
    };

    peerConnections.current[socketId] = pc;
  };

  const sendMessage = () => {
    if (message.trim()) {
      socket.emit("chat", { roomId, message, senderId: user.name });
      setChatMessages(prev => [...prev, { senderId: user.name, message }]);
      setMessage("");
    }
  };

  return (
    <div className="video-room-container">
      <div className="user-count-box">참여자 수: {userCount}</div>
      <div className="participant-list">
        <strong>참여자 목록:</strong>
        <ul>{participants.map((name, i) => <li key={i}>{name}</li>)}</ul>
      </div>

      <div className="video-grid">
        <div className="video-box">
          <video ref={myVideoRef} autoPlay muted playsInline className="video" />
          <div className="name-tag">나 ({user.name})</div>
        </div>
        {remoteStreams.map((stream, idx) => (
          <div className="video-box" key={stream.id || idx}>
            <video
              autoPlay
              playsInline
              className="video"
              ref={(el) => { if (el) el.srcObject = stream; }}
            />
            <div className="name-tag">상대방 {idx + 1}</div>
          </div>
        ))}
      </div>

      <div className="chat-container">
        <div className="chat-messages">
          {chatMessages.map((msg, i) => (
            <div key={i} className="chat-message">
              <strong>{msg.senderId}:</strong> {msg.message}
            </div>
          ))}
        </div>
        <div className="chat-input">
          <input
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
            placeholder="메시지 입력..."
          />
          <button onClick={sendMessage}>전송</button>
        </div>
      </div>
    </div>
  );
};

export default VideoRoom;
