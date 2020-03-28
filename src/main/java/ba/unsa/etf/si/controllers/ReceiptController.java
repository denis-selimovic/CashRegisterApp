package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class ReceiptController {


    @FXML private ListView<String> receiptList;

    @FXML
    public void initialize() {
        receiptList.getItems().addAll("Denis", "Denis2", "Denis3");
    }


    public static final class ReceiptCell extends ListCell<Receipt> {

        @FXML private Label receiptID, cashier, date;

        public ReceiptCell() {
            loadFXML();
        }

        private void loadFXML() {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/receipt.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void updateItem(Receipt receipt, boolean empty) {
            super.updateItem(receipt, empty);
            if(empty) {
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
            else {
                receiptID.setText(Long.toString(receipt.getId()));
                cashier.setText(receipt.getCashier());
                date.setText(new SimpleDateFormat("dd/mm/yyyy").format(receipt.getDate()));
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

}
