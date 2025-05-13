import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { io } from "socket.io-client";
import api from "../utils/axios";
import "../css/VideoRoom.css";

const VideoRoom = () => {
  const { roomId } = useParams();

  const myVideoRef = useRef();
  const socket = useRef();
  const peerConnections = useRef({});
  const localStream = useRef();

  const [user, setUser] = useState({ name: "" });
  const [remoteStreams, setRemoteStreams] = useState([]);

  const servers = {
    iceServers: [{ urls: "stun:stun.l.google.com:19302" }]
  };

  useEffect(() => {
    api.get("/api/user/me", {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
      },
    }).then((res) => {
      setUser(res.data);
      socket.current = io(process.env.REACT_APP_SOCKET_URL);

      navigator.mediaDevices.getUserMedia({ video: true, audio: true })
        .then((stream) => {
          localStream.current = stream;
          myVideoRef.current.srcObject = stream;

          socket.current.emit("join-room", {
            roomId,
            userName: res.data.name,
          });
        })
        .catch((err) => {
          console.error("🎤🎥 권한 오류:", err);
          alert("카메라 또는 마이크 권한이 거부되었습니다.");
        });

      socket.current.on("user-connected", async ({ socketId, userName }) => {
        console.log("상대 입장:", userName, socketId);
        await createPeerConnection(socketId);
        const offer = await peerConnections.current[socketId].createOffer();
        await peerConnections.current[socketId].setLocalDescription(offer);
        socket.current.emit("signal", { roomId, target: socketId, data: { type: "offer", offer } });
      });

      socket.current.on("signal", async ({ sender, data }) => {
        if (!peerConnections.current[sender]) {
          await createPeerConnection(sender);
        }

        const pc = peerConnections.current[sender];

        if (data.type === "offer") {
          await pc.setRemoteDescription(new RTCSessionDescription(data.offer));
          const answer = await pc.createAnswer();
          await pc.setLocalDescription(answer);
          socket.current.emit("signal", { roomId, target: sender, data: { type: "answer", answer } });
        } else if (data.type === "answer") {
          await pc.setRemoteDescription(new RTCSessionDescription(data.answer));
        } else if (data.type === "ice-candidate") {
          await pc.addIceCandidate(new RTCIceCandidate(data.candidate));
        }
      });
    }).catch((err) => {
      console.error("사용자 정보 가져오기 실패:", err);
    });

    return () => {
      socket.current?.disconnect();
      Object.values(peerConnections.current).forEach(pc => pc.close());
      localStream.current?.getTracks().forEach(track => track.stop());
    };
  }, [roomId]);

  const createPeerConnection = async (socketId) => {
    const pc = new RTCPeerConnection(servers);

    localStream.current.getTracks().forEach((track) => {
      pc.addTrack(track, localStream.current);
    });

    pc.ontrack = (event) => {
      setRemoteStreams((prev) => {
        const exists = prev.find(stream => stream.id === event.streams[0].id);
        if (exists) return prev;
        return [...prev, event.streams[0]];
      });
    };

    pc.onicecandidate = (event) => {
      if (event.candidate) {
        socket.current.emit("signal", {
          roomId,
          target: socketId,
          data: { type: "ice-candidate", candidate: event.candidate },
        });
      }
    };

    peerConnections.current[socketId] = pc;
  };

  return (
    <div style={{ display: "flex", flexWrap: "wrap", gap: "10px" }}>
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
            ref={(el) => {
              if (el) el.srcObject = stream;
            }}
          />
          <div className="name-tag">상대방 {idx + 1}</div>
        </div>
      ))}
    </div>
  );
};

export default VideoRoom;
