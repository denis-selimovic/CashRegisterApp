package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.TableCell;
import ba.unsa.etf.si.models.Table;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

public class TableCellFactory implements Callback<GridView<Table>, GridCell<Table>> {
    @Override
    public GridCell<Table> call(GridView<Table> tableGridView) {
        return new TableCell();
    }
}
