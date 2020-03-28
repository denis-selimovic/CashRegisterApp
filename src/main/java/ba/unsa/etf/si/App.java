package ba.unsa.etf.si;

import ba.unsa.etf.si.controllers.LoginFormController;
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
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML("fxml/loginForm.fxml"), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Cash Register App");
        stage.getIcons().add(new Image("/ba/unsa/etf/si/img/appIcon.png"));
        stage.show();
    }


    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        fxmlLoader.setControllerFactory(c -> new LoginFormController(primaryStage));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}