package ba.unsa.etf.si.models.status;

public enum ReceiptStatus {
    CANCELED("CANCELED"),
    PAID("PAID"),
    INSUFFICIENT_FUNDS("INSUFFICIENT_FUNDS"),
    PENDING("PENDING"),
    DELETED("DELETED");

    private String status;

    ReceiptStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
