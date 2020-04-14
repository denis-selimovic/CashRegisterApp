package ba.unsa.etf.si.utility;

import ba.unsa.etf.si.App;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Optional;

public class JavaFXUtils {

    private JavaFXUtils() {}

    public static FXMLLoader getFXMLLoader(String fxml) {
        return new FXMLLoader(App.class.getResource(fxml));
    }

    public static Rectangle2D getScreenBounds() {
        return Screen.getPrimary().getBounds();
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
        Rectangle2D primScreenBounds = getScreenBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public static void setStage(Stage stage, String title, boolean resizable, StageStyle stageStyle, Modality modality) {
        stage.setTitle(title);
        stage.setResizable(resizable);
        stage.initStyle(stageStyle);
        stage.initModality(modality);
    }

    public static void setStageDimensions(Stage stage) {
        Rectangle2D bounds = getScreenBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }

    public static Optional<ButtonType> showAlert(String title, String header, Alert.AlertType alertType, ButtonType... buttonTypes) {
        Alert alert = new Alert(alertType, "", buttonTypes);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.getDialogPane().getStylesheets().add(App.class.getResource("css/alert.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        return alert.showAndWait();
    }
}
