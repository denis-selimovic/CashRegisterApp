package ba.unsa.etf.si.services;

import ba.unsa.etf.si.notifications.client.NotificationStompClient;
import ba.unsa.etf.si.notifications.topics.GuestNotificationTopic;
import ba.unsa.etf.si.notifications.topics.InventoryNotificationTopic;
import ba.unsa.etf.si.notifications.topics.PaymentNotificationTopic;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;

public class MessageBrokerService {

    public final NotificationStompClient guestNotificationStompClient;
    public final NotificationStompClient inventoryStompClient;
    public final NotificationStompClient paymentStompClient;

    public MessageBrokerService() {
        guestNotificationStompClient = new NotificationStompClient(new GuestNotificationTopic(), new MappingJackson2MessageConverter());
        inventoryStompClient = new NotificationStompClient(new InventoryNotificationTopic(), new StringMessageConverter());
        paymentStompClient = new NotificationStompClient(new PaymentNotificationTopic(), new StringMessageConverter());
    }
}
