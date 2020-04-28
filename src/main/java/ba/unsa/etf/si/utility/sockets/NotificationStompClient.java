package ba.unsa.etf.si.utility.sockets;

import ba.unsa.etf.si.utility.interfaces.SessionInitializer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;

public class NotificationStompClient implements SessionInitializer {

    private static final String URL = "ws://stomp-test.herokuapp.com/ws";
    private static final String CHANNEL = "/app/news";

    private final WebSocketStompClient stompClient;
    private StompSession stompSession;

    public NotificationStompClient() {
        stompClient = new WebSocketStompClient(getSockJsClient());
        initialize();
    }

    private void initialize() {
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect(URL, new NotificationStompSessionHandler(this));
    }

    private static SockJsClient getSockJsClient() {
        return new SockJsClient(addTransports());
    }

    private static List<Transport> addTransports() {
        WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(simpleWebSocketClient));
        return transports;
    }

    @Override
    public void initialize(StompSession stompSession) {
        this.stompSession = stompSession;
    }

    public void sendMessage(String message) {
        stompSession.send(CHANNEL, message);
    }
}
