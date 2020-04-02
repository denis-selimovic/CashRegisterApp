package ba.unsa.etf.si.models;

import ba.unsa.etf.si.models.status.PaymentMethod;
import ba.unsa.etf.si.models.status.ReceiptStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Transient
    private String receiptID;

    @Transient
    private PaymentMethod paymentMethod;

    @Transient
    private ReceiptStatus receiptStatus;

    @Basic
    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime date;

    private String cashier;
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private List<Receipt> receiptItems;

    public Receipt() {

    }

    public Receipt(Long id, LocalDateTime date, String cashier, Double amount) {
        this.id = id;
        this.date = date;
        this.cashier = cashier;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public Double getAmount() {
        return amount;
    }


    public String getReceiptID() {
        return receiptID;
    }

    public ReceiptStatus getReceiptStatus() {
        return receiptStatus;
    }

    public void setReceiptStatus(ReceiptStatus receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<Receipt> getReceiptItems() {
        return receiptItems;
    }

    public void setReceiptItems(List<Receipt> receiptItems) {
        this.receiptItems = receiptItems;
    }
}
