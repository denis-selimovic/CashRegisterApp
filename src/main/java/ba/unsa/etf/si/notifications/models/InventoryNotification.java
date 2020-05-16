package ba.unsa.etf.si.notifications.models;

import ba.unsa.etf.si.models.Inventory;
import ba.unsa.etf.si.utility.modelutils.InventoryUtils;
import lombok.Data;
import org.json.JSONArray;

import java.util.ArrayList;

@Data
public class InventoryNotification {

    private final ArrayList<Inventory> inventory;

    public InventoryNotification(String inventory) {
        this.inventory = InventoryUtils.getInventoryListFromJSON(new JSONArray(inventory));
    }
}
