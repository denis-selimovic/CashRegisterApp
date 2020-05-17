package ba.unsa.etf.si.utility.modelutils;

import ba.unsa.etf.si.models.Inventory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InventoryUtils {

    public static ArrayList<Inventory> getInventoryListFromJSON(JSONArray json) {
        ArrayList<Inventory> inventory = new ArrayList<>();
        for (int i = 0; i < json.length(); ++i) inventory.add(getInventoryListFromJSON(json.getJSONObject(i)));
        return inventory;
    }

    private static Inventory getInventoryListFromJSON(JSONObject json) {
        return new Inventory(json.getString("productName"), json.getDouble("productQuantity"));
    }
}
