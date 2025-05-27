// signalingServer.js
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

  // payload를 객체로 받습니다.
  socket.on("join-room", ({ roomId, userId, name }) => {
    socket.join(roomId);
    console.log(`${socket.id} (${name}) 님이 방 ${roomId} 에 입장했습니다.`);

    // 다른 참가자에게 알림: 누가 들어왔는지 id와 이름까지 전달
    socket.to(roomId).emit("user-connected", {
      socketId: socket.id,
      userId,
      name
    });

    // 현재 방의 인원 수 전달
    const numClients = io.sockets.adapter.rooms.get(roomId)?.size || 0;
    io.to(roomId).emit("user-count", numClients);
  });

  socket.on("signal", msg => {
    // signal 메시지는 from/to/type/payload 형태로 주고받습니다.
    const { roomId, to } = msg;
    socket.to(to).emit("signal", msg);
  });

  socket.on("chat", ({ roomId, message, senderId }) => {
    io.to(roomId).emit("chat", { message, senderId });
  });

socket.on("disconnecting", () => {
  const rooms = Array.from(socket.rooms).filter(r => r !== socket.id);
  rooms.forEach(roomId => {
    // 1) 지연 없이 즉시 퇴장 알림
    socket.to(roomId).emit("user-disconnected", socket.id);
    // 2) 즉시 인원 수 갱신
    const numClients = io.sockets.adapter.rooms.get(roomId)?.size || 0;
    io.to(roomId).emit("user-count", numClients);
  });
  console.log("사용자 연결 해제:", socket.id);
});


server.listen(4000, () => {
  console.log("✅ Signaling 서버가 포트 4000에서 실행 중입니다.");
});
