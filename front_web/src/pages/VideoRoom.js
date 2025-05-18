import React, {useEffect, useRef, useState} from "react";
import {useParams} from "react-router-dom";
import io from "socket.io-client";
import axios from "axios";
import "../css/VideoRoom.css";

const VideoRoom = () => {
    const {roomId} = useParams();

    const myVideoRef = useRef();
    const remoteVideoRef = useRef();
    const socket = useRef();
    const peerConnection = useRef(null);
    const localStream = useRef();

    const [user, setUser] = useState({name: ""});

    const servers = {
        iceServers: [{urls: "stun:stun.l.google.com:19302"}]
    };

    useEffect(() => {
        // 사용자 이름 가져오기
        axios.get("/api/user/me", {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
            },
        }).then((res) => {
            setUser(res.data);
        }).catch((err) => {
            console.error("사용자 정보 가져오기 실패:", err);
        });

        // 소켓 연결
        socket.current = io("http://localhost:4000");

        // 미디어 권한 요청
        navigator.mediaDevices.getUserMedia({video: true, audio: true})
            .then((stream) => {
                localStream.current = stream;
                myVideoRef.current.srcObject = stream;

                socket.current.emit("join-room", roomId);
            })
            .catch((err) => {
                console.error("🎤🎥 권한 오류:", err);
                alert("카메라 또는 마이크 권한이 거부되었습니다.");
            });

        socket.current.on("user-connected", async (userId) => {
            console.log("상대 입장:", userId);
            await createOffer();
        });

        socket.current.on("signal", async (data) => {
            if (!peerConnection.current) {
                await createPeerConnection();
            }

            if (data.type === "offer") {
                try {
                    await peerConnection.current.setRemoteDescription(
                        new RTCSessionDescription(data.offer)
                    );
                    const answer = await peerConnection.current.createAnswer();
                    await peerConnection.current.setLocalDescription(answer);
                    socket.current.emit("signal", {roomId, data: {type: "answer", answer}});
                } catch (err) {
                    console.error("🚨 Offer 처리 중 오류:", err);
                }
            } else if (data.type === "answer") {
                try {
                    await peerConnection.current.setRemoteDescription(
                        new RTCSessionDescription(data.answer)
                    );
                } catch (err) {
                    console.error("🚨 Answer 처리 중 오류:", err);
                }
            } else if (data.type === "ice-candidate") {
                try {
                    await peerConnection.current.addIceCandidate(
                        new RTCIceCandidate(data.candidate)
                    );
                } catch (err) {
                    console.error("🚨 ICE Candidate 추가 중 오류:", err);
                }
            }
        });

        return () => {
            socket.current.disconnect();
            localStream.current?.getTracks().forEach(track => track.stop());
        };
    }, [roomId]);

    const createPeerConnection = async () => {
        peerConnection.current = new RTCPeerConnection(servers);

        localStream.current.getTracks().forEach((track) => {
            peerConnection.current.addTrack(track, localStream.current);
        });

        peerConnection.current.ontrack = (event) => {
            remoteVideoRef.current.srcObject = event.streams[0];
        };

        peerConnection.current.onicecandidate = (event) => {
            if (event.candidate) {
                socket.current.emit("signal", {
                    roomId,
                    data: {type: "ice-candidate", candidate: event.candidate}
                });
            }
        };
    };

    const createOffer = async () => {
        await createPeerConnection();
        try {
            const offer = await peerConnection.current.createOffer();
            await peerConnection.current.setLocalDescription(offer);
            socket.current.emit("signal", {roomId, data: {type: "offer", offer}});
        } catch (err) {
            console.error("🚨 Offer 생성 중 오류:", err);
        }
    };

    return (
        <div style={{display: "flex", gap: "10px"}}>
            <div className="video-box">
                <video ref={myVideoRef} autoPlay muted playsInline className="video"/>
                <div className="name-tag">{user.name}</div>
            </div>
            <div className="video-box">
                <video ref={remoteVideoRef} autoPlay playsInline className="video"/>
                <div className="name-tag">상대방</div>
            </div>
        </div>
    );
};

export default VideoRoom;
