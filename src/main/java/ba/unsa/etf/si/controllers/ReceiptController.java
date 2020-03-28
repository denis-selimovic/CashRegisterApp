package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import com.jfoenix.controls.JFXDatePicker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiptController {


    public JFXDatePicker datePicker;
    @FXML private ListView<Receipt> receiptList;

    @FXML
    public void initialize() {
        receiptList.setCellFactory(new ReceiptCellFactory());
        receiptList.getItems().addAll(new Receipt(123L, new Date(), "Neko Nekić", 21.31),
                new Receipt(124L, new Date(), "Oki Okić", 107.32));
    }


    public static final class ReceiptCell extends ListCell<Receipt> {

        @FXML private Label receiptID, cashier, date;

        public ReceiptCell() {
            loadFXML();
        }

        private void loadFXML() {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/receipt.fxml"));
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
                date.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(receipt.getDate()));
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    public static class ReceiptCellFactory implements Callback<ListView<Receipt>, ListCell<Receipt>> {

        @Override
        public ListCell<Receipt> call(ListView<Receipt> param) {
            return new ReceiptCell();
        }
    }

}
