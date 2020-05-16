package ba.unsa.etf.si.notifications.topics;

import ba.unsa.etf.si.controllers.PaymentProcessingController;
import ba.unsa.etf.si.notifications.models.PaymentNotification;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class PaymentNotificationTopic implements Topic{

    private final Consumer<Object> action = payload -> {
        String payloadString = (String) payload;
        PaymentNotification paymentNotification = new PaymentNotification(new JSONObject(payloadString));
        synchronized (this) {
            PaymentProcessingController.paymentProcessing = false;
            PaymentProcessingController.status = paymentNotification.getStatus();
            notifyAll();
        }
    };

    @Override
    public Type getType() {
        return String.class;
    }

    @Override
    public String getTopic() {
        return "/topic/receipt_status_update";
    }

    @Override
    public Consumer<Object> getAction() {
        return action;
    }
}
