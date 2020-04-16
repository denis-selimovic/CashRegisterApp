package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.ProductCell;
import ba.unsa.etf.si.models.Product;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import java.util.function.Consumer;

public class ProductCellFactory implements Callback<ListView<Product>, ListCell<Product>> {

    private final Consumer<Product> action;

    public ProductCellFactory(Consumer<Product> action) {
        this.action = action;
    }

    @Override
    public ListCell<Product> call(ListView<Product> productListView) {
        return new ProductCell(action);
    }
}
