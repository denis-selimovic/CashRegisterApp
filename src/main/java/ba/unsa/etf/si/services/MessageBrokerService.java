package ba.unsa.etf.si.services;

import ba.unsa.etf.si.notifications.client.NotificationStompClient;
import ba.unsa.etf.si.notifications.topics.GuestNotificationTopic;
import ba.unsa.etf.si.notifications.topics.InventoryNotificationTopic;
import ba.unsa.etf.si.notifications.topics.PaymentNotificationTopic;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;

public class MessageBrokerService {

    public static final GuestNotificationTopic guestNotificationTopic = new GuestNotificationTopic();
    public static final InventoryNotificationTopic inventoryNotificationTopic = new InventoryNotificationTopic();
    public static final PaymentNotificationTopic paymentNotificationTopic = new PaymentNotificationTopic();

    public final NotificationStompClient guestNotificationStompClient;
    public final NotificationStompClient inventoryStompClient;
    public final NotificationStompClient paymentStompClient;

    public MessageBrokerService() {
        guestNotificationStompClient = new NotificationStompClient(guestNotificationTopic, new MappingJackson2MessageConverter());
        inventoryStompClient = new NotificationStompClient(inventoryNotificationTopic, new StringMessageConverter());
        paymentStompClient = new NotificationStompClient(paymentNotificationTopic, new StringMessageConverter());
    }
}
