package ba.unsa.etf.si.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SuppliesController {

    public TableColumn productID;
    public TableColumn productImage;
    public TableColumn productName;
    public TableColumn quantityInStock;
    public TableView articleTable ;


    final ObservableList<Product> data = FXCollections.observableArrayList(
            new Product("a_123", "err_no_image", "Oklagija", "12"),
            new Product("a_52", "err_no_image", "Čupavci", "25"),
            new Product("at_235", "err_no_image", "Auspuh", "23"),
            new Product("a_15", "err_no_image", "Krigle", "33"),
            new Product("a_112", "err_no_image", "Sarma", "24")
    );

    @FXML
    public void initialize() {
       productID.setCellValueFactory(  new PropertyValueFactory<Product, String>("id"));
        productImage.setCellValueFactory(  new PropertyValueFactory<Product, String>("image"));
        productName.setCellValueFactory(  new PropertyValueFactory<Product, String>("name"));
       quantityInStock.setCellValueFactory(new PropertyValueFactory<Product, String>("quantity"));
       articleTable.setItems(data);
    }



    public static class Product {

        private final SimpleStringProperty id;
        private final SimpleStringProperty image;
        private final SimpleStringProperty name;
        private final SimpleStringProperty quantity;

        public Product(String id,String image, String name, String quantity) {
            this.id = new SimpleStringProperty(id);
            this.image =  new SimpleStringProperty(image);
            this.name =  new SimpleStringProperty(name);
            this.quantity =  new SimpleStringProperty(quantity);
        }

        public String getId() {
            return id.get();
        }

        public SimpleStringProperty idProperty() {
            return id;
        }

        public void setId(String id) {
            this.id.set(id);
        }

        public String getImage() {
            return image.get();
        }

        public SimpleStringProperty imageProperty() {
            return image;
        }

        public void setImage(String image) {
            this.image.set(image);
        }

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public String getQuantity() {
            return quantity.get();
        }

        public SimpleStringProperty quantityProperty() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity.set(quantity);
        }
    }






}
