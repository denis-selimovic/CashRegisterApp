package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.ReceiptCell;
import ba.unsa.etf.si.models.Receipt;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ReceiptCellFactory implements Callback<javafx.scene.control.ListView<Receipt>, ListCell<Receipt>> {

    @Override
    public ListCell<Receipt> call(ListView<Receipt> receiptListView) {
        return new ReceiptCell();
    }
}
