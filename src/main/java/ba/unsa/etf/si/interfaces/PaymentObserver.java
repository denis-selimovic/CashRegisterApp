package ba.unsa.etf.si.interfaces;

import ba.unsa.etf.si.notifications.models.PaymentNotification;

public interface PaymentObserver {
    void onPaymentProcessed(PaymentNotification paymentNotification);
}
