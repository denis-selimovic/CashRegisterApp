package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.function.Consumer;

public class ProductCell extends ListCell<Product> {

    @FXML private Label productID, name;
    @FXML private JFXButton addBtn, plus, minus;

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
        plus.setVisible(!visible);
        minus.setVisible(!visible);
    }

    @Override
    public void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);
        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            productID.setText(Long.toString(product.getServerID()));
            name.setText(product.getName());
            addBtn.setTooltip(new Tooltip("Add to cart"));
            addBtn.setOnAction(e -> action.accept(product));
            plus.setTooltip(new Tooltip("Increase quantity"));
            plus.setOnAction(e -> plusAct.accept(product));
            minus.setTooltip(new Tooltip("Decrease quantity"));
            minus.setOnAction(e -> minusAct.accept(product));
            //Platform.runLater(() -> refreshListView(product));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
