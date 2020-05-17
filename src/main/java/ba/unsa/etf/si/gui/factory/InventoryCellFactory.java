package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.InventoryCell;
import ba.unsa.etf.si.models.Inventory;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class InventoryCellFactory implements Callback<ListView<Inventory>, ListCell<Inventory>> {

    @Override
    public ListCell<Inventory> call(ListView<Inventory> inventoryListView) {
        return new InventoryCell();
    }
}
