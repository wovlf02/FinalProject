const { Server } = require("socket.io");

const io = new Server(8081, {
  cors: { origin: "*" },
});

io.on("connection", (socket) => {
  console.log("✅ 사용자 연결됨:", socket.id);

  socket.on("join-room", ({ roomId, userId }) => {
    socket.join(roomId);
    console.log(`🧑‍🤝‍🧑 ${userId} joined room ${roomId}`);
    socket.to(roomId).emit("user-connected", userId);

    socket.on("offer", (data) => {
      socket.to(roomId).emit("offer", data);
    });

    socket.on("answer", (data) => {
      socket.to(roomId).emit("answer", data);
    });

    socket.on("ice-candidate", (data) => {
      socket.to(roomId).emit("ice-candidate", data);
    });

    socket.on("disconnect", () => {
      console.log(`❌ ${userId} disconnected`);
      socket.to(roomId).emit("user-disconnected", userId);
    });
  });
});
