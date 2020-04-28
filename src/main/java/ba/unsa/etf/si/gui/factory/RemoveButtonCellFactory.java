package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.RemoveButtonCell;
import ba.unsa.etf.si.models.Product;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.function.Consumer;

public class RemoveButtonCellFactory implements Callback<TableColumn<Product, Void>, TableCell<Product, Void>> {

    private final Consumer<Product> eventHandler;

    public RemoveButtonCellFactory(Consumer<Product> eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public TableCell<Product, Void> call(TableColumn<Product, Void> productVoidTableColumn) {
        return new RemoveButtonCell(eventHandler);
    }
}
