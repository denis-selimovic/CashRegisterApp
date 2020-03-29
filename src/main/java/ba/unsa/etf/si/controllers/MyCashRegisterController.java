package ba.unsa.etf.si.controllers;
import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.HttpUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

public class MyCashRegisterController {


    public TableColumn productID;
    public TableColumn productName;
    public TableColumn productPrice;
    public TableColumn productQuantity;
    public TableColumn productDiscount;
    public TableColumn total;
    public TableView receiptTable;

    public Label productListLabel;
    public TableView<Product> productsTable;
    public TableColumn<Product, Integer> productId;
    public TableColumn<Product, String> productTitle;


    @FXML private ChoiceBox<String> myCashRegisterSearchFilters;
    @FXML private TextField myCashRegisterSearchInput;

    private SimpleBooleanProperty productListLabelVisibleProperty = new SimpleBooleanProperty(true);

    private ObservableList<Product> products = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        /*productID.setCellValueFactory(new PropertyValueFactory<Receipt, Integer>("id"));
        productName.setCellValueFactory(new PropertyValueFactory<Receipt, String>("name"));
        productPrice.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("price"));
        productDiscount.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("discount"));
        total.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("totalPrice"));
        addSpinner();
        addRemoveButtonToTable();
        receiptTable.setItems(data);*/

        productListLabel.visibleProperty().bindBidirectional(productListLabelVisibleProperty);
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
        HttpRequest GET = HttpUtils.GET(App.DOMAIN + "/api/products", "Authorization", "Bearer " + PrimaryController.currentUser.getToken());
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
        /*TableColumn<Receipt, Void> colBtn = new TableColumn("Remove");

        Callback<TableColumn<Receipt, Void>, TableCell<Receipt, Void>> cellFactory = new Callback<TableColumn<Receipt, Void>, TableCell<Receipt, Void>>() {
            @Override
            public TableCell<Receipt, Void> call(final TableColumn<Receipt, Void> param) {
                final TableCell<Receipt, Void> cell = new TableCell<Receipt, Void>() {

                    private final Button btn = new Button("Remove");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Receipt data1 = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data1);
                            receiptTable.getItems().removeAll(data1);
                        });
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

        receiptTable.getColumns().add(colBtn);*/

    }

    private void addSpinner(){
       /* TableColumn<Receipt, Spinner> SpinnerCol = new TableColumn<Receipt, Spinner>("Quantity");

        SpinnerCol.setMaxWidth(100);
        SpinnerCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Receipt, Spinner>, ObservableValue<Spinner>>() {

            @Override
            public ObservableValue<Spinner> call(
                    TableColumn.CellDataFeatures<Receipt, Spinner> arg0) {
                Receipt strumentoObject = arg0.getValue();

                Spinner<Integer> quantita = new Spinner<Integer>(1, 100, 1);
                quantita.valueProperty().addListener(
                        (obs, oldValue, newValue) -> {

                            System.out.println("New value: " + newValue);


                        }
                );

                return new SimpleObjectProperty<Spinner>(quantita);

            }

        });
        receiptTable.getColumns().add(SpinnerCol);*/
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
                            System.out.println("Adding to cart " + p.getTitle());
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
}
