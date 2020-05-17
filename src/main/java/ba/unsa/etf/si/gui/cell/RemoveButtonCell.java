package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Product;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.function.Consumer;

public class RemoveButtonCell extends TableCell<Product, Void> {

    private final Button removeBtn = new Button();
    private final Consumer<Product> action;

    public RemoveButtonCell(Consumer<Product> action) {
        this.action = action;
        removeBtn.getStyleClass().add("btn");
        removeBtn.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("img/cancel.png"))));
    }

    @Override
    public void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        removeBtn.setOnAction(e -> action.accept(getTableColumn().getTableView().getItems().get(indexProperty().get())));
        if (empty) setGraphic(null);
        else setGraphic(removeBtn);
    }
}
