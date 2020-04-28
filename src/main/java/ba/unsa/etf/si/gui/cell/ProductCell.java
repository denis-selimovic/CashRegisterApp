package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import java.io.IOException;
import java.util.function.Consumer;

public class ProductCell extends ListCell<Product> {

    @FXML private Label productID, name;
    @FXML private JFXButton addBtn;

    private final Consumer<Product> action;

    public ProductCell(Consumer<Product> action) {
        this.action = action;
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
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
