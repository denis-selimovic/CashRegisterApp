package ba.etf.unsa.si;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.controllers.LoginFormController;
import ba.unsa.etf.si.utility.javafx.CustomFXMLLoader;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SettingsTest {

    @Start
    public void start(Stage stage) {
        CustomFXMLLoader<LoginFormController> fxmlLoader = FXMLUtils.getCustomLoader("fxml/loginForm.fxml", LoginFormController.class);
        stage.setScene(new Scene(fxmlLoader.root));
        stage.show();
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        App.primaryStage = stage;
        StageUtils.centerStage(App.primaryStage, 700, 600);
        stage.toFront();
    }

    @Test
    @Order(1)
    public void emptyBillTest(FxRobot fxRobot) {
        fxRobot.sleep(250);

        fxRobot.clickOn("#usernameField");
        fxRobot.write("amandal");
        fxRobot.clickOn("#passwordField");
        fxRobot.write("Password1");

        fxRobot.clickOn("#submitButton");

        try {
            while (fxRobot.lookup("#submitButton").tryQuery().isPresent()) {
            }
        } catch (Exception ignored) {

        }

        fxRobot.sleep(500);
        fxRobot.clickOn("#payButton");
        fxRobot.sleep(500);

        // Čekamo da dijalog postane vidljiv
        while (fxRobot.lookup(".dialog-pane").tryQuery().isEmpty()) {
        }
        ;

        // Klik na dugme Cancel
        assertTrue(fxRobot.lookup(".dialog-pane").tryQuery().isPresent());

        DialogPane dialogPane = fxRobot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        fxRobot.clickOn(cancelButton);
        Platform.runLater(() -> App.primaryStage.close());
    }
}
