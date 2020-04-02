package ba.unsa.etf.si.models.status;

public enum PaymentMethod {
    CASH("CASH"),
    CREDIT_CARD("CREDIT_CARD"),
    PAY_APP("PAY_APP");

    private String method;

    PaymentMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
