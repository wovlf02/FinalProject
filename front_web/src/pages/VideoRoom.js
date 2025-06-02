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
    const peerConnections = useRef(new Map()); // socketId -> { pc, polite }
    const pendingCandidates = useRef(new Map()); // socketId -> ICE 큐
    const [remoteStreams, setRemoteStreams] = useState([]);
    const [user, setUser] = useState({ name: "" });
    const mySocketId = useRef("");

    const servers = {
        iceServers: [
            { urls: "stun:stun.l.google.com:19302" },
            {
                urls: "turn:openrelay.metered.ca:80",
                username: "openrelayproject",
                credential: "openrelayproject"
            }
        ]
    };

    useEffect(() => {
        api.get("/users/me", { withCredentials: true })
            .then((res) => {
                setUser(res.data?.data || {});
            })
            .catch((err) => {
                console.error("❌ 사용자 정보 가져오기 실패:", err);
            });

        // 4000번 포트 url
        socket.current = io("https://e212-121-127-165-110.ngrok-free.app", {
            withCredentials: true,
            transports: ["websocket"]
        });

        socket.current.on("connect", () => {
            mySocketId.current = socket.current.id;
            console.log("✅ Socket 연결됨:", mySocketId.current);
        });

        navigator.mediaDevices.getUserMedia({ video: true, audio: true })
            .then((stream) => {
                localStream.current = stream;
                if (myVideoRef.current) {
                    myVideoRef.current.srcObject = stream;
                }
                socket.current.emit("join-room", roomId);
            })
            .catch((err) => {
                console.error("❌ 미디어 권한 오류:", err);
                alert("카메라 또는 마이크 권한이 거부되었습니다.");
            });

        socket.current.on("user-connected", async (userId) => {
            if (userId === mySocketId.current) return;
            const polite = mySocketId.current > userId;
            const { pc } = createPeerConnection(userId, polite);
            try {
                const offer = await pc.createOffer();
                await pc.setLocalDescription(offer);
                socket.current.emit("signal", {
                    roomId,
                    data: {
                        type: "offer",
                        offer,
                        targetId: userId,
                        senderId: mySocketId.current
                    }
                });
            } catch (err) {
                console.warn("❗ Offer 생성 오류:", err);
            }
        });

        socket.current.on("signal", async ({ type, offer, answer, candidate, senderId }) => {
            if (senderId === mySocketId.current) return;

            let entry = peerConnections.current.get(senderId);
            const polite = mySocketId.current > senderId;
            let pc = entry?.pc;

            if (!pc) {
                const created = createPeerConnection(senderId, polite);
                pc = created.pc;
            }

            try {
                if (type === "offer") {
                    const offerCollision = pc.signalingState !== "stable";

                    if (offerCollision && !polite) {
                        console.warn("❌ Offer 충돌 무시 (impolite):", senderId);
                        return;
                    }

                    if (offerCollision) {
                        console.warn("⚠️ Offer 충돌 → 롤백 실행:", senderId);
                        await pc.setRemoteDescription({ type: "rollback" });
                    }

                    await pc.setRemoteDescription(new RTCSessionDescription(offer));
                    const answer = await pc.createAnswer();
                    await pc.setLocalDescription(answer);

                    socket.current.emit("signal", {
                        roomId,
                        data: {
                            type: "answer",
                            answer,
                            targetId: senderId,
                            senderId: mySocketId.current
                        }
                    });

                    const queued = pendingCandidates.current.get(senderId) || [];
                    for (const c of queued) await pc.addIceCandidate(c);
                    pendingCandidates.current.delete(senderId);

                } else if (type === "answer") {
                    await pc.setRemoteDescription(new RTCSessionDescription(answer));

                    const queued = pendingCandidates.current.get(senderId) || [];
                    for (const c of queued) await pc.addIceCandidate(c);
                    pendingCandidates.current.delete(senderId);

                } else if (type === "ice-candidate") {
                    const ice = new RTCIceCandidate(candidate);
                    if (pc.remoteDescription) {
                        await pc.addIceCandidate(ice);
                    } else {
                        if (!pendingCandidates.current.has(senderId)) {
                            pendingCandidates.current.set(senderId, []);
                        }
                        pendingCandidates.current.get(senderId).push(ice);
                    }
                }
            } catch (err) {
                console.error("🚨 시그널 처리 중 오류:", err);
            }
        });

        return () => {
            socket.current.disconnect();
            localStream.current?.getTracks().forEach(track => track.stop());
            peerConnections.current.forEach(({ pc }) => pc.close());
            peerConnections.current.clear();
        };
    }, [roomId]);

    const createPeerConnection = (targetId, polite) => {
        const pc = new RTCPeerConnection(servers);

        localStream.current.getTracks().forEach((track) => {
            pc.addTrack(track, localStream.current);
        });

        pc.ontrack = (event) => {
            const stream = event.streams[0];
            setRemoteStreams((prev) => {
                if (prev.some(r => r.id === targetId)) return prev;
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
                        senderId: mySocketId.current
                    }
                });
            }
        };

        pc.onconnectionstatechange = () => {
            console.log(`[${targetId}] 연결 상태:`, pc.connectionState);
            if (pc.connectionState === "failed" || pc.connectionState === "disconnected") {
                pc.close();
                peerConnections.current.delete(targetId);
                setRemoteStreams((prev) => prev.filter(r => r.id !== targetId));
            }
        };

        peerConnections.current.set(targetId, { pc, polite });
        return { pc };
    };

    return (
        <div style={{ display: "flex", gap: "10px", flexWrap: "wrap" }}>
            <div className="video-box">
                <video ref={myVideoRef} autoPlay muted playsInline className="video" />
                <div className="name-tag">{user.name}</div>
            </div>

            {remoteStreams.map(({ id, stream }) => (
                <RemoteVideo key={id} id={id} stream={stream} />
            ))}
        </div>
    );
};

const RemoteVideo = ({ id, stream }) => {
    const ref = useRef();
    useEffect(() => {
        if (ref.current) ref.current.srcObject = stream;
    }, [stream]);
    return (
        <div className="video-box">
            <video ref={ref} autoPlay playsInline className="video" />
            <div className="name-tag">상대방 ({id.slice(0, 5)})</div>
        </div>
    );
};

export default VideoRoom;
