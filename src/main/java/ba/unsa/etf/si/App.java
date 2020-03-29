package ba.unsa.etf.si;

import ba.unsa.etf.si.controllers.LoginFormController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Stage primaryStage;
    public static final String DOMAIN = "http://cash-register-server-si.herokuapp.com";

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        Scene scene = new Scene(loadFXML(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Cash Register App");
        stage.getIcons().add(new Image("/ba/unsa/etf/si/img/appIcon.png"));
        stage.show();
    }


    private Parent loadFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/loginForm.fxml"));
        fxmlLoader.setControllerFactory(c -> new LoginFormController(primaryStage));
        return fxmlLoader.load();
    }


    public static void main(String[] args) {
        launch();
    }

}