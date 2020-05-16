package ba.unsa.etf.si;

import ba.unsa.etf.si.models.CashRegister;
import ba.unsa.etf.si.notifications.client.NotificationStompClient;
import ba.unsa.etf.si.notifications.topics.GuestNotificationTopic;
import ba.unsa.etf.si.notifications.topics.InventoryNotificationTopic;
import ba.unsa.etf.si.services.ConnectivityService;
import ba.unsa.etf.si.services.DailyReportService;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import ba.unsa.etf.si.utility.properties.PropertiesReader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;


/**
 * JavaFX App
 */
public class App extends Application {

    public static final Long CASH_REGISTER_ID = Long.parseLong(PropertiesReader.getValue("cash_register_id"));
    public static Stage primaryStage;

    public static final String DOMAIN = "http://cash-register-server-si.herokuapp.com";
    public static final String BROKER = "ws://cash-register-server-si.herokuapp.com/ws";
    public static final String TARGET = DOMAIN + "/api/test";

    public static final ConnectivityService connectivity = new ConnectivityService(TARGET);
    public static final DailyReportService dailyReportService = new DailyReportService();

    public static final NotificationStompClient stompClient = new NotificationStompClient(new GuestNotificationTopic(), new MappingJackson2MessageConverter());
    public static final NotificationStompClient inventoryStompClient = new NotificationStompClient(new InventoryNotificationTopic(), new StringMessageConverter());
    public static final CashRegister cashRegister = new CashRegister();

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
        connectivity.stop();
        dailyReportService.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}