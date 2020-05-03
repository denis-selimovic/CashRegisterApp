package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.models.Table;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import org.controlsfx.control.GridCell;

import java.io.IOException;

public class TableCell extends GridCell<Table> {

    @FXML private Label tableNum;

    public TableCell() {
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader loader = FXMLUtils.getFXMLLoader("fxml/table.fxml");
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateItem(Table item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            tableNum.setText(Long.toString(item.getTableNumber()));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
