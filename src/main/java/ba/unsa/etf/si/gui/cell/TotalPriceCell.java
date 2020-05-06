package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.models.Product;
import javafx.scene.control.TableCell;

public class TotalPriceCell extends TableCell<Product, String> {

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            int current = indexProperty().getValue();
            Product p = getTableColumn().getTableView().getItems().get(current);
            setText(String.format("%.2f", p.getTotalPrice()));
        } else {
            setText(null);
        }
    }
}