package ba.unsa.etf.si.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ReceiptController {


    @FXML private ListView<String> receiptList;

    @FXML
    public void initialize() {
        receiptList.getItems().addAll("Denis", "Denis2", "Denis3");
    }
}
