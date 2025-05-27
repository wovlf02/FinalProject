const http = require("http");
const { Server } = require("socket.io");

const server = http.createServer();
const io = new Server(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"]
  }
});

io.on("connection", socket => {
  console.log("사용자 연결됨:", socket.id);

  socket.on("join-room", ({ roomId, userId, name }) => {
    socket.join(roomId);
    socket.userId = userId; // ✅ 메타데이터 저장
    socket.name = name;

    // 기존 참여자 목록 전달 (메타데이터 포함)
    const existingUsers = Array.from(io.sockets.adapter.rooms.get(roomId) || [])
      .filter(id => id !== socket.id)
      .map(id => {
        const s = io.sockets.sockets.get(id);
        return { socketId: id, userId: s.userId, name: s.name };
      });
    socket.emit("existing-users", existingUsers);

    // 새 사용자 입장 알림
    socket.to(roomId).emit("user-connected", {
      socketId: socket.id,
      userId,
      name
    });

    // 인원 수 업데이트
    const numClients = io.sockets.adapter.rooms.get(roomId)?.size || 0;
    io.to(roomId).emit("user-count", numClients);
  });

  socket.on("signal", msg => {
    const { to } = msg;
    socket.to(to).emit("signal", msg); // 모든 시그널 즉시 전달
  });

  socket.on("disconnecting", () => {
    const rooms = Array.from(socket.rooms).filter(r => r !== socket.id);
    rooms.forEach(roomId => {
      socket.to(roomId).emit("user-disconnected", socket.id); // ✅ 즉시 전파
      const numClients = io.sockets.adapter.rooms.get(roomId)?.size || 0;
      io.to(roomId).emit("user-count", numClients - 1);
    });
  });
});

server.listen(4000, () => {
  console.log("✅ Signaling 서버가 포트 4000에서 실행 중입니다.");
});
