package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.HttpUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

public class MyCashRegisterController {

    private static String TOKEN;

    public TableColumn<Product, String> productName;
    public TableColumn<Product, Double> productPrice;
    public TableColumn<Product, String> productQuantity;
    public TableColumn<Product, Double> productDiscount;
    public TableColumn<Product, String> total;
    public TableView<Product> receiptTable;

    public Label productListLabel;
    public TableView<Product> productsTable;
    public TableColumn<Product, Integer> productId;
    public TableColumn<Product, String> productTitle;



    @FXML private ChoiceBox<String> myCashRegisterSearchFilters;
    @FXML private TextField myCashRegisterSearchInput;
    @FXML private Label price;

    private ObservableList<Product> products = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        TOKEN = PrimaryController.currentUser.getToken();

        Callback<TableColumn<Product, String>, TableCell<Product, String>> cellFactory
                = (TableColumn<Product, String> param) -> new EditingCell();

        productName.setCellValueFactory(new PropertyValueFactory<Product, String>("title"));
        productPrice.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        productDiscount.setCellValueFactory(new PropertyValueFactory<Product, Double>("discount"));
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

        productId.setCellValueFactory(new PropertyValueFactory<>("id"));
        productTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        addButtonToTable();
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
            return Double.parseDouble(format);
        }).sum();
    }

    public String showPrice() {
        BigDecimal decimal = BigDecimal.valueOf(price());
        decimal = decimal.setScale(2, RoundingMode.HALF_UP);
        return Double.toString(decimal.doubleValue());
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
        return products.stream().filter(p -> p.getTitle().toLowerCase().contains(name.toLowerCase())).collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
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

                    private final Button btn = new Button("Remove");

                    {
                        btn.setOnAction(e -> removeFromReceipt(indexProperty().get()));
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

        receiptTable.getColumns().add(colBtn);
    }

    public void removeFromReceipt(int index) {
        receiptTable.getItems().remove(index).setTotal(1);
        receiptTable.refresh();
        price.setText(showPrice());
    }

    public void addButtonToTable() {
        TableColumn<Product, Void> buttonColumn = new TableColumn<>("Add");
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(TableColumn<Product, Void> productVoidTableColumn) {
                final TableCell<Product, Void> cell = new TableCell<Product, Void>() {
                    private final Button btnAction = new Button("Add to Cart");

                    {
                        btnAction.setOnAction(event -> {
                            Product p = getTableView().getItems().get(getIndex());
                            if(!receiptTable.getItems().contains(p)) {
                                receiptTable.getItems().add(p);
                                price.setText(showPrice());
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnAction);
                        }
                    }

                };
                return cell;
            }
        };
        buttonColumn.setCellFactory(cellFactory);
        productsTable.getColumns().add(buttonColumn);
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
                        p.setTotal((int) p.getQuantity());
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
}
