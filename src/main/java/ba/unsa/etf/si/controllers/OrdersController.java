package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.gui.factory.OrderCellFactory;
import ba.unsa.etf.si.models.Order;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.routes.OrderRoutes;
import ba.unsa.etf.si.routes.ProductRoutes;
import ba.unsa.etf.si.utility.interfaces.ReceiptLoader;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import ba.unsa.etf.si.utility.json.OrderUtils;
import ba.unsa.etf.si.utility.json.ProductUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.GridView;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public class OrdersController {

    @FXML private JFXButton addBtn;
    @FXML private GridView<Order> grid;

    private ObservableList<Product> products;
    private ObservableList<Order> orders;
    private final ReceiptLoader receiptLoader;

    public OrdersController(ReceiptLoader receiptLoader) {
        this.receiptLoader = receiptLoader;
    }

    @FXML
    public void initialize() {
        grid.setCellFactory(new OrderCellFactory(this::createReceiptFromOrder, this::editOrder, this::removeOrder));
        grid.setHorizontalCellSpacing(20);
        grid.setVerticalCellSpacing(20);
        grid.setCellHeight(280);
        grid.setCellWidth(350);
        addBtn.setOnAction(e -> addOrder());
        ProductRoutes.getProducts(PrimaryController.currentUser.getToken(), productsCallback, () -> System.out.println("Could not fetch products!"));
    }

    private void addOrder() {
        Platform.runLater(() -> grid.getItems().add(new Order(1L, PrimaryController.currentUser.getUsername(), LocalDateTime.now())));
    }

    private void removeOrder(Order order) {
        Platform.runLater(() -> StageUtils.showAlert("Warning", "Are you sure you want to delete the order?\n Action can not be undone.",
                Alert.AlertType.WARNING, ButtonType.YES, ButtonType.NO).ifPresent(p -> {
                    if(p.getButtonData() == ButtonBar.ButtonData.YES) deleteOrder(order);
        }));
    }

    private void deleteOrder(Order order) {
        OrderRoutes.deleteOrder(order.getServerID(), response -> Platform.runLater(() -> grid.getItems().remove(order)), () -> System.out.println("Error while deleting order!"));
    }

    private void editOrder(Order order) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLUtils.loadCustomController("fxml/orderEditor.fxml", c -> new OrderEditorController(order, products))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StageUtils.centerStage(stage, 1000, 1000);
        StageUtils.setStage(stage, "Order Editor", false, StageStyle.UNDECORATED, Modality.APPLICATION_MODAL);
        stage.getScene().getStylesheets().add(App.class.getResource("css/notification.css").toExternalForm());
        stage.show();
    }

    private void createReceiptFromOrder(Order order) {
        receiptLoader.onReceiptLoaded(new Receipt(order));
    }

    private final Consumer<String> ordersCallback = response -> new Thread(() -> {
        orders = OrderUtils.getOrdersFromJSON(response, products);
        Platform.runLater(() -> grid.setItems(orders));
    }).start();

    private final Consumer<String> productsCallback = response -> {
        products = ProductUtils.getObservableProductListFromJSON(response);
        OrderRoutes.getOrders(ordersCallback, () -> System.out.println("Could not fetch orders!"));
    };
}
