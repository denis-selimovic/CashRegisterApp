package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Order;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.io.IOException;

public class OrdersController {

    @FXML
    private GridView grid;

    @FXML
    public void initialize() {

    }

    public static class OrderCell extends GridCell<Order> {

        public OrderCell() {
            loadFXML();
        }

        private void loadFXML() {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/order.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
