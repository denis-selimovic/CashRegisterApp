package ba.unsa.etf.si;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("fxml/primary.fxml"), 640, 480);
        Rectangle2D rect = getScreenSize();
        stage.setWidth(rect.getWidth());
        stage.setHeight(rect.getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        return fxmlLoader.load();
    }

    public static Rectangle2D getScreenSize() {
        Screen screen = Screen.getPrimary();
        return screen.getBounds();
    }

    public static void main(String[] args) {
        launch();
    }

}