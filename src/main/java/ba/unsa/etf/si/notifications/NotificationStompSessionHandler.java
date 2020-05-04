package ba.unsa.etf.si.notifications;

import ba.unsa.etf.si.utility.interfaces.StompInitializer;
import ba.unsa.etf.si.utility.javafx.NotificationUtils;
import javafx.geometry.Pos;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;

public class NotificationStompSessionHandler implements StompSessionHandler {

    private final String TOPIC;
    private final StompInitializer stompInitializer;

    public NotificationStompSessionHandler(StompInitializer stompInitializer, String topic) {
        this.stompInitializer = stompInitializer;
        this.TOPIC = topic;
    }

    @Override
    public void afterConnected(@NonNull StompSession stompSession, @NonNull StompHeaders stompHeaders) {
        stompSession.subscribe(TOPIC, this);
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
        return NotificationMessage.class;
    }

    @Override
    public void handleFrame(@NonNull StompHeaders stompHeaders, Object payload) {
        NotificationMessage notificationMessage = (NotificationMessage) payload;
        NotificationUtils.showNotification(Pos.BOTTOM_RIGHT, "Guest notification" + notificationMessage.id, notificationMessage.message, 10);
    }
}
