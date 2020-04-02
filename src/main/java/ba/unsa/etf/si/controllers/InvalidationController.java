package ba.unsa.etf.si.controllers;


import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InvalidationController {


    @FXML private TextField searchField;
    @FXML private JFXListView<Receipt> receiptList;


    @FXML
    public void initialize() {
        receiptList.setCellFactory(new ReceiptCellFactory());
        receiptList.getItems().add(new Receipt(LocalDateTime.now(), "Denis", 20.0, 1L));
    }

    public static class ReceiptCell extends ListCell<Receipt>{

        @FXML private Label receiptID, date, cashier, amount;

        public ReceiptCell() {
            loadFXML();
        }

        private void loadFXML() {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/invalidation.fxml"));
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
                receiptID.setText(Long.toString(receipt.getServerID()));
                date.setText(receipt.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm")));
                cashier.setText(receipt.getCashier());
                amount.setText(Double.toString(receipt.getAmount()));
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    private static class ReceiptCellFactory implements javafx.util.Callback<javafx.scene.control.ListView<Receipt>, javafx.scene.control.ListCell<Receipt>> {
        @Override
        public ListCell<Receipt> call(ListView<Receipt> receiptListView) {
            return new ReceiptCell();
        }
    }
}
