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

    @Transient
    private String unit;
    public ReceiptItem() { }

    public ReceiptItem(Product product) {
        this.productID = product.getServerID();
        this.name = product.getName();
        this.price = product.getPrice();
        this.discount = product.getDiscount();
        this.quantity = product.getTotal();
        this.unit = "kom";
    }

    public ReceiptItem(OrderItem item) {
        this.name = item.getProductName();
        this.productID = item.getProductID();
        this.price = item.getPrice();
        this.discount = item.getDiscount();
        this.quantity = item.getQuantity();
        this.unit = "kom";
    }

    public String getUnit() { return unit;}

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
        return (price - price * (discount / 100))*quantity;
    }

    @Override
    public String toString() {
        return "{ \n" +
                " \"id\": " + getProductID() + ", \n" +
                " \"quantity\": " + getQuantity() + "\n" +
                "}";
    }

    public String getReceiptItemFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("  #").append(productID);
        sb.append(getProductFormat());
        sb.append("\tQuantity: ").append(quantity);
        sb.append("\tCost: ").append(price).append("\u20ac");
        return sb.toString();
    }

    public String getProductFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t(").append(productID).append(")\t").append(name).append("\t").append(price).append("\u20ac\tvat ").append(discount).append("%");
        return sb.toString();
    }
}
