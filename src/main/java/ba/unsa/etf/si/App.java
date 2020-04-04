package ba.unsa.etf.si;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Stage primaryStage;
    public static final String DOMAIN = "http://cash-register-server-si.herokuapp.com";

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Cash Register App");
        primaryStage.getIcons().add(new Image("/ba/unsa/etf/si/img/appIcon.png"));
        Scene scene = new Scene(loadFXML());
        centerStage(800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private Parent loadFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/loginForm.fxml"));
        return fxmlLoader.load();
    }

    public static void centerStage(int width, int height) {
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
    }

    public static void main(String[] args) {
        launch();
    }

}