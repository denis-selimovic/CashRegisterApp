package ba.unsa.etf.si;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        scene = new Scene(loadFXML("fxml/loginForm.fxml"), 800, 500);
        //Rectangle2D rect = getScreenSize();
        //stage.setWidth(rect.getWidth());
        //stage.setHeight(rect.getHeight());
        stage.setScene(scene);
        stage.setTitle("Cash Register App");
        stage.getIcons().add(new Image("/ba/unsa/etf/si/img/appIcon.png"));
        //stage.setMaximized(true);
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