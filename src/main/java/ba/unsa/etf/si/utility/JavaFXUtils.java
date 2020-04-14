package ba.unsa.etf.si.utility;

import ba.unsa.etf.si.App;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class JavaFXUtils {

    private JavaFXUtils() {}

    public static FXMLLoader getFXMLLoader(String fxml) {
        return new FXMLLoader(App.class.getResource(fxml));
    }

    public static Parent loadController(String fxml) throws IOException {
        return getFXMLLoader(fxml).load();
    }

    public static Parent loadCustomController(String fxml, Callback<Class<?>, Object> controllerFactory) throws IOException {
        FXMLLoader loader = getFXMLLoader(fxml);
        loader.setControllerFactory(controllerFactory);
        return loader.load();
    }


    public static void centerStage(Stage stage, int width, int height) {
        stage.setWidth(width);
        stage.setHeight(height);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }
}
