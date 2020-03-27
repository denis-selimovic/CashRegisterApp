package ba.unsa.etf.si;

import ba.unsa.etf.si.controllers.PrimaryController;
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
        //scene = new Scene(loadFXML("fxml/primary.fxml"), 640, 480);
        FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/primary.fxml"));
        loader.setController(new PrimaryController(PrimaryController.Role.CHIEF));
        scene = new Scene(loader.load());
        setStage(stage);
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

    private static void setStage(Stage stage) {
        Rectangle2D rect = getScreenSize();
        stage.setWidth(rect.getWidth());
        stage.setHeight(rect.getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch();
    }

}