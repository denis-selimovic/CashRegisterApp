package ba.unsa.etf.si;

import ba.unsa.etf.si.utility.javafx.StageUtils;
import ba.unsa.etf.si.utility.server.Connectivity;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;


/**
 * JavaFX App
 */
public class App extends Application {

    public static Stage primaryStage;
    public static final String DOMAIN = "http://cash-register-server-si.herokuapp.com";
    public static final Long CASH_REGISTER_ID = 1L;
    public static final Long BRANCH_ID = 1L;
    public static final Long MERCHANT_ID = 1L;
    public static final String TARGET = DOMAIN + "/api/test";

    public static Long getCashRegisterID() {
        return CASH_REGISTER_ID;
    }

    public static Long getBranchID() {
        return BRANCH_ID;
    }

    public static Long getMerchantID() {
        return MERCHANT_ID;
    }

    public static final Connectivity connectivity = new Connectivity(TARGET);

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Cash Register App");
        primaryStage.getIcons().add(new Image("/ba/unsa/etf/si/img/appIcon.png"));
        Scene scene = new Scene(FXMLUtils.loadController("fxml/loginForm.fxml"));
        StageUtils.centerStage(primaryStage, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        connectivity.run();
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connectivity.cancel();
    }

    public static void main(String[] args) {
        launch();
    }

}