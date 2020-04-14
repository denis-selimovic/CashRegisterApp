package ba.unsa.etf.si.utility.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class CustomFXMLLoader<T> {

    public Parent root;
    public T controller;

    public CustomFXMLLoader(String fxml) {
        FXMLLoader loader = JavaFXUtils.getFXMLLoader(fxml);
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = loader.getController();
    }
}
