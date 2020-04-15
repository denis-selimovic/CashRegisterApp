package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.gui.factory.ImageCellFactory;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.json.ProductUtils;
import ba.unsa.etf.si.routes.ProductRoutes;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import java.util.function.Consumer;

import static ba.unsa.etf.si.controllers.PrimaryController.currentUser;

public class SuppliesController {

    @FXML private TableColumn<Product, String> productID;
    @FXML private TableColumn<Product, Image> productImage;
    @FXML private TableColumn<Product, String> productName;
    @FXML private TableColumn<Product, String> quantityInStock;
    @FXML private TableColumn<Product, String> productUnit;
    @FXML private TableView<Product> articleTable;
    @FXML private TextField searchBar;

    private ObservableList<Product> data = FXCollections.observableArrayList();

    private final Consumer<String> callback = response -> {
        try {
            data = ProductUtils.getObservableProductListFromJSON(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
        FilteredList<Product> filterList = new FilteredList<>(data, b -> true);
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filterList.setPredicate(entry -> {
                if (newValue == null || newValue.isEmpty()) return true;
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
        ProductRoutes.getProducts(currentUser.getToken(), callback, () -> System.out.println("Could not get supplies!"));
        productID.setCellValueFactory(cellData -> new SimpleStringProperty(Long.toBinaryString(cellData.getValue().getServerID())));
        productName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        quantityInStock.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getQuantity())));
        productUnit.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUnit()));
        productImage.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getImage()));
        productImage.setCellFactory(new ImageCellFactory());
    }
}
