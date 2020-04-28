package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.ArchivedReceiptCell;
import ba.unsa.etf.si.models.Receipt;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ArchivedReceiptCellFactory implements Callback<ListView<Receipt>, ListCell<Receipt>> {

    @Override
    public ListCell<Receipt> call(ListView<Receipt> receiptListView) {
        return new ArchivedReceiptCell();
    }
}
