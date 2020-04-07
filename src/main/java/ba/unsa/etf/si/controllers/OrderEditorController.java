package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.Order;
import javafx.fxml.FXML;

public class OrderEditorController {

    private Order order;

    public OrderEditorController(Order order) {
        this.order = order;
    }

    @FXML
    public void initialize() {

    }
}
