package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.ProductCell;
import ba.unsa.etf.si.models.Product;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import java.util.function.Consumer;

public class ProductCellFactory implements Callback<ListView<Product>, ListCell<Product>> {

    private final Consumer<Product> action, plus, minus;

    public ProductCellFactory(Consumer<Product> action, Consumer<Product> plus, Consumer<Product> minus) {
        this.action = action;
        this.plus = plus;
        this.minus = minus;
    }

    @Override
    public ListCell<Product> call(ListView<Product> productListView) {
        return new ProductCell(action, plus, minus);
    }
}
