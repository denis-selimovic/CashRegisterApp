package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.EditingCell;
import ba.unsa.etf.si.models.Product;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.function.Consumer;

public class EditingCellFactory implements Callback<TableColumn<Product, String>, TableCell<Product, String>> {

    private final Consumer<Product> action;

    public EditingCellFactory(Consumer<Product> action) {
        this.action = action;
    }

    @Override
    public TableCell<Product, String> call(TableColumn<Product, String> productStringTableColumn) {
        return new EditingCell(action);
    }
}
