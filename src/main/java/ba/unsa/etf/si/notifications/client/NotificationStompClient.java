package ba.unsa.etf.si.notifications.client;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.interfaces.MessageSender;
import ba.unsa.etf.si.interfaces.StompInitializer;
import ba.unsa.etf.si.notifications.topics.Topic;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class NotificationStompClient implements StompInitializer, MessageSender {

    private final String TOPIC;
    private final WebSocketStompClient stompClient;
    private StompSession stompSession;

    public NotificationStompClient(Topic topic, MessageConverter converter) {
        TOPIC = topic.getTopic();
        stompClient = new WebSocketStompClient(SockJSUtils.getSockJsClient());
        initializeClient(topic, converter);
    }

    @Override
    public void initializeClient(Topic topic, MessageConverter converter) {
        stompClient.setMessageConverter(converter);
        stompClient.connect(App.BROKER, new NotificationStompSessionHandler(this, topic));
    }

    @Override
    public void initializeSession(StompSession stompSession) {
        this.stompSession = stompSession;
    }

    @Override
    public void sendMessage(String message) {
        if(stompSession == null) return;
        stompSession.send(TOPIC, message);
    }
}
