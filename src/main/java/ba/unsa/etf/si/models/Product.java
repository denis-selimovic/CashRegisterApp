package ba.unsa.etf.si.models;

import ba.unsa.etf.si.App;
import javafx.scene.image.Image;

import javax.persistence.Table;
import javax.persistence.*;
import java.io.IOException;
import java.util.Objects;

import static ba.unsa.etf.si.utility.image.Base64Utils.base64ToImageDecoder;
import static ba.unsa.etf.si.utility.image.Base64Utils.imageToBase64Encoder;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "server_id")
    private Long serverID;
    @Column(name = "name")
    private String name;
    @Column(name = "quantity")
    private Double quantity;
    @Column(name = "price")
    private Double price;
    @Column(name = "discount")
    private Double discount;
    @Transient
    private Double vat;
    @Transient
    private String unit;
    @Transient
    private Image image;
    @Transient
    private int total = 0;

    public Product() {
    }

    public Product(Long id, Long serverID, String name, Double quantity, Double price, Double discount) {
        this(serverID, name, quantity, price, discount);
        this.id = id;
    }

    public Product(String name, double price, String base64Image, String measurementUnit, double discount, double quantity) {
        this.name = name;
        this.price = price;
        this.unit = measurementUnit;
        this.discount = discount;
        this.quantity = quantity;
        setImage(base64Image);
    }


    public Product(Long serverID, String title, double quantity, double price, double discount) {
        this.serverID = serverID;
        this.name = title;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    public Product(Long serverID, String name, double price, String base64Image, String measurementUnit, double discount, double quantity, double vat) {
        this.serverID = serverID;
        this.name = name;
        this.price = price;
        this.unit = measurementUnit;
        this.discount = discount;
        this.quantity = quantity;
        this.vat = vat;
        setImage(base64Image);
    }

    public Product(Long serverID, String name, double price, Image image, String unit, double discount, double quantity) {
        this.serverID = serverID;
        this.name = name;
        this.price = price;
        this.image = image;
        this.unit = unit;
        this.discount = discount;
        this.quantity = quantity;
    }

    public static Image getDefaultImage() throws IOException {
        return new Image(App.class.getResourceAsStream("img/no_icon.png"));
    }


    public double getTotalPrice() {
        return (price - price * (discount / 100)) * total;
    }

    public void setTotal(int total) {
        if (this.total <= quantity) {
            this.total = total;
        }
    }

    public int getTotal() {
        return total;
    }

    public Long getServerID() {
        return serverID;
    }

    public void setServerID(Long serverID) {
        this.serverID = serverID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }


    //test 1
    public void setImage(String base64Image) {
        if (base64Image == null) {
            try {
                setDefaultImage();
            } catch (Exception e) {
                e.printStackTrace();
                this.image = null;
            }
        } else {

            try {
                this.image = base64ToImageDecoder(base64Image);
            } catch (Exception e) {
                e.printStackTrace();
                this.image = null;
            }
        }
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    void setDefaultImage() throws IOException {
        image = new Image(App.class.getResourceAsStream("img/no_icon.png"));
    }

    @Override
    public String toString() {
        return " { \n" +
                " \"id\" :" + this.getServerID() + ",\n" +
                " \"name\" :\"" + this.getName() + "\",\n" +
                " \"quantity\" :" + this.getQuantity() + ",\n" +
                " \"price\" :" + this.getPrice() + ",\n" +
                " \"discount\" :" + this.getDiscount() + ",\n" +
                " \"measurementUnit\" : \"" + this.getUnit() + "\",\n" +
                " \"imageBase64\" : \"" + imageToBase64Encoder(this.getImage()) + "\"\n }";
    }

    public String stringify() {
        return " { \n" +
                " \"id\" :" + this.getServerID() + ",\n" +
                " \"name\" :\"" + this.getName() + "\",\n" +
                " \"quantity\" :" + this.getQuantity() + ",\n" +
                " \"price\" :" + this.getPrice() + ",\n" +
                " \"discount\" :" + this.getDiscount() + ",\n" +
                " \"measurementUnit\" : \"" + this.getUnit() + "\",\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return serverID.equals(product.serverID) &&
                name.equals(product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverID, name);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPriceAfterDiscount() {
        return price - price * (discount / 100);
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }
}
