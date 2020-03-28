package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.Receipt;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ReceiptController {


    @FXML private ListView<String> receiptList;

    @FXML
    public void initialize() {
        receiptList.getItems().addAll("Denis", "Denis2", "Denis3");
    }


    public static final class ReceiptCell extends ListCell<Receipt> {

    }

}
