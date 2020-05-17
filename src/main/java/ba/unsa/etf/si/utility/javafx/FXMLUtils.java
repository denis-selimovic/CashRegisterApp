package ba.unsa.etf.si.utility.javafx;

import ba.unsa.etf.si.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

import java.io.IOException;

public class FXMLUtils {

    private FXMLUtils() {
    }

    private static Parent load(FXMLLoader loader) {
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static FXMLLoader getFXMLLoader(String fxml) {
        return new FXMLLoader(App.class.getResource(fxml));
    }

    public static FXMLLoader getFXMLLoader(String fxml, Callback<Class<?>, Object> callback) {
        FXMLLoader fxmlLoader = getFXMLLoader(fxml);
        fxmlLoader.setControllerFactory(callback);
        return fxmlLoader;
    }

    public static FXMLLoader getFXMLLoader(String fxml, Object controller) {
        FXMLLoader fxmlLoader = getFXMLLoader(fxml);
        fxmlLoader.setController(controller);
        return fxmlLoader;
    }

    public static Parent loadController(String fxml) {
        return load(getFXMLLoader(fxml));
    }

    public static Parent loadCustomController(String fxml, Callback<Class<?>, Object> controllerFactory) {
        return load(getFXMLLoader(fxml, controllerFactory));
    }

    public static Parent loadCustomController(String fxml, Object controller) {
        return load(getFXMLLoader(fxml, controller));
    }

    public static <T> CustomFXMLLoader<T> getCustomLoader(String fxml, Class<T> tClass) {
        return new CustomFXMLLoader<>(fxml);
    }

    public static <T> CustomFXMLLoader<T> getCustomLoader(String fxml, Callback<Class<?>, Object> controllerFactory) {
        return new CustomFXMLLoader<>(fxml, controllerFactory);
    }
}
