package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.ReceiptItem;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ArchiveReceiptController {
    @FXML private TableColumn<ReceiptItem, String> prodName;
    @FXML private TableColumn<ReceiptItem, String> prodPrice;
    @FXML private TableColumn<ReceiptItem, String> prodQuantity;
    @FXML private TableColumn<ReceiptItem, String> prodDiscount;
    @FXML private TableColumn<ReceiptItem, String> totalPrice;
    @FXML private TableView<ReceiptItem> archiveReceiptTable;
    @FXML private Label price;
    @FXML private JFXButton abort;

    public void setSelected(Receipt selected) {
        this.selected = selected;
    }

    private Receipt selected;

    @FXML
    public void initialize(){
        selected = DialogController.getSelected();
        archiveReceiptTable.setItems(FXCollections.observableArrayList(selected.getReceiptItems()));
        prodName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        prodPrice.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(getPrice(cellData.getValue().getPrice()))));
        prodDiscount.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getDiscount())));
        prodQuantity.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getQuantity())));
        totalPrice.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getTotalPrice())));
        abort.setOnAction(e -> ((Stage) abort.getScene().getWindow()).close());
        price.setText(getTotalPriceAsString());
    }

    private double getPrice(double price) {
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private double getTotalPrice() {
        return selected.getReceiptItems().stream().mapToDouble(ReceiptItem::getTotalPrice).sum();
    }

    private String getTotalPriceAsString() {
        return BigDecimal.valueOf(getTotalPrice()).setScale(2, RoundingMode.HALF_UP).toString();
    }

}
