const http = require("http");
const { Server } = require("socket.io");

const server = http.createServer();
const io = new Server(server, {
  cors: {
    origin: "*", // 배포 시 특정 도메인으로 변경 권장
    methods: ["GET", "POST"]
  }
});

// 소켓 연결
io.on("connection", (socket) => {
  console.log("사용자 연결됨:", socket.id);

  // 방 참가
  socket.on("join-room", (roomId) => {
    socket.join(roomId);
    console.log(`${socket.id} 님이 방 ${roomId} 에 입장했습니다.`);

    // 다른 참가자에게 알림
    socket.to(roomId).emit("user-connected", socket.id);

    // 현재 방의 인원 수 전달
    const numClients = io.sockets.adapter.rooms.get(roomId)?.size || 0;
    io.to(roomId).emit("user-count", numClients);
  });

  // WebRTC 시그널링 전달
  socket.on("signal", ({ roomId, data }) => {
    socket.to(roomId).emit("signal", data);
  });

  // 채팅 기능
  socket.on("chat", ({ roomId, message, senderId }) => {
    io.to(roomId).emit("chat", { message, senderId });
  });

  // 연결 해제
  socket.on("disconnecting", () => {
    const rooms = Array.from(socket.rooms).filter((room) => room !== socket.id);
    rooms.forEach((roomId) => {
      // 퇴장 후 인원 수 갱신
      setTimeout(() => {
        const numClients = io.sockets.adapter.rooms.get(roomId)?.size || 0;
        io.to(roomId).emit("user-count", numClients);
      }, 100); // 소켓 퇴장 직후엔 아직 방에 남아있어서 약간의 지연 필요
    });
    console.log("사용자 연결 해제:", socket.id);
  });
});

server.listen(4000, () => {
  console.log("✅ Signaling 서버가 포트 4000에서 실행 중입니다.");
});
