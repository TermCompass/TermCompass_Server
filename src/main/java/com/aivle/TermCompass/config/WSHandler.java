package com.aivle.TermCompass.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.aivle.TermCompass.domain.Record;
import com.aivle.TermCompass.domain.Request;
import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.domain.Record.RecordType;
import com.aivle.TermCompass.repository.RecordRepository;
import com.aivle.TermCompass.repository.RequestRepository;
import com.aivle.TermCompass.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class WSHandler extends TextWebSocketHandler {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RecordRepository recordRepository;

    private final Map<Long, WebSocketSession> clientWebSocketMap = new ConcurrentHashMap<>();
    private final Map<Long, WebSocketSession> fastapiWebSocketMap = new ConcurrentHashMap<>();
    private final Map<Long, Request> requestMap = new ConcurrentHashMap<>();

    Gson gson = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

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
        clientWebSocketMap.put(id, session);

        // FastAPI 연결 생성 및 추가
        if (fastapiWebSocketMap.get(id) == null) {
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
            WebSocketSession fastAPISession = fastapiWebSocketMap.get(id);
            if (fastAPISession != null && fastAPISession.isOpen()) {
                System.out.println("[FastAPI 메시지 발신] " + email + " " +", 내용: " + message);
                fastAPISession.sendMessage(message);

            } else {
                connectToFastAPI(id, email);
                System.err.println("FastAPI 세션 생성");

                // 세션 다시 생성 후 메세지 전송
                fastAPISession = fastapiWebSocketMap.get(id);
                // fastAPISession.sendMessage(message);
                // 연결 이후 재전송 로직 추가
                CompletableFuture.runAsync(() -> {
                    try {
                        // 연결 대기 후 메시지 전송
                        Thread.sleep(1000); // 연결 대기 (필요시 조정)
                        WebSocketSession newFastAPISession = fastapiWebSocketMap.get(id);
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

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        Long id = (Long) session.getAttributes().get("id");
        System.out.println("[Client 연결 종료] ID: " + id + ", 상태: " + status);

        // 클라이언트 맵에서 제거
        clientWebSocketMap.remove(id);   
    }

    // 클라이언트 맵 관리

    // WebSocketConfig에서 현재 클라이언트 맵핑 조회가 필요할때 사용
    public boolean containsClientAndFastAPIId(Long id) {
        System.out.println("containsClientAndFastAPIId 실행");
        return clientWebSocketMap.containsKey(id)||fastapiWebSocketMap.containsKey(id);
    }
    
    // 클라이언트 맵 관리 end

    // Request 맵 관리 ( FastAPI로부터 total 타입 메세지 받았을때 )
    private void addRequestMap(Long id, List<String> total) {
        // id로 DB 조회해서 User 객체 만들기
        Optional<User> user = userRepository.findById(id);
        
        Record record = new Record();
        Request request = new Request();

        // Request 세팅
        request.setRecord(record);
        request.setRequest(listToJson(total)); // List를 문자열(JSON)로
        request.setAnswer(listToJson(new ArrayList<>())); // 빈 List

        List<Request> requests = new ArrayList<>();
        requests.add(request);
        
        // Record 세팅
        record.setUser(user.get());
        record.setRecord_type(RecordType.REVIEW);
        record.setResult(total.get(0)); // 첫번째(문서 제목 유력함) 값으로 설정
        record.setRequests(requests); // record - request 연결되는 지점
        
        requestMap.put(id,request);
    }

    // Request 맵 관리 end

    // JSON <-> List<String>

    // List<String> -> JSON String
    public static String listToJson(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);  // List<String>을 JSON 문자열로 변환
    }

    // JSON String -> List<String>
    public static List<String> jsonToList(String jsonString) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(jsonString, listType);  // JSON 문자열을 List<String>으로 변환
    }

    // JSON <-> List<String> end

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
                WebSocketSession clientSession = clientWebSocketMap.get(sessionId);

                // "type" 값 확인
                Map<String, Object> jsonMessage = gson.fromJson(payload, mapType);
                String type = (String) jsonMessage.get("type");
                System.out.println("type :" + type);

                // 약관 항목 List인 경우 & 로그인 회원인 경우
                if (type.equals("total")&&!session.getAttributes().get("email").equals("not@user")) {
                    // Request entity Map 등록
                    List<String> total = (List<String>) jsonMessage.get("content");
                    addRequestMap(sessionId, total);
                }

                // 약관 검토 조각인경우 & 로그인 회원인 경우
                if (type.equals("review")&&!session.getAttributes().get("email").equals("not@user")) {

                    // 해당 세션의 request 추출
                    Request request = requestMap.get(sessionId);
                    // 현재 Answer List<>
                    List<String> current = jsonToList(request.getAnswer());
                    // List에 받은 review content 값 추가
                    current.add((String) jsonMessage.get("content"));

                    // List를 request의 answer에 덮어쓰기
                    request.setAnswer(listToJson(current));

                    // requestMap 덮어쓰기
                    requestMap.put(sessionId, request);
                }

                // FastAPI에서 end 메세지 온 경우
                if (type.equals("end")) {

                    // 현재 record 최종으로 먼저 repository에 저장
                    recordRepository.save(requestMap.get(sessionId).getRecord());
                    // 현재 request 최종으로 repository에 저장
                    requestRepository.save(requestMap.get(sessionId));
                    
                    // 클라이언트 접속상태이면 저장 완료 알림
                    if (clientWebSocketMap.containsKey(sessionId)) {
                        String done_message = "";
                        clientWebSocketMap.get(sessionId).sendMessage(new TextMessage("{\"type\":\"done\"}"));
                    }

                    // request Map, session 삭제
                    requestMap.remove(sessionId);
                    fastapiWebSocketMap.remove(sessionId);
                    clientWebSocketMap.remove(sessionId);
                } 

                if (clientSession != null) {
                    // 클라이언트에 그대로 전달
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

            fastapiWebSocketMap.put(id, fastapiSession);
            System.out.println("FastAPI 연결 성공: " + email);
        }).exceptionally(ex -> {
            System.err.println("FastAPI WebSocket 연결 실패: " + ex.getMessage());
            return null;
        });
    }

}
