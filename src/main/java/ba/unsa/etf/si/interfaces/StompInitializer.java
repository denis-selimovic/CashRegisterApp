package ba.unsa.etf.si.interfaces;

import ba.unsa.etf.si.notifications.topics.Topic;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;

public interface StompInitializer {
    void initializeSession(StompSession stompSession);

    void initializeClient(Topic topic, MessageConverter converter);
}
