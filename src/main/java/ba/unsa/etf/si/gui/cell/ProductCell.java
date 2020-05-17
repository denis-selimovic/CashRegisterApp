package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.Consumer;

public class ProductCell extends ListCell<Product> {

    @FXML
    private Label productID, name;
    @FXML
    private JFXButton addBtn, plus, minus;
    @FXML
    private HBox hbox;

    private final Consumer<Product> action, plusAct, minusAct;

    public ProductCell(Consumer<Product> action, Consumer<Product> plusAct, Consumer<Product> minusAct) {
        this.action = action;
        this.plusAct = plusAct;
        this.minusAct = minusAct;
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader loader = FXMLUtils.getFXMLLoader("fxml/product.fxml");
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshListView(Product product) {
        boolean visible = product.getTotal() == 0;
        addBtn.setVisible(visible);
        hbox.setVisible(!visible);
    }

    private void commit(Product product) {
        getListView().fireEvent(new ListView.EditEvent<>(getListView(), ListView.editCommitEvent(), product, indexProperty().get()));
    }

    private void initializeButton(JFXButton button, EventHandler<ActionEvent> handler, Tooltip tooltip) {
        button.setTooltip(tooltip);
        button.setOnAction(handler);
    }

    private EventHandler<ActionEvent> initializeEventHandler(Product product, Consumer<Product> consumer) {
        return e -> {
            consumer.accept(product);
            Platform.runLater(() -> commit(product));
        };
    }

    @Override
    public void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);
        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            productID.setText(Long.toString(product.getServerID()));
            name.setText(product.getName());
            initializeButton(addBtn, initializeEventHandler(product, action), new Tooltip("Add to cart"));
            initializeButton(plus, initializeEventHandler(product, plusAct), new Tooltip("Increase quantity"));
            initializeButton(minus, initializeEventHandler(product, minusAct), new Tooltip("Decrease quantity"));
            Platform.runLater(() -> refreshListView(product));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
