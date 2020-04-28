package ba.unsa.etf.si.utility.javafx;

import ba.unsa.etf.si.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;
import java.io.IOException;

public class FXMLUtils {

    private FXMLUtils() {}

    public static FXMLLoader getFXMLLoader(String fxml) {
        return new FXMLLoader(App.class.getResource(fxml));
    }

    public static FXMLLoader getFXMLLoader(String fxml, Callback<Class<?>, Object> callback) {
        FXMLLoader fxmlLoader = getFXMLLoader(fxml);
        fxmlLoader.setControllerFactory(callback);
        return fxmlLoader;
    }

    public static Parent loadController(String fxml) throws IOException {
        return getFXMLLoader(fxml).load();
    }

    public static Parent loadCustomController(String fxml, Callback<Class<?>, Object> controllerFactory) throws IOException {
        return getFXMLLoader(fxml, controllerFactory).load();
    }

    public static <T> CustomFXMLLoader<T> getCustomLoader(String fxml, Class<T> tClass) {
        return new CustomFXMLLoader<>(fxml);
    }

    public static <T> CustomFXMLLoader<T> getCustomLoader(String fxml, Callback<Class<?>, Object> controllerFactory) {
        return new CustomFXMLLoader<>(fxml, controllerFactory);
    }
}
