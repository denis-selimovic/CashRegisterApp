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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Stage primaryStage;
    public static final String DOMAIN = "http://cash-register-server-si.herokuapp.com";
    public static final Long CASH_REGISTER_ID = 1L;
    public static final Long BRANCH_ID = 1L;
    public static final Long MERCHANT_ID = 1L;

    public static Long getCashRegisterID() {
        return CASH_REGISTER_ID;
    }

    public static Long getBranchID() {
        return BRANCH_ID;
    }

    public static Long getMerchantID() {
        return MERCHANT_ID;
    }

    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        primaryStage = stage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Cash Register App");
        primaryStage.getIcons().add(new Image("/ba/unsa/etf/si/img/appIcon.png"));
        Scene scene = new Scene(loadFXML());
        centerStage(primaryStage, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private Parent loadFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/loginForm.fxml"));
        return fxmlLoader.load();
    }

    public static void centerStage(Stage stage, int width, int height) {
        stage.setWidth(width);
        stage.setHeight(height);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public static void main(String[] args) {
        launch();
    }

}