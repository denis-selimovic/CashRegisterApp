package ba.unsa.etf.si.utility.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

import java.io.IOException;

public class CustomFXMLLoader<T> {

    public Parent root;
    public T controller;

    public CustomFXMLLoader(String fxml) {
        initialize(JavaFXUtils.getFXMLLoader(fxml));
    }

    public CustomFXMLLoader(String fxml, Callback<Class<?>, Object> controllerFactory) {
        initialize(JavaFXUtils.getFXMLLoader(fxml, controllerFactory));
    }

    private void initialize(FXMLLoader loader) {
        try {
            root = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
