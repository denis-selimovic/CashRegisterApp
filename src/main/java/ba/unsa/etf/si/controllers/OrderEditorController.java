package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.gui.factory.EditingCellFactory;
import ba.unsa.etf.si.gui.factory.ProductGridCellFactory;
import ba.unsa.etf.si.gui.factory.TotalPriceCellFactory;
import ba.unsa.etf.si.models.Order;
import ba.unsa.etf.si.models.OrderItem;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.routes.OrderRoutes;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import ba.unsa.etf.si.utility.modelutils.ProductUtils;
import ba.unsa.etf.si.utility.stream.StreamUtils;
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
import java.util.function.Consumer;
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

    private final Consumer<String> updateCallback = response -> {
        Platform.runLater(() -> StageUtils.showAlert("Update info", new JSONObject(response).getString("message"), Alert.AlertType.INFORMATION, ButtonType.CLOSE)
                .ifPresent(p -> ((Stage) orderItems.getScene().getWindow()).close()));
    };

    private void setupTable() {
        itemName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        itemQuantity.setCellFactory(new EditingCellFactory(this::removeFromReceipt, () -> priceLbl.setText(showPrice())));
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
            if(!oldValue.equals(newValue)) productsGrid.setItems(FXCollections.observableList(StreamUtils.search(products, newValue)));
        });
        priceLbl.setText(showPrice());
        saveBtn.setOnAction(e -> save());
        cancelBtn.setOnAction(e -> cancel());
    }

    private void cancel() {
        Platform.runLater(() -> StageUtils.showAlert("Warning", "Are you sure you want to discard changes?", Alert.AlertType.WARNING, ButtonType.YES, ButtonType.NO)
                .ifPresent(btn -> {
                    if(btn.getButtonData() == ButtonBar.ButtonData.YES) ((Stage) cancelBtn.getScene().getWindow()).close();;
                }));
    }

    private void save() {
        order.setOrderItemList(orderItems.getItems().stream().map(OrderItem::new).collect(Collectors.toList()));
        OrderRoutes.updateOrder(order, updateCallback, () -> System.out.println("Error while updating order!"));
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

    private String showPrice() {
        BigDecimal decimal = BigDecimal.valueOf(StreamUtils.price(orderItems.getItems())).setScale(2, RoundingMode.HALF_UP);
        return String.format("%.2f", decimal.doubleValue());
    }
}
