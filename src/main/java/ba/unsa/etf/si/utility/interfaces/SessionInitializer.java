package ba.unsa.etf.si.utility.interfaces;

import org.springframework.messaging.simp.stomp.StompSession;

public interface SessionInitializer {

    void initialize(StompSession stompSession);
}
