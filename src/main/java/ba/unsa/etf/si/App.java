package ba.unsa.etf.si;

import ba.unsa.etf.si.notifications.client.NotificationStompClient;
import ba.unsa.etf.si.notifications.topics.GuestNotificationTopic;
import ba.unsa.etf.si.services.ConnectivityService;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * JavaFX App
 */
public class App extends Application {

    public static Stage primaryStage;

    public static final String DOMAIN = "http://cash-register-server-si.herokuapp.com";
    public static final String BROKER = "ws://cash-register-server-si.herokuapp.com/ws";
    public static final String TARGET = DOMAIN + "/api/test";

    public static final Long CASH_REGISTER_ID = 1L;
    public static final Long BRANCH_ID = 1L;
    public static final Long MERCHANT_ID = 1L;
    public static String UUID = "";
    public static double VAT_RATE = 0.17;

    public static Long getCashRegisterID() {
        return CASH_REGISTER_ID;
    }
    public static Long getBranchID() {
        return BRANCH_ID;
    }
    public static Long getMerchantID() {
        return MERCHANT_ID;
    }

    public static final ConnectivityService connectivity = new ConnectivityService(TARGET);
    public static final NotificationStompClient stompClient = new NotificationStompClient(new GuestNotificationTopic());

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        StageUtils.setStage(primaryStage, "Cash Register App", false, StageStyle.UNDECORATED, null);
        primaryStage.getIcons().add(new Image("/ba/unsa/etf/si/img/appIcon.png"));
        StageUtils.centerStage(primaryStage, 800, 600);
        primaryStage.setScene(new Scene(FXMLUtils.loadController("fxml/loginForm.fxml")));
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