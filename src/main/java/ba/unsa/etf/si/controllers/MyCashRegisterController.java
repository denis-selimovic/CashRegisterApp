package ba.unsa.etf.si.controllers;
import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Branch;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.HttpUtils;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MyCashRegisterController {


    public TextField myCashRegisterSearchInput = new TextField();

    public TableColumn productID;
    public TableColumn productName;
    public TableColumn productPrice;
    public TableColumn productQuantity;
    public TableColumn productDiscount;
    public TableColumn total;
    public TableView receiptTable;

    public Label productListLabel;
    public TableView<Product> productsTable;
    public TableColumn productId;
    public TableColumn productTitle;
    public TableColumn<Product,String> productCompany;

    private SimpleBooleanProperty productListLabelVisibleProperty = new SimpleBooleanProperty(true);

    private ObservableList<Product> products = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        /*productID.setCellValueFactory(new PropertyValueFactory<Receipt, Integer>("id"));
        productName.setCellValueFactory(new PropertyValueFactory<Receipt, String>("name"));
        productPrice.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("price"));
        productDiscount.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("discount"));
        total.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("totalPrice"));



        data.add(new Receipt(1,"nescafe", 2.30, 0.0));
        data.add(new Receipt(2,"7Days", 1.30,0.0));
        addSpinner();
        addRemoveButtonToTable();
        receiptTable.setItems(data);*/

        productListLabel.visibleProperty().bindBidirectional(productListLabelVisibleProperty);
        productId.setCellValueFactory(new PropertyValueFactory("id"));
        productTitle.setCellValueFactory(new PropertyValueFactory("title"));
        //productCompany.setCellValueFactory(new PropertyValueFactory("companyName"));
        productCompany.setCellValueFactory(data -> (data.getValue().getBranchId() != null) ? (new SimpleStringProperty(data.getValue().getBranchId().getCompanyName())) :
                new SimpleStringProperty("") );
        addButtonToTable();
        getProducts();
    }

    public void clickSearchButton(ActionEvent actionEvent) {

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
        TableColumn<Product, Void> buttonColumn = new TableColumn("Action");
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<TableColumn<Product, Void>, TableCell<Product, Void>>() {
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
