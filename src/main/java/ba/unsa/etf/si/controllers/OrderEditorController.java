package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Order;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.HttpUtils;
import ba.unsa.etf.si.utility.IKonverzija;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

public class OrderEditorController {

    private static String TOKEN;

    @FXML
    private TableView<Product> orderItems;
    @FXML
    private TableColumn<Product, String> itemName, itemQuantity, itemTotalPrice;
    @FXML
    private GridView<Product> productsGrid;
    @FXML
    private TextField search;

    private Order order;
    private ObservableList<Product> products = FXCollections.observableArrayList();

    public OrderEditorController(Order order) {
        this.order = order;
        TOKEN = PrimaryController.currentUser.getToken();
    }

    @FXML
    public void initialize() {
        productsGrid.setCellFactory(new ProductGridCellFactory());
        productsGrid.setVerticalCellSpacing(10);
        productsGrid.setHorizontalCellSpacing(10);
        productsGrid.setCellWidth(150.0);
        productsGrid.setCellHeight(150.0);
        getProducts();
        search.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue == null || newValue.isEmpty()) {
                productsGrid.setItems(products);
                return;
            }
            if(!oldValue.equals(newValue)) search(newValue);
        });
    }

    private void search(String value) {
        productsGrid.setItems(products.stream().filter(p -> p.getName().toLowerCase().contains(value.toLowerCase()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList)));
    }

    private void getProducts() {
        HttpRequest GET = HttpUtils.GET(App.DOMAIN + "/api/products", "Authorization", "Bearer " + TOKEN);
        HttpUtils.send(GET, HttpResponse.BodyHandlers.ofString(), response -> {
            try {
                products = IKonverzija.getObservableProductListFromJSON(response);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                search.setDisable(false);
                productsGrid.setItems(products);
            });
        }, () -> {
            System.out.println("ERROR!");
        });
    }

    public static class ProductGridCell extends GridCell<Product> {

        @FXML private ImageView img;
        @FXML private Label price, name;

        private ProductGridCell() {
            loadFXML();
        }

        private void loadFXML() {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/productGrid.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void updateItem(Product product, boolean empty) {
            super.updateItem(product, empty);
            if (empty) {
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            } else {
                img.setImage(product.getImage());
                name.setText(product.getName());
                price.setText(String.format("%.2f", product.getTotalPrice()));
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    public static class ProductGridCellFactory implements Callback<GridView<Product>, GridCell<Product>> {

        @Override
        public GridCell<Product> call(GridView<Product> productGridView) {
            return new ProductGridCell();
        }
    }
}
