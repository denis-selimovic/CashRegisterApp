package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.gui.factory.TableCellFactory;
import ba.unsa.etf.si.models.Table;
import ba.unsa.etf.si.routes.TableRoutes;
import ba.unsa.etf.si.utility.modelutils.TableUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import org.controlsfx.control.GridView;
import org.json.JSONArray;

import java.util.function.Consumer;

public class TablesController {

    @FXML private GridView<Table> grid;

    private final Consumer<String> getTablesCallback = response -> {
        Platform.runLater(() -> grid.setItems(TableUtils.getTablesFromJSON(new JSONArray(response))));
    };

    @FXML
    public void initialize() {
        grid.setCellFactory(new TableCellFactory());
        grid.setHorizontalCellSpacing(30);
        grid.setVerticalCellSpacing(30);
        grid.setCellHeight(250);
        grid.setCellWidth(350);
        TableRoutes.getTables(getTablesCallback, () -> System.out.println("Could not fetch tables!"));
    }
}
