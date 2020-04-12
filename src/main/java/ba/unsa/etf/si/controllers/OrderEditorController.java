package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Order;
import ba.unsa.etf.si.models.OrderItem;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.HttpUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderEditorController {

    private static String TOKEN;

    @FXML
    private JFXButton cancelBtn, saveBtn;
    @FXML
    private Label priceLbl;
    @FXML
    private TableView<Product> orderItems;
    @FXML
    private TableColumn<Product, String> itemName, itemQuantity, itemTotalPrice;
    @FXML
    private GridView<Product> productsGrid;
    @FXML
    private TextField search;

    private final Order order;
    private final ObservableList<Product> products;

    public OrderEditorController(Order order, ObservableList<Product> products) {
        this.order = order;
        this.products = products;
        TOKEN = PrimaryController.currentUser.getToken();
    }

    @FXML
    public void initialize() {
        search.setDisable(false);
        itemName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        itemQuantity.setCellFactory(new EditingCellFactory());
        itemQuantity.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getTotal())));
        itemTotalPrice.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (!empty) {
                    int current = indexProperty().getValue();
                    Product p = param.getTableView().getItems().get(current);
                    setText(String.format("%.2f", p.getTotalPrice()));
                } else {
                    setText(null);
                }
            }
        });
        orderItems.setItems(FXCollections.observableList(getProductsFromOrder(order.getOrderItemList())));

        productsGrid.setCellFactory(new ProductGridCellFactory());
        productsGrid.setVerticalCellSpacing(10);
        productsGrid.setHorizontalCellSpacing(10);
        productsGrid.setCellWidth(150.0);
        productsGrid.setCellHeight(150.0);
        productsGrid.setItems(products);
        priceLbl.setText("0.00");
        search.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue == null || newValue.isEmpty()) {
                productsGrid.setItems(products);
                return;
            }
            if(!oldValue.equals(newValue)) search(newValue);
        });
        saveBtn.setOnAction(e -> save());
    }

    private List<Product> getProductsFromOrder(List<OrderItem> items) {
        List<Product> productsItems = new ArrayList<>();
        products.forEach(p -> {
            items.forEach(i -> {
                if(p.getServerID().equals(i.getProductID())) {
                    p.setTotal((int) i.getQuantity());
                    productsItems.add(p);
                }
            });
        });
        return productsItems;
    }

    private void updateOrder() {
        HttpRequest PUT = HttpUtils.PUT(HttpRequest.BodyPublishers.ofString(order.toString()), App.DOMAIN + "/api/orders", "Authorization", "Bearer " + PrimaryController.currentUser.getToken(), "Content-Type", "application/json");
        HttpUtils.send(PUT, HttpResponse.BodyHandlers.ofString(), response -> {
            JSONObject resJSON = new JSONObject(response);
            Platform.runLater(() -> {
                showInformation("Update info", resJSON.getString("message")).ifPresent(p -> {
                    ((Stage) orderItems.getScene().getWindow()).close();
                });
            });
        }, () -> System.out.println("ERROR"));
    }

    private Optional<ButtonType> showInformation(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.CLOSE);
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

    private void removeFromReceipt(int current) {
        orderItems.getItems().remove(current).setTotal(0);
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

    public class ProductGridCell extends GridCell<Product> {

        @FXML private JFXButton addBtn;
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
                name.setText(product.getName());
                price.setText(String.format("%.2f", product.getPriceAfterDiscount()));
                addBtn.setOnAction(e -> OrderEditorController.this.addProduct(product));
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    public class ProductGridCellFactory implements Callback<GridView<Product>, GridCell<Product>> {

        @Override
        public GridCell<Product> call(GridView<Product> productGridView) {
            return new ProductGridCell();
        }
    }

    class EditingCell extends TableCell<Product, String> {

        private TextField textField;

        private EditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(item);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnAction((e) -> commitEdit(textField.getText()));
            textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
                if (!newValue.matches("[0-9\u0008]*")) {
                    textField.setText(newValue.replaceAll("[^\\d\b]", ""));
                }
            });
            textField.setOnKeyPressed(e -> {
                if(e.getCode().equals(KeyCode.ENTER)) {
                    int current = indexProperty().get();
                    if(getText().isEmpty()) {
                        getTableView().getItems().get(current).setTotal(1);
                        setText("1");
                    }
                    if(getText().equals("0")) {
                        removeFromReceipt(current);
                        return;
                    }
                    Product p = getTableView().getItems().get(current);
                    if(p.getQuantity() < Integer.parseInt(getText())) {
                        p.setTotal((int)p.getQuantity().doubleValue()) ;
                        setText(Integer.toString(p.getTotal()));
                    }
                    else p.setTotal(Integer.parseInt(getText()));
                    Platform.runLater(() -> {
                        orderItems.refresh();
                        priceLbl.setText(showPrice());
                    });

                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }

    public class EditingCellFactory implements Callback<TableColumn<Product, String>, TableCell<Product, String>> {

        @Override
        public TableCell<Product, String> call(TableColumn<Product, String> productStringTableColumn) {
            return new EditingCell();
        }
    }
}
