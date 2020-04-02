package ba.unsa.etf.si.models;

import ba.unsa.etf.si.models.Product;
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
    private double quantity;

    public ReceiptItem() { }

    public ReceiptItem(Long productID, double quantity) {
        this.productID = productID;
        this.quantity = quantity;
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
}
