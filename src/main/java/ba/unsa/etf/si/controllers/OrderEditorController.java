package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.Order;
import ba.unsa.etf.si.models.Product;
import javafx.fxml.FXML;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

public class OrderEditorController {

    @FXML
    private GridView<Product> productsGrid;

    private Order order;

    public OrderEditorController(Order order) {
        this.order = order;
    }

    @FXML
    public void initialize() {

    }

    public class ProductGridCell extends GridCell<Product> {


    }
}
