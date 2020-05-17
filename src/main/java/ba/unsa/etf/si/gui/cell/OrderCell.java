package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.models.Order;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import org.controlsfx.control.GridCell;

import java.io.IOException;
import java.util.function.Consumer;

public class OrderCell extends GridCell<Order> {

    @FXML
    private Label orderID, bartenderName, date;
    @FXML
    private JFXButton payBtn, addToOrderBtn, deleteOrderBtn;

    private final Consumer<Order> pay, edit, remove;

    public OrderCell(Consumer<Order> pay, Consumer<Order> edit, Consumer<Order> remove) {
        this.pay = pay;
        this.edit = edit;
        this.remove = remove;
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader loader = FXMLUtils.getFXMLLoader("fxml/order.fxml");
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateItem(Order order, boolean empty) {
        super.updateItem(order, empty);
        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            orderID.setText(Long.toString(order.getServerID()));
            bartenderName.setText(Double.toString(order.getTotalAmount()));
            //date.setText(order.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            payBtn.setOnAction(e -> pay.accept(order));
            addToOrderBtn.setOnAction(e -> edit.accept(order));
            deleteOrderBtn.setOnAction(e -> remove.accept(order));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
