package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.Order;
import javafx.fxml.FXML;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

public class OrdersController {

    @FXML
    private GridView grid;

    @FXML
    public void initialize() {

    }

    public static class OrderCell extends GridCell<Order> {

    }
}
