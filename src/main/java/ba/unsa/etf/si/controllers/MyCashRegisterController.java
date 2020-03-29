package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.Branch;
import ba.unsa.etf.si.models.Product;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyCashRegisterController implements Initializable {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
