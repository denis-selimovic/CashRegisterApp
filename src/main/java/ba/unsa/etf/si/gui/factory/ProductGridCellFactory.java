package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.ProductGridCell;
import ba.unsa.etf.si.models.Product;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.util.function.Consumer;

public class ProductGridCellFactory implements Callback<GridView<Product>, GridCell<Product>> {

    private final Consumer<Product> action;

    public ProductGridCellFactory(Consumer<Product> action) {
        this.action = action;
    }

    @Override
    public GridCell<Product> call(GridView<Product> productGridView) {
        return new ProductGridCell(action);
    }
}
