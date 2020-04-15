package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.TotalPriceCell;
import ba.unsa.etf.si.models.Product;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class TotalPriceCellFactory implements Callback<TableColumn<Product, String>, TableCell<Product, String>> {
    @Override
    public TableCell<Product, String> call(TableColumn<Product, String> productStringTableColumn) {
        return new TotalPriceCell();
    }
}
