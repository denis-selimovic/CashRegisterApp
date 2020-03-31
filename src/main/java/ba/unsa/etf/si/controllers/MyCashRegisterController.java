package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

public class MyCashRegisterController {

    private static String TOKEN;

    public TableColumn<Product, String> productName;
    public TableColumn<Product, String> productPrice;
    public TableColumn<Product, String> productQuantity;
    public TableColumn<Product, String> productDiscount;
    public TableColumn<Product, String> total;
    public TableView<Product> receiptTable;


    @FXML private ListView<Product> productsTable;

    @FXML private ChoiceBox<String> myCashRegisterSearchFilters;
    @FXML private TextField myCashRegisterSearchInput;
    @FXML private Label price;

    private ObservableList<Product> products = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        TOKEN = PrimaryController.currentUser.getToken();

        Callback<TableColumn<Product, String>, TableCell<Product, String>> cellFactory
                = (TableColumn<Product, String> param) -> new EditingCell();

        productName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        productPrice.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getPrice())));
        productDiscount.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getDiscount())));
        total.setCellFactory(param -> new TableCell<>() {
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
        productQuantity.setCellFactory(cellFactory);
        productQuantity.setCellValueFactory(cellData -> {
            Product p = cellData.getValue();
            return new SimpleStringProperty(Integer.toString(p.getTotal()));
        });
        addRemoveButtonToTable();


        productsTable.setCellFactory(new ProductCellFactory());
        getProducts();
        myCashRegisterSearchInput.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue == null || newValue.isEmpty()) {
                productsTable.setItems(products);
                return;
            }
            if(!oldValue.equals(newValue)) search();
        });
    }

    public double price() {
        return receiptTable.getItems().stream().mapToDouble( p -> {
            String format = String.format("%.2f", p.getTotalPrice());
            if(format.contains(",")) format = format.replace(",", ".");
            return Double.parseDouble(format);
        }).sum();
    }

    public String showPrice() {
        BigDecimal decimal = BigDecimal.valueOf(price());
        decimal = decimal.setScale(2, RoundingMode.HALF_UP);
        return String.format("%.2f", decimal.doubleValue());
    }

    public void search() {
        String filter = myCashRegisterSearchFilters.getValue();
        switch (filter) {
            case "Search by ID":
                productsTable.setItems(filterByID(getID()));
                break;
            case "Search by name":
                productsTable.setItems(filterByName(getName()));
                break;
        }
    }

    public String getName() {
        return myCashRegisterSearchInput.getText();
    }

    public int getID() {
        String text = myCashRegisterSearchInput.getText();
        int id;
        try {
            id = Integer.parseInt(text);
        }
        catch (NumberFormatException e) {
            id = -1;
        }
        return id;
    }

    private ObservableList<Product> filterByID(int id) {
        if(id == -1) return FXCollections.observableArrayList();
        return products.stream().filter(p -> p.getId() == id).collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
    }

    private ObservableList<Product> filterByName(String name) {
        return products.stream().filter(p -> p.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
    }

    public void getProducts() {
        HttpRequest GET = HttpUtils.GET(App.DOMAIN + "/api/products", "Authorization", "Bearer " + TOKEN);
        HttpUtils.send(GET, HttpResponse.BodyHandlers.ofString(), response -> {
            try {
                products = Product.getProductListFromJSON(response);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                productsTable.setItems(products);
            });
        }, () -> {
            System.out.println("ERROR!");
        });
    }

    private void addRemoveButtonToTable() {
        TableColumn<Product, Void> colBtn = new TableColumn<>("Remove");

        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<TableColumn<Product, Void>, TableCell<Product, Void>>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                final TableCell<Product, Void> cell = new TableCell<Product, Void>() {

                    private final Button btn = new Button();

                    {
                        btn.setOnAction(e -> removeFromReceipt(indexProperty().get()));
                        btn.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("img/cancel.png"))));
                        btn.getStyleClass().add("btn");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);
        colBtn.setResizable(false);
        receiptTable.getColumns().add(colBtn);
    }

    public void removeFromReceipt(int index) {
        receiptTable.getItems().remove(index).setTotal(1);
        receiptTable.refresh();
        price.setText(showPrice());
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
                    getTableView().getColumns().get(current).setVisible(false);
                    getTableView().getColumns().get(current).setVisible(true);
                    price.setText(showPrice());
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }

    public final class ProductCell extends ListCell<Product> {

        @FXML private Label productID, name;
        @FXML private JFXButton addBtn;

        public ProductCell() {
            loadFXML();
        }

        private void loadFXML() {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/product.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void updateItem(Product product, boolean empty) {
            super.updateItem(product, empty);
            if(empty) {
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
            else {
                productID.setText(Long.toString(product.getId()));
                name.setText(product.getName());
                addBtn.setOnAction(e -> {
                    if(!receiptTable.getItems().contains(product)) {
                        receiptTable.getItems().add(product);
                        price.setText(showPrice());
                    }
                });
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    public class ProductCellFactory implements Callback<ListView<Product>, ListCell<Product>> {

        @Override
        public ListCell<Product> call(ListView<Product> param) {
            return new ProductCell();
        }
    }

}
