package ba.unsa.etf.si.models;

import javax.persistence.*;

@Entity
@Table(name = "receipt_items")
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;

    @Column
    private Long productID;

    @Column
    private String name;

    @Column
    private double price;

    @Column
    private double discount;

    @Column
    private double quantity;

    public ReceiptItem() { }

    public ReceiptItem(Product product) {
        this.productID = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.discount = product.getDiscount();
        this.quantity = product.getTotal();
    }

    public ReceiptItem(OrderItem item) {
        this.name = item.getProductName();
        this.productID = item.getProductID();
        this.price = item.getPrice();
        this.discount = item.getDiscount();
        this.quantity = item.getQuantity();
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getProductID() {
        return productID;
    }

    public void setProductID(Long productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getTotalPrice() {
        return price - price * (discount / 100);
    }

    @Override
    public String toString() {
        return "{ \n" +
                " \"id\": " + getProductID() + ", \n" +
                " \"quantity\": " + getQuantity() + "\n" +
                "}";
    }
}
