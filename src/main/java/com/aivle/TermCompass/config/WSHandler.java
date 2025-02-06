package com.aivle.TermCompass.config;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WSHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> clientWebSocketMap = new ConcurrentHashMap<>();
    private final Map<Long, WebSocketSession> fastapiWebSocketMap = new ConcurrentHashMap<>();

    // 클라이언트 WebSocket 연결 수립
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String email = (String) session.getAttributes().get("email");
        Long id = (Long) session.getAttributes().get("id");
        System.out.println("[Client 연결 수립] 이메일: " + email + ", ID: " + id);

        // 최대 메세지 길이 지정
        session.setTextMessageSizeLimit(512 * 1024);
        session.setBinaryMessageSizeLimit(512 * 1024);

        // Map에 추가
        addToClientMap(id, session);

        // FastAPI 연결 생성 및 추가
        if (getFastAPISession(id) == null) {
            connectToFastAPI(id, email);
        }
    }

    // 메시지 수신 처리
    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        String payload = message.getPayload();
        Long id = (Long) session.getAttributes().get("id");
        String email = (String) session.getAttributes().get("email");
        String direction = (String) session.getAttributes().get("direction");

        System.out.println(direction + "[Client 메시지 수신] " + email);
        
        // JSON을 Map으로 파싱
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> jsonMessage = gson.fromJson(payload, mapType);

        // "type" 값 확인
        String type = (String) jsonMessage.get("type");
        System.out.println("type :" + type);

        // ping 메세지는 별도의 처리 없이 pong 응답
        if (type.equals("ping")) {
            session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
        }
        else {
            // 클라이언트 -> FastAPI 메시지 전달
            WebSocketSession fastAPISession = getFastAPISession(id);
            if (fastAPISession != null && fastAPISession.isOpen()) {
                System.out.println("[FastAPI 메시지 발신] " + email + " " +", 내용: " + message);
                fastAPISession.sendMessage(message);

            } else {
                connectToFastAPI(id, email);
                System.err.println("FastAPI 세션 생성");

                // 세션 다시 생성 후 메세지 전송
                fastAPISession = getFastAPISession(id);
                // fastAPISession.sendMessage(message);
                // 연결 이후 재전송 로직 추가
                CompletableFuture.runAsync(() -> {
                    try {
                        // 연결 대기 후 메시지 전송
                        Thread.sleep(1000); // 연결 대기 (필요시 조정)
                        WebSocketSession newFastAPISession = getFastAPISession(id);
                        if (newFastAPISession != null && newFastAPISession.isOpen()) {
                            newFastAPISession.sendMessage(message);
                            System.out.println("새 FastAPI 세션으로 메시지 전송 완료");
                        } else {
                            System.err.println("새 FastAPI 세션 생성 실패 또는 닫힘");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        } 
        // else {
        //     // FastAPI -> 클라이언트 메시지 전달
        //     WebSocketSession clientSession = getClientSession(id);
        //     if (clientSession != null) {
        //         clientSession.sendMessage(message);
        //     } else {
        //         System.err.println("클라이언트 세션이 없습니다.");
        //     }
        // }
    }

    // 클라이언트 맵 관리
    private void addToClientMap(Long id, WebSocketSession session) {
        clientWebSocketMap.put(id, session);
    }

    private WebSocketSession getClientSession(Long id) {
        return clientWebSocketMap.get(id);
    }

    // FastAPI 맵 관리
    private void addToFastAPIMap(Long id, WebSocketSession session) {
        fastapiWebSocketMap.put(id, session);
    }

    private WebSocketSession getFastAPISession(Long id) {
        return fastapiWebSocketMap.get(id);
    }

    // FastAPI 연결 생성
    private void connectToFastAPI(Long id, String email) {
        WebSocketClient client = new StandardWebSocketClient();
        String url = "ws://localhost:8000/ws";

        // WebSocketHandler의 handleTextMessage를 재사용
        CompletableFuture<WebSocketSession> fastapiFuture = client.execute(new TextWebSocketHandler() {
            @Override
            public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message)
                    throws Exception {
                // 기존 WebSocketHandler handleTextMessage 로직 재사용
                String payload = message.getPayload();
                System.out.println("[FastAPI 메시지 수신] " + session.getAttributes().get("email") + payload);

                Long sessionId = (Long) session.getAttributes().get("id");
                WebSocketSession clientSession = getClientSession(sessionId);

                if (clientSession != null) {
                    clientSession.sendMessage(message);
                } else {
                    System.err.println("대응하는 클라이언트 세션이 없습니다.");
                }
            }
        }, url, new Object[] {});

        fastapiFuture.thenAccept(fastapiSession -> {
            fastapiSession.getAttributes().put("email", email);
            fastapiSession.getAttributes().put("id", id);
            fastapiSession.getAttributes().put("direction", "fastapi");

            addToFastAPIMap(id, fastapiSession);
            System.out.println("FastAPI 연결 성공: " + email);
        }).exceptionally(ex -> {
            System.err.println("FastAPI WebSocket 연결 실패: " + ex.getMessage());
            return null;
        });
    }

}
