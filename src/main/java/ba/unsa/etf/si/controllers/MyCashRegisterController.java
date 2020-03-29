package ba.unsa.etf.si.controllers;
<<<<<<< HEAD
import ba.unsa.etf.si.models.Branch;
=======
>>>>>>> ad76ae4890574e2b288b944ff4cfd9c8875643e7
import ba.unsa.etf.si.models.Product;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class MyCashRegisterController {

<<<<<<< HEAD
    public TextField myCashRegisterSearchInput = new TextField();

=======
>>>>>>> ad76ae4890574e2b288b944ff4cfd9c8875643e7
    public TableColumn productID;
    public TableColumn productName;
    public TableColumn productPrice;
    public TableColumn productQuantity;
    public TableColumn productDiscount;
    public TableColumn total;
    public TableView receiptTable;

<<<<<<< HEAD
=======
    public Label productListLabel;
>>>>>>> ad76ae4890574e2b288b944ff4cfd9c8875643e7
    public TableView<Product> productsTable;
    public TableColumn productId;
    public TableColumn productTitle;
    public TableColumn productImage;
    public TableColumn<Product,String> productCompany;
<<<<<<< HEAD
=======
    private SimpleBooleanProperty productListLabelVisibleProperty = new SimpleBooleanProperty(true);
>>>>>>> ad76ae4890574e2b288b944ff4cfd9c8875643e7
    private SimpleListProperty<Product> productSimpleListProperty = new SimpleListProperty<>();

    List<Product> getTestData() {
        List<Product> productList = new ArrayList<Product>();
        /*
<<<<<<< HEAD

=======
>>>>>>> ad76ae4890574e2b288b944ff4cfd9c8875643e7
        productList.add(new Product(1, "Ime 1") );
        productList.add(new Product(2, "Ime 2") );
        productList.add(new Product(3, "Ime 3") );
        productList.add(new Product(4, "Ime 4") );
        productList.add(new Product(5, "Ime 5") );
        productList.add(new Product(11,
                "Ime 11",
                new Branch(1, "Kompanija 2")));
<<<<<<< HEAD

*/
=======
        */

>>>>>>> ad76ae4890574e2b288b944ff4cfd9c8875643e7
        return productList;
    }

    public static ObservableList<Receipt> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        productID.setCellValueFactory(new PropertyValueFactory<Receipt, Integer>("id"));
        productName.setCellValueFactory(new PropertyValueFactory<Receipt, String>("name"));
        productPrice.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("price"));
        productDiscount.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("discount"));
        total.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("totalPrice"));



        data.add(new Receipt(1,"nescafe", 2.30, 0.0));
        data.add(new Receipt(2,"7Days", 1.30,0.0));
        addSpinner();
        addButtonToTable();
        receiptTable.setItems(data);

<<<<<<< HEAD
=======
        productListLabel.visibleProperty().bindBidirectional(productListLabelVisibleProperty);
>>>>>>> ad76ae4890574e2b288b944ff4cfd9c8875643e7
        productsTable.itemsProperty().bindBidirectional(productSimpleListProperty);
        List<Product> productList = getTestData();
        productId.setCellValueFactory(new PropertyValueFactory("id"));
        productImage.setCellValueFactory(new PropertyValueFactory("image"));
        productTitle.setCellValueFactory(new PropertyValueFactory("title"));
        //productCompany.setCellValueFactory(new PropertyValueFactory("companyName"));
        productCompany.setCellValueFactory(data -> (data.getValue().getBranchId() != null) ? (new SimpleStringProperty(data.getValue().getBranchId().getCompanyName())) :
                new SimpleStringProperty("") );
        addButtonToTable();
        productSimpleListProperty.setValue(FXCollections.observableList(productList));
    }

<<<<<<< HEAD
    public void clickSearchButton(ActionEvent actionEvent) {

    }

=======
>>>>>>> ad76ae4890574e2b288b944ff4cfd9c8875643e7

    private void addRemoveButtonToTable() {
        TableColumn<Receipt, Void> colBtn = new TableColumn("Remove");

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

        receiptTable.getColumns().add(colBtn);

    }

    private void addSpinner(){
        TableColumn<Receipt, Spinner> SpinnerCol = new TableColumn<Receipt, Spinner>("Quantity");

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
        receiptTable.getColumns().add(SpinnerCol);
    }

    public void addProducts(List<Product> productList) {
        productSimpleListProperty.setValue(FXCollections.observableList(productList));
    }

    /*
    public void btnClicked(ActionEvent actionEvent) {
        productListLabelVisibleProperty.set(false);
        addProductToTable(new Product(11,
                "Ime 11",
                new Branch(1, "Kompanija 2"))
        );
    }
    public void addProductToTable(Product p) {
        productSimpleListProperty.add(p);
        System.out.println(p.getBranchId().getCompanyName());
    }
    */

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

<<<<<<< HEAD

=======
>>>>>>> ad76ae4890574e2b288b944ff4cfd9c8875643e7
    //privremena klasa, potrebno ju je definirati u modelu
    public class Receipt {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty name;
        private final SimpleDoubleProperty price;
        private final SimpleDoubleProperty discount;
        private final SimpleDoubleProperty totalPrice;



        private Receipt(Integer id, String name, Double price, Double discount) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.price = new SimpleDoubleProperty(price);
            this.discount = new SimpleDoubleProperty(discount);
            this.totalPrice = new SimpleDoubleProperty(price);
        }

        public Integer getId() {
            return id.get();
        }


        public void setId(Integer id) {
            this.id.set(id);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public Double getPrice() {
            return price.get();
        }

        public void setPrice(Double price) {
            this.price.set(price);
        }

        public Double getDiscount() {
            return discount.get();
        }


        public void setDiscount(Double discount) {
            this.discount.set(discount);
        }

        public Double getTotalPrice() {
            return totalPrice.get();
        }


        public void setTotalPrice(Double totalPrice) {
            this.totalPrice.set(totalPrice);
        }
    }

}