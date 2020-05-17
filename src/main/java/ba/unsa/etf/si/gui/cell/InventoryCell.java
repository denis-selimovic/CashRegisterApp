package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.models.Inventory;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class InventoryCell extends ListCell<Inventory> {

    @FXML
    private Label productName, productQuantity;

    public InventoryCell() {
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader loader = FXMLUtils.getFXMLLoader("fxml/inventory.fxml");
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateItem(Inventory item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            productName.setText(item.getProductName());
            productQuantity.setText("Amount: " + (int) item.getProductQuantity());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
