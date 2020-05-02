package ba.unsa.etf.si.utility.interfaces;

import org.springframework.messaging.simp.stomp.StompSession;

public interface StompInitializer {

    void initializeSession(StompSession stompSession);
    void initializeClient(String topic);
}
