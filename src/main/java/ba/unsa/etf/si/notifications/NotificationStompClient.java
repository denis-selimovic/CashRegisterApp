package ba.unsa.etf.si.notifications;

import ba.unsa.etf.si.utility.interfaces.MessageSender;
import ba.unsa.etf.si.utility.interfaces.StompInitializer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class NotificationStompClient implements StompInitializer, MessageSender {

    private static final String URL = "ws://cash-register-server-si.herokuapp.com/ws";

    private static final String CHANNEL = "/api/notifications";
    private static final String TOPIC = "/topic/notifications";
    private final WebSocketStompClient stompClient;
    private StompSession stompSession;

    public NotificationStompClient() {
        stompClient = new WebSocketStompClient(SockJSUtils.getSockJsClient());
        initializeClient(TOPIC);
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
        if(stompSession == null) {
            System.out.println("Stomp session is null!");
            return;
        }
        stompSession.send(CHANNEL, message);
    }
}
