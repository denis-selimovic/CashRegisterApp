package ba.unsa.etf.si.gui.factory;

import ba.unsa.etf.si.gui.cell.ImageCell;
import ba.unsa.etf.si.models.Product;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.util.Callback;


public class ImageCellFactory implements Callback<TableColumn<Product, Image>, TableCell<Product, Image>> {

    public ImageCellFactory() {
    }

    @Override
    public TableCell<Product, Image> call(TableColumn<Product, Image> productImageTableColumn) {
        return new ImageCell();
    }
}
