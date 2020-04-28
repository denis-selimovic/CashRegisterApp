package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.DisabledDateCell;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;

public class DisabledDateCellFactory implements Callback<DatePicker, DateCell> {
    @Override
    public DateCell call(DatePicker datePicker) {
        return new DisabledDateCell();
    }
}
