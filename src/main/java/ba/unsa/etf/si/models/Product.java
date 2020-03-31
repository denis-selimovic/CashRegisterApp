package ba.unsa.etf.si.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

public class Product {

    private Long id;
    private String name;
    private Image image;
    private Double quantity;
    private Double price;
    private Double discount;
    private int total = 1;


    public Product(Long id, String title, double quantity, double price, double discount) {
        this.id = id;
        this.name = title;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }


    private static Product getProductFromJSON(JSONObject json) {
        return new Product(json.getLong("id"), json.getString("name"), json.getDouble("quantity"),
                json.getDouble("price"), json.getDouble("discount"));
    }

    public static ObservableList<Product> getProductListFromJSON(String response) {
        ObservableList<Product> list = FXCollections.observableArrayList();
        JSONArray array = new JSONArray(response);
        for (int i = 0; i < array.length(); ++i) list.add(getProductFromJSON(array.getJSONObject(i)));
        return list;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

}
