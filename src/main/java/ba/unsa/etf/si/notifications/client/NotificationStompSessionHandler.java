package ba.unsa.etf.si.notifications.client;

import ba.unsa.etf.si.interfaces.StompInitializer;
import ba.unsa.etf.si.notifications.topics.Topic;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;

public class NotificationStompSessionHandler implements StompSessionHandler {

    private final StompInitializer stompInitializer;
    private final Topic topic;

    public NotificationStompSessionHandler(StompInitializer stompInitializer, Topic topic) {
        this.stompInitializer = stompInitializer;
        this.topic = topic;
    }

    @Override
    public void afterConnected(@NonNull StompSession stompSession, @NonNull StompHeaders stompHeaders) {
        stompSession.subscribe(topic.getTopic(), this);
        stompInitializer.initializeSession(stompSession);
    }

    @Override
    public void handleException(@NonNull StompSession stompSession, StompCommand stompCommand, @NonNull StompHeaders stompHeaders, @NonNull byte[] bytes, Throwable throwable) {
        System.err.println("Exception!");
        throwable.printStackTrace();
    }

    @Override
    public void handleTransportError(@NonNull StompSession stompSession, Throwable throwable) {
        System.err.println("Transport error!");
        throwable.printStackTrace();
    }

    @Override
    @NonNull
    public Type getPayloadType(@NonNull StompHeaders stompHeaders) {
        return topic.getType();
    }

    @Override
    public void handleFrame(@NonNull StompHeaders stompHeaders, Object payload) {
        topic.getAction().accept(payload);
    }
}
