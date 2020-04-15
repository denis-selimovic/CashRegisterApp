package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.gui.factory.OrderCellFactory;
import ba.unsa.etf.si.models.Order;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.utility.interfaces.ReceiptLoader;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import ba.unsa.etf.si.utility.json.OrderUtils;
import ba.unsa.etf.si.utility.json.ProductUtils;
import ba.unsa.etf.si.utility.server.HttpUtils;
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
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Optional;

public class OrdersController {

    @FXML
    private JFXButton addBtn;
    @FXML
    private GridView<Order> grid;

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
        getProducts();
    }

    private void addOrder() {
        Platform.runLater(() -> grid.getItems().add(new Order(1L, PrimaryController.currentUser.getUsername(), LocalDateTime.now())));
    }

    public void removeOrder(Order order) {
        Platform.runLater(() -> {
            showInformation("Warning", "Are you sure you want to delete the order? Action can not be undone.", Alert.AlertType.WARNING, ButtonType.YES, ButtonType.NO)
                    .ifPresent(p -> {
                        if(p.getButtonData() == ButtonBar.ButtonData.YES) deleteOrder(order);
                    });
        });
    }

    private void deleteOrder(Order order) {
        HttpRequest DELETE = HttpUtils.DELETE(App.DOMAIN + "/api/orders/" + order.getServerID(), "Authorization", "Bearer " + PrimaryController.currentUser.getToken());
        HttpUtils.send(DELETE, HttpResponse.BodyHandlers.ofString(), response -> {
            Platform.runLater(() -> grid.getItems().remove(order));
        }, () -> System.out.println("ERROR IN DELETING ORDER!"));
    }

    public void editOrder(Order order) {
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

    public void createReceiptFromOrder(Order order) {
        receiptLoader.onReceiptLoaded(new Receipt(order));
    }

    private void getOrders() {
        HttpRequest GET = HttpUtils.GET(App.DOMAIN + "/api/orders", "Authorization", "Bearer " + PrimaryController.currentUser.getToken());
        HttpUtils.send(GET, HttpResponse.BodyHandlers.ofString(), response -> {
            new Thread(() -> {
                orders = OrderUtils.getOrdersFromJSON(response, products);
                Platform.runLater(() -> grid.setItems(orders));
            }).start();
        }, () -> System.out.println("ERROR!"));
    }

    private Optional<ButtonType> showInformation(String title, String text, Alert.AlertType type, ButtonType... types) {
        Alert alert = new Alert(type, "", types);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.getDialogPane().getStylesheets().add(App.class.getResource("css/alert.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        return alert.showAndWait();
    }

    private void getProducts() {
        HttpRequest GET = HttpUtils.GET(App.DOMAIN + "/api/products", "Authorization", "Bearer " + PrimaryController.currentUser.getToken());
        HttpUtils.send(GET, HttpResponse.BodyHandlers.ofString(), response -> {
            try {
                products = ProductUtils.getObservableProductListFromJSON(response);
                getOrders();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }, () -> System.out.println("ERROR!"));
    }
}
