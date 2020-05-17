package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.models.Product;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageCell extends TableCell<Product, Image> {

    final ImageView imageView = new ImageView();

    public ImageCell() {
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(115);
        imageView.setFitWidth(115);
    }

    @Override
    public void updateItem(Image item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            imageView.setImage(item);
            setGraphic(imageView);
        } else setGraphic(null);
    }
}
