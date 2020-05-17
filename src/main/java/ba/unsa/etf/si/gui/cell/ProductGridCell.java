package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Product;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import org.controlsfx.control.GridCell;

import java.io.IOException;
import java.util.function.Consumer;

public class ProductGridCell extends GridCell<Product> {

    @FXML
    private JFXButton addBtn;
    @FXML
    private Label price, name;

    private final Consumer<Product> add;

    public ProductGridCell(Consumer<Product> add) {
        this.add = add;
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/productGrid.fxml"));
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
        } else {
            name.setText(product.getName());
            price.setText(String.format("%.2f", product.getPriceAfterDiscount()));
            addBtn.setOnAction(e -> add.accept(product));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
