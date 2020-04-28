package ba.unsa.etf.si.utility.sockets;

import ba.unsa.etf.si.utility.interfaces.SessionInitializer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class NotificationStompClient implements SessionInitializer {

    private static final String URL = "ws://stomp-test.herokuapp.com/ws";

    private final String CHANNEL;
    private final WebSocketStompClient stompClient;
    private StompSession stompSession;

    public NotificationStompClient(String channel, String topic) {
        stompClient = new WebSocketStompClient(SockJSUtils.getSockJsClient());
        this.CHANNEL = channel;
        initializeClient(topic);
    }

    private void initializeClient(String topic) {
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect(URL, new NotificationStompSessionHandler(this, topic));
    }

    @Override
    public void initializeSession(StompSession stompSession) {
        this.stompSession = stompSession;
    }

    public void sendMessage(String message) {
        if(stompSession == null) return;
        stompSession.send(CHANNEL, message);
    }
}
