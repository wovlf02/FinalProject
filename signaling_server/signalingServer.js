const http = require("http");
const { Server } = require("socket.io");

const server = http.createServer();

// ✅ ngrok 주소 허용 + 세션 인증 CORS 설정
const io = new Server(server, {
    cors: {
        origin: (origin, callback) => {
            if (!origin || /^https:\/\/.*\.ngrok-free\.app$/.test(origin)) {
                callback(null, true);
            } else {
                callback(new Error("Not allowed by CORS"));
            }
        },
        methods: ["GET", "POST"],
        credentials: true
    }
});

// ✅ 소켓 연결
io.on("connection", (socket) => {
    console.log("✅ 사용자 연결됨:", socket.id);

    // ✅ 방 입장 처리
    socket.on("join-room", (roomId) => {
        socket.join(roomId);
        console.log(`${socket.id} 님이 방 ${roomId} 에 입장했습니다.`);

        const clientsInRoom = Array.from(io.sockets.adapter.rooms.get(roomId) || []);
        const otherClients = clientsInRoom.filter(id => id !== socket.id);

        // ✅ 새 유저에게 기존 유저 목록 전송
        socket.emit("all-users", otherClients);

        // ✅ 기존 유저에게 새 유저 알림
        socket.to(roomId).emit("user-connected", socket.id);

        // ✅ 인원 수 갱신 브로드캐스트
        const numClients = clientsInRoom.length;
        io.to(roomId).emit("user-count", numClients);
    });

    // ✅ WebRTC 시그널 중계
    socket.on("signal", ({ roomId, data }) => {
        data.senderId = socket.id;

        if (data.target_id) {
            // ✅ 특정 사용자에게만 전송
            io.to(data.target_id).emit("signal", data);
        } else {
            // ✅ 예외: 타겟 지정 안 된 경우 전체 브로드캐스트
            socket.to(roomId).emit("signal", data);
        }
    });

    // ✅ 채팅 메시지 중계
    socket.on("chat", ({ roomId, message, senderId }) => {
        io.to(roomId).emit("chat", { message, senderId });
    });

    // ✅ 연결 해제 처리
    socket.on("disconnecting", () => {
        const rooms = Array.from(socket.rooms).filter(room => room !== socket.id);

        rooms.forEach((roomId) => {
            setTimeout(() => {
                const numClients = io.sockets.adapter.rooms.get(roomId)?.size || 0;

                // ✅ 사용자 수 갱신
                io.to(roomId).emit("user-count", numClients);

                // ✅ 사용자 퇴장 알림
                socket.to(roomId).emit("user-disconnected", socket.id);
            }, 100);
        });

        console.log("❌ 사용자 연결 해제:", socket.id);
    });
});

server.listen(4000, () => {
    console.log("✅ Signaling 서버가 포트 4000에서 실행 중입니다.");
});
