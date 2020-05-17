package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.gui.factory.InventoryCellFactory;
import ba.unsa.etf.si.models.Inventory;
import ba.unsa.etf.si.notifications.models.InventoryNotification;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class InventoryController {

    @FXML
    private JFXButton closeBtn;
    @FXML
    private ListView<Inventory> inventory;

    private final InventoryNotification inventoryNotification;

    public InventoryController(InventoryNotification inventoryNotification) {
        this.inventoryNotification = inventoryNotification;
    }

    @FXML
    public void initialize() {
        inventory.setCellFactory(new InventoryCellFactory());
        inventory.setItems(FXCollections.observableList(inventoryNotification.getInventory()));
        closeBtn.setOnAction(e -> close());
    }

    private void close() {
        ((Stage) closeBtn.getScene().getWindow()).close();
    }
}
