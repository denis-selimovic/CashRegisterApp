package ba.unsa.etf.si.models;

import javax.persistence.Table;
import javax.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Long productID;

    @Column
    private String productName;

    @Column
    private double price;

    @Column
    private double discount;

    @Column
    private double quantity;

    public OrderItem() {
    }

    public OrderItem(Product product) {
        this(product.getServerID(), product.getName(), product.getPrice(), product.getDiscount(), product.getTotal());
    }

    public OrderItem(Product product, double quantity) {
        this(product.getServerID(), product.getName(), product.getPrice(), product.getDiscount(), quantity);
    }

    public OrderItem(Long id, Long productID, String productName, double price, double discount, double quantity) {
        this(productID, productName, price, discount, quantity);
        this.id = id;
    }

    public OrderItem(Long productID, String productName, double price, double discount, double quantity) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductID() {
        return productID;
    }

    public void setProductID(Long productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return price - price * (discount / 100.0);
    }

    @Override
    public String toString() {
        return "{ \n" +
                " \"id\": " + getProductID() + ", \n" +
                " \"quantity\": " + getQuantity() + "\n" +
                "}";
    }
}
