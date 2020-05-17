package ba.unsa.etf.si.notifications.topics;

import ba.unsa.etf.si.controllers.InventoryController;
import ba.unsa.etf.si.notifications.models.InventoryNotification;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.NotificationUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class InventoryNotificationTopic implements Topic {

    private final Consumer<Object> action = payload -> {
        String inventory = (String) payload;
        InventoryNotification notification = new InventoryNotification(inventory);
        Platform.runLater(() -> NotificationUtils.showInformation(Pos.BASELINE_RIGHT, "Inventory notification", "New products delivered!", 15, e -> showInventory(notification)));
    };

    private void showInventory(InventoryNotification inventoryNotification) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLUtils.loadCustomController("fxml/inventories.fxml", c -> new InventoryController(inventoryNotification))));
            StageUtils.setStage(stage, "", false, StageStyle.UNDECORATED, null);
            stage.showAndWait();
        });
    }

    @Override
    public Type getType() {
        return String.class;
    }

    @Override
    public String getTopic() {
        return "/topic/inventory_update";
    }

    @Override
    public Consumer<Object> getAction() {
        return action;
    }
}
