package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.utility.HttpUtils;
import com.sun.tools.jconsole.JConsoleContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    public TextField searchBar;
    public static int x = 0;

    private ObservableList<Product> data = FXCollections.observableArrayList();
    private Image defaultImage= null;

    //ako u image polju (u json fajlu) nije definisana slika u base64 formatu
    void setDefaultImage () throws IOException
    {
        FileInputStream inputstream = new FileInputStream("src/main/resources/ba/unsa/etf/si/img/no_icon.png");
        defaultImage = new Image (inputstream);
    }

    void populateObservableList (String jsonString) {
        JSONArray ja = new JSONArray(jsonString);

        //parsiram JSON fajl i ucitavam podatke u listu
        for (int i=0 ; i<ja.length(); i++) {
           JSONObject obj = ja.getJSONObject(i);
           JSONObject productObj = obj.getJSONObject("product");
          data.add(new Product(productObj.getNumber("id").toString(), Product.base64ToImageDecoder(productObj.getString("image")),productObj.getString("name"), Double.toString(obj.getDouble("quantity")) ));
        }
    }



    Consumer<String>  callback = (String str) -> {
        try {
            setDefaultImage();
            populateObservableList(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FilteredList<Product> filterList = new FilteredList<>(data, b ->true);
        productID.setCellValueFactory(  new PropertyValueFactory<Product, String>("id"));
        productImage.setCellFactory(param -> {
            //postavi imageview
            final ImageView imageview = new ImageView();
            //imageview.setPreserveRatio(true);
            imageview.setFitHeight(115);
            imageview.setFitWidth(115);

            //uspostavi tabelu
            TableCell<Product, Image> cell = new TableCell<Product, Image>() {
                public void updateItem(Image item, boolean empty) {
                    if (item != null) {
                        imageview.setImage(item);
                    }
                   else {
                        imageview.setImage(null);
                    }
                }
            };
            //zakaci sliku na cell
            cell.setGraphic(imageview);
            return cell;
        });

        productImage.setCellValueFactory(new PropertyValueFactory<Product, Image>("image"));
        productName.setCellValueFactory(  new PropertyValueFactory<Product, String>("name"));
        quantityInStock.setCellValueFactory(new PropertyValueFactory<Product, String>("quantity"));
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filterList.setPredicate(entry -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) return true;

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                return entry.getName().toLowerCase().indexOf(lowerCaseFilter) != -1;
            });
        });
        SortedList<Product> sortedList = new SortedList<>(filterList);
        sortedList.comparatorProperty().bind(articleTable.comparatorProperty());
        articleTable.setItems(sortedList);
    };

    @FXML
    public void initialize() {
        HttpRequest getSuppliesData = HttpUtils.GET("https://raw.github.com/Lino2007/FakeAPI/master/db.json", "Content-Type", "application/json");
        HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), callback);
    }


    //privremena klasa, potrebno ju je definirati u modelu
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
