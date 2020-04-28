package ba.unsa.etf.si.utility.sockets;

import ba.unsa.etf.si.utility.interfaces.SessionInitializer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class NotificationStompClient implements SessionInitializer {

    private static final String URL = "ws://stomp-test.herokuapp.com/ws";
    private static final String CHANNEL = "/app/news";

    private final WebSocketStompClient stompClient;
    private StompSession stompSession;

    public NotificationStompClient() {
        stompClient = new WebSocketStompClient(SockJSUtils.getSockJsClient());
        initialize();
    }

    private void initialize() {
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect(URL, new NotificationStompSessionHandler(this));
    }

    @Override
    public void initialize(StompSession stompSession) {
        this.stompSession = stompSession;
    }

    public void sendMessage(String message) {
        stompSession.send(CHANNEL, message);
    }
}
