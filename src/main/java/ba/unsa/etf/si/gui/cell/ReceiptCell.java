package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ReceiptCell extends ListCell<Receipt> {

    @FXML private Label receiptID, date, cashier, amount;

    public ReceiptCell() {
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader loader = FXMLUtils.getFXMLLoader("fxml/invalidation.fxml");
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateItem(Receipt receipt, boolean empty) {
        super.updateItem(receipt, empty);
        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            receiptID.setText(receipt.getTimestampID().split("-")[3]);
            date.setText(receipt.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            cashier.setText(receipt.getCashier());
            amount.setText(String.format("%.2f", receipt.getAmount()));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
