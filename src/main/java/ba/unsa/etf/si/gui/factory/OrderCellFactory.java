package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.OrderCell;
import ba.unsa.etf.si.models.Order;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.util.function.Consumer;

public class OrderCellFactory implements Callback<GridView<Order>, GridCell<Order>> {

    private final Consumer<Order> pay, edit, remove;

    public OrderCellFactory(Consumer<Order> pay, Consumer<Order> edit, Consumer<Order> remove) {
        this.pay = pay;
        this.edit = edit;
        this.remove = remove;
    }

    @Override
    public GridCell<Order> call(GridView<Order> orderGridView) {
        return new OrderCell(pay, edit, remove);
    }
}
