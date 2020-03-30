package ba.unsa.etf.si.controllers;
import ba.unsa.etf.si.models.Branch;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MyCashRegisterController {


    public TextField myCashRegisterSearchInput = new TextField();

    public TableColumn productName;
    public TableColumn productPrice;
    public TableColumn productQuantity;
    public Button add;
    public TableColumn productDiscount;
    public TableColumn total;
    public TableView receiptTable;
    public TextField totalAmount;


    public static ObservableList<Receipt> data = FXCollections.observableArrayList();

    public Label productListLabel;
    public TableView<Product> productsTable;
    public TableColumn productId;
    public TableColumn productTitle;
    public TableColumn productImage;
    public TableColumn<Product,String> productCompany;

    private SimpleBooleanProperty productListLabelVisibleProperty = new SimpleBooleanProperty(true);
    private SimpleListProperty<Product> productSimpleListProperty = new SimpleListProperty<>();

    List<Product> getTestData() {
        List<Product> productList = new ArrayList<Product>();
        /*
        productList.add(new Product(1, "Ime 1") );
        productList.add(new Product(2, "Ime 2") );
        productList.add(new Product(3, "Ime 3") );
        productList.add(new Product(4, "Ime 4") );
        productList.add(new Product(5, "Ime 5") );
        productList.add(new Product(11,
                "Ime 11",
                new Branch(1, "Kompanija 2")));


*/
        return productList;
    }

    @FXML
    public void initialize() {
        productName.setCellValueFactory(new PropertyValueFactory<Receipt, String>("name"));
        productPrice.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("price"));
        productDiscount.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("discount"));
        productQuantity.setCellValueFactory(new PropertyValueFactory<Receipt, TextField>("quantity"));
        total.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("totalPrice"));
       totalAmount.setText("0.0");

        data.addListener(new ListChangeListener<Receipt>(){

            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Receipt> pChange) {
                    receiptTable.refresh();
                    calculateTotalAmount();

            }
        });

        data.add(new Receipt("nescafe", 2.30, 0.0));
        data.add(new Receipt("7Days", 1.30,0.0));
        addRemoveButtonToTable();
        receiptTable.setItems(data);

        productListLabel.visibleProperty().bindBidirectional(productListLabelVisibleProperty);
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

    public int find(Receipt newReceipt){
        for(int i=0;i<data.size();i++){
            if(data.get(i).getName().equals(newReceipt.getName()) && data.get(i).getPrice().equals(newReceipt.getPrice()))return i;
        }
        return -1;
    }

    public void addNewRow(String name, Double price, Double discount){
        if(find(new Receipt(name, price, discount))==-1){
            data.add(new Receipt(name, price, discount));
        }
        else{
            String quant = data.get(find(new Receipt(name, price, discount))).getQuantity().getText();
            Integer quantInt= Integer.parseInt(quant)+1;
            data.get(find(new Receipt(name, price, discount))).getQuantity().setText(quantInt.toString());
            receiptTable.refresh();

        }

    }

    public void calculateTotalAmount(){
        Double amount= new Double(0.0);
        for (Receipt recept:data) {
            amount+=recept.getTotalPrice();
        }
        totalAmount.setText(amount.toString());
    }

    public void clickSearchButton(ActionEvent actionEvent) {

        addNewRow("Voda", 2.30, 0.0);
    }


    private void addRemoveButtonToTable() { TableColumn<Receipt, Void> colBtn = new TableColumn("Remove");

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
                            calculateTotalAmount();
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
}
