package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.gui.factory.EditingCellFactory;
import ba.unsa.etf.si.gui.factory.ProductGridCellFactory;
import ba.unsa.etf.si.gui.factory.TotalPriceCellFactory;
import ba.unsa.etf.si.models.Order;
import ba.unsa.etf.si.models.OrderItem;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.json.ProductUtils;
import ba.unsa.etf.si.utility.server.HttpUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.GridView;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderEditorController {

    @FXML private JFXButton cancelBtn, saveBtn;
    @FXML private Label priceLbl;
    @FXML private TableView<Product> orderItems;
    @FXML private TableColumn<Product, String> itemName, itemQuantity, itemTotalPrice;
    @FXML private GridView<Product> productsGrid;
    @FXML private TextField search;

    private final Order order;
    private final ObservableList<Product> products;

    public OrderEditorController(Order order, ObservableList<Product> products) {
        this.order = order;
        this.products = products;
    }

    private void setupTable() {
        itemName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        itemQuantity.setCellFactory(new EditingCellFactory(this::removeFromReceipt));
        itemQuantity.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getTotal())));
        itemTotalPrice.setCellFactory(new TotalPriceCellFactory());
        orderItems.setItems(FXCollections.observableList(ProductUtils.getProductsFromOrder(products, order.getOrderItemList())));
        orderItems.itemsProperty().addListener((observableValue, products, t1) -> {
            priceLbl.setText(showPrice());
        });
    }

    private void setupGrid() {
        productsGrid.setCellFactory(new ProductGridCellFactory(this::addProduct));
        productsGrid.setVerticalCellSpacing(10);
        productsGrid.setHorizontalCellSpacing(10);
        productsGrid.setCellWidth(150.0);
        productsGrid.setCellHeight(150.0);
        productsGrid.setItems(products);
    }

    @FXML
    public void initialize() {
        setupTable();
        setupGrid();
        search.setDisable(false);
        search.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue == null || newValue.isEmpty()) {
                productsGrid.setItems(products);
                return;
            }
            if(!oldValue.equals(newValue)) search(newValue);
        });
        priceLbl.setText(showPrice());
        saveBtn.setOnAction(e -> save());
        cancelBtn.setOnAction(e -> cancel());
    }

    private void cancel() {
        Platform.runLater(() -> {
            showInformation("Warning", "Are you sure you want to discard changes?", Alert.AlertType.WARNING, ButtonType.YES, ButtonType.NO)
                    .ifPresent(btn -> {
                        if(btn.getButtonData() == ButtonBar.ButtonData.YES) ((Stage) cancelBtn.getScene().getWindow()).close();
                    });
        });
    }

    private void updateOrder() {
        HttpRequest PUT = HttpUtils.PUT(HttpRequest.BodyPublishers.ofString(order.toString()), App.DOMAIN + "/api/orders", "Authorization", "Bearer " + PrimaryController.currentUser.getToken(), "Content-Type", "application/json");
        HttpUtils.send(PUT, HttpResponse.BodyHandlers.ofString(), response -> {
            JSONObject resJSON = new JSONObject(response);
            Platform.runLater(() -> {
                showInformation("Update info", resJSON.getString("message"), Alert.AlertType.INFORMATION, ButtonType.CLOSE).ifPresent(p -> ((Stage) orderItems.getScene().getWindow()).close());
            });
        }, () -> System.out.println("ERROR"));
    }

    private Optional<ButtonType> showInformation(String title, String text, Alert.AlertType type, ButtonType... types) {
        Alert alert = new Alert(type, "", types);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.getDialogPane().getStylesheets().add(App.class.getResource("css/alert.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        return alert.showAndWait();
    }

    private void save() {
        order.setOrderItemList(orderItems.getItems().stream().map(OrderItem::new).collect(Collectors.toList()));
        updateOrder();
    }

    private void search(String value) {
        productsGrid.setItems(products.stream().filter(p -> p.getName().toLowerCase().contains(value.toLowerCase()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList)));
    }

    private void addProduct(Product product) {
        if(orderItems.getItems().contains(product) || product.getQuantity() < 1) return;
        Platform.runLater(() -> {
            product.setTotal(1);
            orderItems.getItems().add(product);
            orderItems.refresh();
            priceLbl.setText(showPrice());
        });
    }

    private void removeFromReceipt(Product p) {
        p.setTotal(0);
        orderItems.getItems().remove(p);
        orderItems.refresh();
        priceLbl.setText(showPrice());
    }

    private double price() {
        return orderItems.getItems().stream().mapToDouble( p -> {
            String format = String.format("%.2f", p.getTotalPrice());
            if(format.contains(",")) format = format.replace(",", ".");
            return Double.parseDouble(format);
        }).sum();
    }

    private String showPrice() {
        BigDecimal decimal = BigDecimal.valueOf(price()).setScale(2, RoundingMode.HALF_UP);
        return String.format("%.2f", decimal.doubleValue());
    }
}
