package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Order;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrdersController {

    @FXML
    private JFXButton addBtn;
    @FXML
    private GridView<Order> grid;

    @FXML
    public void initialize() {
        grid.setCellFactory(new OrderCellFactory());
        grid.setHorizontalCellSpacing(20);
        grid.setVerticalCellSpacing(20);
        grid.setCellHeight(280);
        grid.setCellWidth(350);
        grid.getItems().addAll(new Order(1L, "Denis", LocalDateTime.now()), new Order(2L, "Selimovic", LocalDateTime.now()));
        grid.getItems().addAll(new Order(1L, "Denis", LocalDateTime.now()), new Order(2L, "Selimovic", LocalDateTime.now()));
        grid.getItems().addAll(new Order(1L, "Denis", LocalDateTime.now()), new Order(2L, "Selimovic", LocalDateTime.now()));
        grid.getItems().addAll(new Order(1L, "Denis", LocalDateTime.now()), new Order(2L, "Selimovic", LocalDateTime.now()));
        grid.getItems().addAll(new Order(1L, "Denis", LocalDateTime.now()), new Order(2L, "Selimovic", LocalDateTime.now()));
        grid.getItems().addAll(new Order(1L, "Denis", LocalDateTime.now()), new Order(2L, "Selimovic", LocalDateTime.now()));
    }

    public static class OrderCell extends GridCell<Order> {

        @FXML private Label orderID, bartenderName, date;

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

        @Override
        protected void updateItem(Order order, boolean empty) {
            super.updateItem(order, empty);
            if (empty) {
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            } else {
                orderID.setText(Long.toString(order.getId()));
                bartenderName.setText(order.getBartender());
                date.setText(order.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    public static class OrderCellFactory implements Callback<GridView<Order>, GridCell<Order>> {

        @Override
        public GridCell<Order> call(GridView<Order> orderGridView) {
            return new OrderCell();
        }
    }
}
