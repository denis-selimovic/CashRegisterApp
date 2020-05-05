package ba.unsa.etf.si.interfaces;

public interface PaymentProcessingListener {
    void onPaymentProcessed(boolean isValid);
}
