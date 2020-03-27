package ba.unsa.etf.si.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MyCashRegisterController {

    public TableColumn<Receipt,SimpleStringProperty> productID;
    public TableColumn<Receipt,SimpleStringProperty> productName;
    public TableColumn<Receipt,SimpleStringProperty> productPrice;
    public TableColumn<Receipt,SimpleStringProperty> productQuantity;
    public TableColumn<Receipt,SimpleStringProperty> productDiscount;
    public TableColumn<Receipt,SimpleStringProperty> total;
    public TableView<Receipt> receiptTable;


    private ObservableList<Receipt> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
            Receipt one = new Receipt("1","Nescafe", "2.10","1","0","2.10");
            Receipt two = new Receipt("2","7Days", "1.40","1","0","1.40");
            data.add(one);
            data.add(two);
            receiptTable.setItems(data);
    }


    //privremena klasa, potrebno ju je definirati u modelu
    public static class Receipt {

        private String id;
        private String name;
        private String price;
        private String quantity;
        private String discount;
        private String total;

        public Receipt(String id, String name, String price, String quantity, String discount, String total) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.discount = discount;
            this.total = total;
        }

    }

}
