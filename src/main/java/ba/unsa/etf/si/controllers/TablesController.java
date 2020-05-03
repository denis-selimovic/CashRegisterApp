package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.gui.factory.TableCellFactory;
import ba.unsa.etf.si.models.Table;
import javafx.fxml.FXML;
import org.controlsfx.control.GridView;

public class TablesController {


    @FXML private GridView<Table> grid;

    @FXML
    public void initialize() {
        grid.setCellFactory(new TableCellFactory());
    }
}
