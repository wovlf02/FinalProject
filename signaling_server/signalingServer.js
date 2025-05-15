// signalingServer.js
const http = require("http");
const { Server } = require("socket.io");
const axios = require("axios");
require("dotenv").config();

const API_BASE = process.env.API_BASE || "http://172.17.5.61:8080";

const server = http.createServer();
const io = new Server(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"],
  },
});

const userMap = new Map(); // socket.id -> userName

io.on("connection", (socket) => {
  console.log("✅ 사용자 연결됨:", socket.id);

  socket.on("join-room", async ({ roomId, userName }) => {
    if (!socket.rooms.has(roomId)) {
      socket.join(roomId);
      socket.data.userName = userName;
      socket.data.roomId = roomId;
      userMap.set(socket.id, userName);

      console.log(`📥 ${userName} (${socket.id})님이 ${roomId} 방에 입장`);

      try {
        await axios.post(`${API_BASE}/api/video/join/${roomId}`);
        const res = await axios.get(`${API_BASE}/api/video/count/${roomId}`);
        const count = res.data;

        io.to(roomId).emit("user-count", count);
      } catch (err) {
        console.error("접속자 수 증가 실패:", err);
      }

      // 현재 참여자 목록 전송
      const clients = [...io.sockets.adapter.rooms.get(roomId) || []]
        .filter(id => id !== socket.id)
        .map(id => ({ socketId: id, userName: userMap.get(id) || "알 수 없음" }));
      socket.emit("all-users", clients);

      socket.to(roomId).emit("user-connected", {
        socketId: socket.id,
        userName,
      });
    }
  });

  socket.on("chat", ({ roomId, message, senderId }) => {
    io.to(roomId).emit("chat", { message, senderId });
  });

  socket.on("signal", ({ roomId, data }) => {
    socket.to(roomId).emit("signal", data);
  });

  socket.on("disconnecting", async () => {
    const roomId = socket.data.roomId;
    const userName = socket.data.userName;
    userMap.delete(socket.id);

    if (!roomId) return;

    console.log(`👋 ${userName || "익명"} (${socket.id})님이 ${roomId} 방을 떠남`);

    try {
      await axios.post(`${API_BASE}/api/video/leave/${roomId}`);
      const res = await axios.get(`${API_BASE}/api/video/count/${roomId}`);
      const count = res.data;

      io.to(roomId).emit("user-count", count);
    } catch (err) {
      console.error("접속자 수 감소 실패:", err);
    }

    socket.to(roomId).emit("user-disconnected", {
      socketId: socket.id,
      userName,
    });
  });
});

server.listen(4000, () => {
  console.log("✅ Signaling 서버가 포트 4000에서 실행 중입니다.");
});