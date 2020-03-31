package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.IKonverzija;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.HttpUtils;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;
import static ba.unsa.etf.si.controllers.PrimaryController.currentUser;


public class SuppliesController {

    public TableColumn<Product, String> productID;
    public TableColumn<Product, Image> productImage;
    public TableColumn<Product, String> productName;
    public TableColumn<Product, String> quantityInStock;
    public TableView<Product> articleTable;
    public TextField searchBar;
    public static int x = 0;
    public TableColumn<Product, String> productUnit;

    private ObservableList<Product> data = FXCollections.observableArrayList();
    private Image defaultImage = null;
    private String userToken = null;

    //CALLBACK koji se poziva nakon requesta
    Consumer<String> callback = (String str) -> {
        try {
            data = IKonverzija.getProductListFromJSON(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        FilteredList<Product> filterList = new FilteredList<>(data, b -> true);
        productID.setCellValueFactory(new PropertyValueFactory<Product, String>("id"));
        productImage.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getImage()));
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
                    } else {
                        imageview.setImage(null);
                    }
                }
            };
            //zakaci sliku na cell
            cell.setGraphic(imageview);
            return cell;
        });
        //postavka propertija za kolone
        productName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        quantityInStock.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getQuantity())));
        productUnit.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUnit()));
        //pretraga
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filterList.setPredicate(entry -> {
                //ako je textfield prazan ili "null" vrati sve proizvode
                if (newValue == null || newValue.isEmpty()) return true;

                // uporedi naziv proizvoda
                String lowerCaseFilter = newValue.toLowerCase();
                return entry.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });
        SortedList<Product> sortedList = new SortedList<>(filterList);
        sortedList.comparatorProperty().bind(articleTable.comparatorProperty());
        articleTable.setItems(sortedList);
    };


    @FXML
    public void initialize() {
        //slanje requesta
        userToken = currentUser.getToken();
        HttpRequest getSuppliesData = HttpUtils.GET(DOMAIN + "/api/products", "Authorization", "Bearer " + userToken);

        HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), callback, () -> {
            System.out.println("Something went wrong.");
        });
    }
}
