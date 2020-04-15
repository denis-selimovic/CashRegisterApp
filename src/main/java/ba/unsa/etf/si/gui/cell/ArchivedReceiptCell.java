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

public class ArchivedReceiptCell extends ListCell<Receipt> {

    @FXML private Label receiptID, cashier, date;

    public ArchivedReceiptCell() {
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader loader = FXMLUtils.getFXMLLoader("fxml/receipt.fxml");
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
            receiptID.setText(Long.toString(receipt.getId()));
            cashier.setText(receipt.getCashier());
            date.setText(receipt.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
