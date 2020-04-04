package ba.unsa.etf.si.models;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.status.PaymentMethod;
import ba.unsa.etf.si.models.status.ReceiptStatus;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Transient
    private Long serverID;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private ReceiptStatus receiptStatus;

    @Column
    private LocalDateTime date;

    @Column
    private String cashier;

    @Column
    private Double amount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "receipt_id")
    private List<ReceiptItem> receiptItems = new ArrayList<>();

    public Receipt() { }

    public Receipt(LocalDateTime date, String cashier, double amount) {
        this.date = date;
        this.cashier = cashier;
        this.amount = amount;
    }

    public Receipt(LocalDateTime date, String cashier, double amount, Long serverID) {
        this.date = date;
        this.cashier = cashier;
        this.amount = amount;
        this.serverID = serverID;
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
        return null;
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

    public List<ReceiptItem> getReceiptItems() {
        return receiptItems;
    }

    public void setReceiptItems(List<ReceiptItem> receiptItems) {
        this.receiptItems = receiptItems;
    }

    public Long getServerID() {
        return serverID;
    }

    public void setServerID(Long serverID) {
        this.serverID = serverID;
    }

    public String getTimestampID() {
        return "" + App.getMerchantID() + "-" + App.getBranchID() + "-" + App.getCashRegisterID() + "-" + Date.from(date.atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }

    @Override
    public String toString() {
        return "{ \n" +
                " \"id\": " + getServerID() + ",\n" +
                " \"receiptId\": \"" + getTimestampID() + "\",\n" +
                " \"username\": \"" + getCashier() + "\", \n" +
                " \"cashRegisterId\": " + App.getCashRegisterID() + ", \n" +
                " \"paymentMethod\": \"" + getPaymentMethod().getMethod() + "\", \n" +
                " \"receiptItems\": [\n" + getReceiptItemsAsString() + " ]\n}";
    }

    private String getReceiptItemsAsString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < getReceiptItems().size(); ++i) {
            builder.append(getReceiptItems().get(i).toString());
            if(i == getReceiptItems().size() - 1) continue;
            builder.append(",\n");
        }
        builder.append("\n");
        return builder.toString();
    }
}
