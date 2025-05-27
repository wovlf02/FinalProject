const http = require("http");
const { Server } = require("socket.io");

const server = http.createServer();
const io = new Server(server, {
    cors: {
        origin: "*", // 배포 시 특정 도메인으로 변경 권장
        methods: ["GET", "POST"]
    }
});

io.on("connection", (socket) => {
    console.log("사용자 연결됨:", socket.id);

    // 방 참가
    socket.on("join-room", (roomId) => {
        socket.join(roomId);
        console.log(`${socket.id} 님이 방 ${roomId} 에 입장했습니다.`);

        const clientsInRoom = Array.from(io.sockets.adapter.rooms.get(roomId) || []);
        const otherClients = clientsInRoom.filter(id => id !== socket.id);

        // ✅ 새 유저에게 기존 유저 목록 전달
        socket.emit("all-users", otherClients);

        // ✅ 기존 유저들에게 새 유저 입장 알림
        socket.to(roomId).emit("user-connected", socket.id);

        // ✅ 인원 수 전달
        const numClients = clientsInRoom.length;
        io.to(roomId).emit("user-count", numClients);
    });

    // ✅ WebRTC 시그널링 전달 (senderId 포함 필요)
    socket.on("signal", ({ roomId, data }) => {
        data.senderId = socket.id; // 발신자 추가
        if (data.targetId) {
            // 특정 대상에게만 전송
            io.to(data.targetId).emit("signal", data);
        } else {
            // 모든 사람에게 전송 (예외적 상황)
            socket.to(roomId).emit("signal", data);
        }
    });

    // 채팅 기능
    socket.on("chat", ({ roomId, message, senderId }) => {
        io.to(roomId).emit("chat", { message, senderId });
    });

    // 연결 해제
    socket.on("disconnecting", () => {
        const rooms = Array.from(socket.rooms).filter(room => room !== socket.id);
        rooms.forEach((roomId) => {
            // 퇴장 후 인원 수 갱신
            setTimeout(() => {
                const numClients = io.sockets.adapter.rooms.get(roomId)?.size || 0;
                io.to(roomId).emit("user-count", numClients);

                // ✅ 나간 사람 정보를 다른 사용자에게도 전달
                socket.to(roomId).emit("user-disconnected", socket.id);
            }, 100);
        });
        console.log("사용자 연결 해제:", socket.id);
    });
});

server.listen(4000, () => {
    console.log("✅ Signaling 서버가 포트 4000에서 실행 중입니다.");
});
