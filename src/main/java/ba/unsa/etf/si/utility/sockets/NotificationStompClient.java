package ba.unsa.etf.si.utility.sockets;

import ba.unsa.etf.si.utility.interfaces.MessageSender;
import ba.unsa.etf.si.utility.interfaces.StompInitializer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class NotificationStompClient implements StompInitializer, MessageSender {

    private static final String URL = "ws://stomp-test.herokuapp.com/ws";

    private final String CHANNEL;
    private final WebSocketStompClient stompClient;
    private StompSession stompSession;

    public NotificationStompClient(String channel, String topic) {
        stompClient = new WebSocketStompClient(SockJSUtils.getSockJsClient());
        this.CHANNEL = channel;
        initializeClient(topic);
    }

    @Override
    public void initializeClient(String topic) {
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect(URL, new NotificationStompSessionHandler(this, topic));
    }

    @Override
    public void initializeSession(StompSession stompSession) {
        this.stompSession = stompSession;
    }

    @Override
    public void sendMessage(String message) {
        if(stompSession == null) return;
        stompSession.send(CHANNEL, message);
    }
}
