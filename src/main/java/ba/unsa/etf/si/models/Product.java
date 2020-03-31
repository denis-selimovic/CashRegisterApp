package ba.unsa.etf.si.models;

import ba.unsa.etf.si.App;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.time.LocalDate;

import static ba.unsa.etf.si.utility.Base64Utils.base64ToImageDecoder;

public class Product {

    private Long id;
    private String name;
    private Image image;
    private Double quantity;
    private Double price;
    private Double discount;
    private String unit;
    private int total = 1;


    public Product(Long id, String title, double quantity, double price, double discount) {
        this.id = id;
        this.name = title;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    public Product(Long id, String name, double price, String base64Image, String measurementUnit, double discount, double quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.unit = measurementUnit;
        this.discount = discount;
        this.quantity = quantity;
        setImage(base64Image);
    }

    public Product(Long id, String name, double price, Image image, String unit, double discount, double quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.unit = unit;
        this.discount = discount;
        this.quantity = quantity;
    }


    private static Product getProductFromJSON(JSONObject jsonObj) {
        String imageString = null;
        if (jsonObj.get("imageBase64") != null) imageString = jsonObj.getString("imageBase64");
        return new Product(jsonObj.getLong("id"), jsonObj.getString("name"), jsonObj.getDouble("price"), imageString,
                jsonObj.getString("measurementUnit"), jsonObj.getDouble("discount"), jsonObj.getDouble("quantity"));
    }

    public static Product getJSON(JSONObject obj) {
        return getProductFromJSON(obj);
    }

    public static Product getJSON(String json) {
        return getProductFromJSON(new JSONObject(json));
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

    public static Image getDefaultImage() throws IOException {
        return new Image(App.class.getResourceAsStream("img/no_icon.png"));
    }
}
