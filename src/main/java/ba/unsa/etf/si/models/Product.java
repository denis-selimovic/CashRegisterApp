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
    SimpleIntegerProperty id = new SimpleIntegerProperty();
    SimpleStringProperty title = new SimpleStringProperty();
    SimpleObjectProperty<Image> image = new SimpleObjectProperty<>();
    SimpleIntegerProperty quantity = new SimpleIntegerProperty();
    SimpleDoubleProperty price = new SimpleDoubleProperty();
    SimpleDoubleProperty discount = new SimpleDoubleProperty();
    SimpleObjectProperty<Branch> branchId = new SimpleObjectProperty<>();

    SimpleStringProperty companyName = new SimpleStringProperty();

    public Product(int id, String title) {
        this.id.set(id);
        this.title.set(title);
    }

    public Product(int id, String title, Branch branch) {
        this.id.set(id);
        this.title.set(title);
        this.branchId.set(branch);
    }



    public Product(int id, String title, int quantity, double price, double discount) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.discount = new SimpleDoubleProperty(discount);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public Image getImage() {
        return image.get();
    }

    public SimpleObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public double getDiscount() {
        return discount.get();
    }

    public SimpleDoubleProperty discountProperty() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount.set(discount);
    }

    public Branch getBranchId() {
        return branchId.get();
    }

    public SimpleObjectProperty<Branch> branchIdProperty() {
        return branchId;
    }

    public void setBranchId(Branch branchId) {
        this.branchId.set(branchId);
    }

    public String getCompanyName() {
        return branchId.getName();
    }

    private static Product getProductFromJSON(JSONObject json) {
        return new Product(json.getInt("id"), json.getString("name"), json.getInt("quantity"),
                json.getDouble("price"), json.getDouble("discount"));
    }

    public static ObservableList<Product> getProductListFromJSON(String response) {
        ObservableList<Product> list = FXCollections.observableArrayList();
        JSONArray array = new JSONArray(response);
        for(int i = 0; i < array.length(); ++i) list.add(getProductFromJSON(array.getJSONObject(i)));
        return list;
    }
}
