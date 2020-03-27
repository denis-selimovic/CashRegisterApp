package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.utility.HttpUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Consumer;


public class SuppliesController {

    public TableColumn productID;
    public TableColumn productImage;
    public TableColumn productName;
    public TableColumn quantityInStock;
    public TableView articleTable;


    private ObservableList<Product> data = FXCollections.observableArrayList();
    private Image defaultImage= null;

    void setDefaultImage () throws IOException
    {

        FileInputStream inputstream = new FileInputStream("src/main/resources/ba/unsa/etf/si/img/no_icon.png");
        defaultImage = new Image (inputstream);
    /*    data = FXCollections.observableArrayList(
                new Product("a_123",  defaultImage, "Oklagija", "12"),
                new Product("a_52", defaultImage, "Čupavci", "25"),
               new Product("at_235", defaultImage, "Auspuh", "23"),
                new Product("a_15", defaultImage, "Krigle", "33"),
                new Product("a_112", defaultImage, "Sarma", "24")
        ); */
    }

    void populateObservableList (String jsonString) {
        JSONArray ja = new JSONArray(jsonString);

        for (int i=0 ; i<ja.length(); i++) {
           JSONObject obj = ja.getJSONObject(i);
           JSONObject productObj = obj.getJSONObject("product");
          data.add(new Product(productObj.getNumber("id").toString(), Product.base64ToImageDecoder(productObj.getString("image")),productObj.getString("name"), Double.toString(obj.getDouble("quantity")) ));

        }

    }

    @FXML
    public void initialize() {
        HttpRequest getSuppliesData = HttpUtils.GET("https://raw.github.com/Lino2007/FakeAPI/master/db.json", "Content-Type", "application/json");
        Consumer<String>  callback = (String str) -> {
            try {
                setDefaultImage();
                populateObservableList(str);
            } catch (Exception e) {
                e.printStackTrace();
            }

            productID.setCellValueFactory(  new PropertyValueFactory<Product, String>("id"));
            productImage.setCellFactory(param -> {
                //Set up the ImageView
                final ImageView imageview = new ImageView();
                imageview.setFitHeight(75);
                imageview.setFitWidth(75);

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
        };
        HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), callback);

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

        public static Image base64ToImageDecoder (String base64input) {
          byte[] decodedBytes = Base64.getMimeDecoder().decode(base64input.split(",")[1]);
          ByteArrayInputStream imageArr =  new ByteArrayInputStream(decodedBytes);
          return new Image(imageArr);
        }
     }

}
