package ba.unsa.etf.si.models;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.enums.PaymentMethod;
import ba.unsa.etf.si.models.enums.ReceiptStatus;
import ba.unsa.etf.si.utility.modelutils.ReceiptUtils;
import org.json.JSONObject;

import javax.persistence.*;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Transient
    private Long serverID;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "receipt_status")
    @Enumerated(EnumType.STRING)
    private ReceiptStatus receiptStatus;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "cashier")
    private String cashier;

    @Column(name = "amount")
    private Double amount;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "receipt_id")
    private List<ReceiptItem> receiptItems = new ArrayList<>();

    public Receipt() { }

    public Receipt(Order order) {
        this.serverID = order.getServerID();
        this.date = order.getCreationDate();
        this.cashier = order.getBartender();
        this.amount = order.getOrderItemList().stream().mapToDouble(OrderItem::getTotalPrice).sum();
        for(OrderItem item : order.getOrderItemList()) receiptItems.add(new ReceiptItem(item));
    }

    public Receipt (JSONObject json, List<Product> products) {
         setPaymentMethodWithString(json.getString("paymentMethod"));
         setReceiptStatusWithString(json.getString("status"));
         Timestamp timestamp = new Timestamp(json.getLong("timestamp"));
         date = timestamp.toLocalDateTime();
         cashier = json.getString("username");
         amount = json.getDouble("totalPrice");
         receiptItems = ReceiptUtils.receiptItemListFromJSON(json.getJSONArray("receiptItems") ,products);
    }

    public Receipt(LocalDateTime date, String cashier, double amount) {
        this.date = date;
        this.cashier = cashier;
        this.amount = amount;
    }

    public Receipt(LocalDateTime date, String cashier, double amount, Long serverID) {
        this(date, cashier, amount);
        this.serverID = serverID;
    }

    public Receipt(Long id, LocalDateTime date, String cashier, Double amount) {
        this(date, cashier, amount);
        this.id = id;
    }

    public void setPaymentMethodWithString (String str) {
        if (str.equals("CASH")) this.paymentMethod = PaymentMethod.CASH;
        else if (str.equals("CREDIT_CARD")) this.paymentMethod = PaymentMethod.CREDIT_CARD;
        else this.paymentMethod = PaymentMethod.PAY_APP;
    }

    public void setReceiptStatusWithString (String str) {
        if (str.equals("CANCELED")) this.receiptStatus = ReceiptStatus.CANCELED;
        else if (str.equals("PAID")) this.receiptStatus = ReceiptStatus.PAID;
        else if (str.equals("INSUFFICIENT_FUNDS")) this.receiptStatus = ReceiptStatus.INSUFFICIENT_FUNDS;
        else if (str.equals("PENDING")) this.receiptStatus = ReceiptStatus.PENDING;
        else this.receiptStatus= ReceiptStatus.DELETED;
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

    public Double getVATPrice () {
        return BigDecimal.valueOf(receiptItems.stream().mapToDouble(ReceiptItem::getVATValue).sum()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public String getReceiptID() {
        return String.valueOf(getTimestampID());
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
        return "" + App.cashRegister.getMerchantID() + "-" + App.cashRegister.getMerchantID() + "-" + App.cashRegister.getId() + "-" + Date.from(date.atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }

    @Override
    public String toString() {
        return "{ \n" +
                " \"id\": " + getServerID() + ",\n" +
                " \"receiptId\": \"" + getTimestampID() + "\",\n" +
                " \"username\": \"" + getCashier() + "\", \n" +
                " \"cashRegisterId\": " + App.cashRegister.getId() + ", \n" +
                " \"paymentMethod\": \"" + getPaymentMethod().getMethod() + "\", \n" +
                " \"receiptItems\": [\n" + ReceiptUtils.getReceiptItemsAsString(getReceiptItems()) + " ]\n}";
    }
}
