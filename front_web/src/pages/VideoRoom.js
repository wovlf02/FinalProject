import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import io from "socket.io-client";
import "../css/VideoRoom.css";
import api from "../api/api";

const VideoRoom = () => {
    const { roomId } = useParams();

    const myVideoRef = useRef();
    const socket = useRef();
    const localStream = useRef();

    const peerConnections = useRef(new Map()); // socketId -> RTCPeerConnection
    const [remoteStreams, setRemoteStreams] = useState([]); // [{ id, stream }]
    const [user, setUser] = useState({ name: "" });

    const servers = {
        iceServers: [{ urls: "stun:stun.l.google.com:19302" }]
    };

    useEffect(() => {
        // ✅ 세션 기반 사용자 정보 요청
        api.get("/users/me", { withCredentials: true })
            .then((res) => {
                setUser(res.data?.data || {});
            })
            .catch((err) => {
                console.error("사용자 정보 가져오기 실패:", err);
            });

        // ✅ 소켓 연결 (세션 인증 포함)
        socket.current = io("https://4acf-2001-2d8-699b-7a4b-f170-d29-bd64-7e6e.ngrok-free.app", {
            withCredentials: true // 핵심!
        });

        // ✅ 카메라/마이크 권한 요청
        navigator.mediaDevices.getUserMedia({ video: true, audio: true })
            .then((stream) => {
                localStream.current = stream;
                myVideoRef.current.srcObject = stream;
                socket.current.emit("join-room", roomId);
            })
            .catch((err) => {
                console.error("🎤🎥 권한 오류:", err);
                alert("카메라 또는 마이크 권한이 거부되었습니다.");
            });

        // ✅ 새로운 유저 연결 수신
        socket.current.on("user-connected", async (userId) => {
            const pc = createPeerConnection(userId);
            peerConnections.current.set(userId, pc);

            const offer = await pc.createOffer();
            await pc.setLocalDescription(offer);

            socket.current.emit("signal", {
                roomId,
                data: { type: "offer", offer, targetId: userId, senderId: socket.current.id }
            });
        });

        // ✅ 시그널 처리
        socket.current.on("signal", async ({ type, offer, answer, candidate, senderId }) => {
            let pc = peerConnections.current.get(senderId);
            if (!pc) {
                pc = createPeerConnection(senderId);
                peerConnections.current.set(senderId, pc);
            }

            try {
                if (type === "offer") {
                    await pc.setRemoteDescription(new RTCSessionDescription(offer));
                    const answer = await pc.createAnswer();
                    await pc.setLocalDescription(answer);
                    socket.current.emit("signal", {
                        roomId,
                        data: { type: "answer", answer, targetId: senderId, senderId: socket.current.id }
                    });
                } else if (type === "answer") {
                    await pc.setRemoteDescription(new RTCSessionDescription(answer));
                } else if (type === "ice-candidate") {
                    await pc.addIceCandidate(new RTCIceCandidate(candidate));
                }
            } catch (err) {
                console.error("🚨 시그널 처리 오류:", err);
            }
        });

        return () => {
            socket.current.disconnect();
            localStream.current?.getTracks().forEach(track => track.stop());
        };
    }, [roomId]);

    const createPeerConnection = (targetId) => {
        const pc = new RTCPeerConnection(servers);

        localStream.current.getTracks().forEach((track) => {
            pc.addTrack(track, localStream.current);
        });

        pc.ontrack = (event) => {
            const stream = event.streams[0];
            setRemoteStreams((prev) => {
                if (prev.find(r => r.id === targetId)) return prev; // 중복 방지
                return [...prev, { id: targetId, stream }];
            });
        };

        pc.onicecandidate = (event) => {
            if (event.candidate) {
                socket.current.emit("signal", {
                    roomId,
                    data: {
                        type: "ice-candidate",
                        candidate: event.candidate,
                        targetId: targetId,
                        senderId: socket.current.id
                    }
                });
            }
        };

        return pc;
    };

    return (
        <div style={{ display: "flex", gap: "10px", flexWrap: "wrap" }}>
            <div className="video-box">
                <video ref={myVideoRef} autoPlay muted playsInline className="video" />
                <div className="name-tag">{user.name}</div>
            </div>

            {remoteStreams.map(({ id, stream }) => (
                <div key={id} className="video-box">
                    <video
                        ref={(el) => el && (el.srcObject = stream)}
                        autoPlay
                        playsInline
                        className="video"
                    />
                    <div className="name-tag">상대방 ({id.slice(0, 5)})</div>
                </div>
            ))}
        </div>
    );
};

export default VideoRoom;
