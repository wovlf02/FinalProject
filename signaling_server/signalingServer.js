const http = require("http");
const { Server } = require("socket.io");

const server = http.createServer();
const io = new Server(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"]
  }
});

io.on("connection", (socket) => {
  console.log("사용자 연결됨:", socket.id);

  // ✅ 방 참가 시 사용자 이름도 함께 받음
  socket.on("join-room", ({ roomId, userName }) => {
    socket.join(roomId);
    socket.data.userName = userName; // 소켓에 사용자 이름 저장
    console.log(`${userName} (${socket.id}) 님이 방 ${roomId}에 입장했습니다.`);

    // 다른 참가자에게 이 사용자 정보 알림
    socket.to(roomId).emit("user-connected", {
      socketId: socket.id,
      userName: userName
    });

    // 현재 방의 인원 수 전달
    const numClients = io.sockets.adapter.rooms.get(roomId)?.size || 0;
    io.to(roomId).emit("user-count", numClients);
  });

  // WebRTC 시그널 전달
  socket.on("signal", ({ roomId, data }) => {
    socket.to(roomId).emit("signal", data);
  });

  // 채팅 메시지 브로드캐스트
  socket.on("chat", ({ roomId, message, senderId }) => {
    io.to(roomId).emit("chat", { message, senderId });
  });

  // 퇴장 처리
  socket.on("disconnecting", () => {
    const rooms = Array.from(socket.rooms).filter((room) => room !== socket.id);
    rooms.forEach((roomId) => {
      // 약간 지연 후 인원 수 갱신
      setTimeout(() => {
        const numClients = io.sockets.adapter.rooms.get(roomId)?.size || 0;
        io.to(roomId).emit("user-count", numClients);
      }, 100);

      // 나간 사용자 정보 알림 (선택)
      socket.to(roomId).emit("user-disconnected", {
        socketId: socket.id,
        userName: socket.data.userName || "알 수 없음"
      });
    });

    console.log("사용자 연결 해제:", socket.id);
  });
});

server.listen(4000, () => {
  console.log("✅ Signaling 서버가 포트 4000에서 실행 중입니다.");
});
