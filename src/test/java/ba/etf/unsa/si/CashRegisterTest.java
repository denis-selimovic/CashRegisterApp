package ba.etf.unsa.si;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.controllers.LoginFormController;
import ba.unsa.etf.si.utility.javafx.CustomFXMLLoader;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CashRegisterTest {

    @Start
    public void start(Stage stage) {
        CustomFXMLLoader<LoginFormController> fxmlLoader = FXMLUtils.getCustomLoader("fxml/loginForm.fxml", LoginFormController.class);
        stage.setScene(new Scene(fxmlLoader.root));
        stage.show();
        App.primaryStage = stage;
        StageUtils.centerStage(App.primaryStage, 800, 600);
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        stage.toFront();
    }

    @BeforeAll
    public static void before() {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
    }

    @Test
    @Order(1)
    public void checkForSettings(FxRobot fxRobot) {
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
        fxRobot.clickOn("#menuButton");
        fxRobot.clickOn("#settingsButton");
        assertTrue(fxRobot.lookup("#settingsProfileButton").tryQuery().isPresent());

        fxRobot.sleep(250);
        fxRobot.press(KeyCode.ALT).press(KeyCode.F4);
        fxRobot.sleep(1000);
    }
}
