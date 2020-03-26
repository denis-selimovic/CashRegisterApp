package ba.unsa.etf.si.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.image.*;

import javafx.scene.image.WritableImage;


import java.io.InputStream;
import java.util.*;




import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.server.ExportException;

public class SuppliesController {

    public TableColumn productID;
    public TableColumn productImage;
    public TableColumn productName;
    public TableColumn quantityInStock;
    public TableView articleTable ;

    private ObservableList<Product> data = null;
    private Image defaultImage= null;

    void setDefaultImage () throws IOException
    {

        FileInputStream inputstream = new FileInputStream("src/main/resources/ba/unsa/etf/si/img/no_icon.png");
        defaultImage = new Image (inputstream);
        data = FXCollections.observableArrayList(
                new Product("a_123",  defaultImage, "Oklagija", "12"),
                new Product("a_52", defaultImage, "Čupavci", "25"),
                new Product("at_235", defaultImage, "Auspuh", "23"),
                new Product("a_15", defaultImage, "Krigle", "33"),
                new Product("a_112", defaultImage, "Sarma", "24")
        );


    }
    @FXML
    public void initialize() {
        try {
            setDefaultImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        productID.setCellValueFactory(  new PropertyValueFactory<Product, String>("id"));
        productImage.setCellFactory(param -> {
            //Set up the ImageView
            final ImageView imageview = new ImageView();
            imageview.setFitHeight(50);
            imageview.setFitWidth(50);

            //Set up the Table
            TableCell<Product, Image> cell = new TableCell<Product, Image>() {
                public void updateItem(Image item, boolean empty) {
                    if (item != null) {
                        imageview.setImage(item);
                    }
                }
            };
            // Attach the imageview to the cell
            cell.setGraphic(imageview);
            return cell;
        });
        productImage.setCellValueFactory(new PropertyValueFactory<Product, Image>("image"));
        productName.setCellValueFactory(  new PropertyValueFactory<Product, String>("name"));
        quantityInStock.setCellValueFactory(new PropertyValueFactory<Product, String>("quantity"));
        articleTable.setItems(data);
    }



    public static class Product {

        private final SimpleStringProperty id;
        private  Image image;
        private final SimpleStringProperty name;
        private final SimpleStringProperty quantity;

        public Product(String id, Image image, String name, String quantity) {
            this.id = new SimpleStringProperty(id);
            this.image =  image;
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
        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
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

        public Image base64ToImageDecoder (Base64 input) {

          return null;

        }


    }

}
