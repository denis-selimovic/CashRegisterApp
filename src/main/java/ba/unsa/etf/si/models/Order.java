package ba.unsa.etf.si.models;


import javax.persistence.Table;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Long serverID;

    @Column
    private String bartender;

    @Column
    private LocalDateTime creationDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItemList = new ArrayList<>();

    public Order() {
    }

    public Order(Long id, Long serverID, String bartender, LocalDateTime creationDate) {
        this(serverID, bartender, creationDate);
        this.id = id;
    }

    public Order(Long serverID, String bartender, LocalDateTime creationDate) {
        this(bartender, creationDate);
        this.serverID = serverID;
    }

    public Order(String bartender, LocalDateTime creationDate) {
        this.bartender = bartender;
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBartender() {
        return bartender;
    }

    public void setBartender(String bartender) {
        this.bartender = bartender;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }


    public Long getServerID() {
        return serverID;
    }

    public void setServerID(Long serverID) {
        this.serverID = serverID;
    }

    public double getTotalAmount() {
        double total = 0;
        for (OrderItem item : orderItemList) total += (item.getTotalPrice() * item.getQuantity());
        return BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private String getOrderItemsAsString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < orderItemList.size(); ++i) {
            builder.append(orderItemList.get(i).toString());
            if (i == orderItemList.size() - 1) continue;
            builder.append(",\n");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "{ \n" +
                " \"id\": " + getServerID() + ",\n" +
                " \"receiptItems\": [\n" + getOrderItemsAsString() + " ]\n}";
    }
}
