package ba.unsa.etf.si.models;

import ba.unsa.etf.si.models.Product;
import javax.persistence.*;

@Entity
@Table(name = "receipt_items")
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private double quantity;

    public ReceiptItem() { }

    public ReceiptItem(Product product, double quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }
}
