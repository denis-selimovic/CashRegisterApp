package ba.unsa.etf.si.utility.sockets;

import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;

public class NotificationStompSessionHandler implements StompSessionHandler {

    @Override
    public void afterConnected(@NonNull StompSession stompSession, @NonNull StompHeaders stompHeaders) {

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
        System.out.println("Received message: " + notificationMessage.message);
    }
}
