package ba.etf.unsa.si;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.controllers.LoginFormController;
import ba.unsa.etf.si.utility.javafx.CustomFXMLLoader;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerTest {


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


    @Test
    @Order(1)
    public void invalidLoginInfo(FxRobot fxRobot) {
        fxRobot.sleep(250);

        fxRobot.clickOn("#usernameField");
        fxRobot.write("amandal");
        fxRobot.clickOn("#passwordField");
        fxRobot.write("Password12345");

        fxRobot.clickOn("#submitButton");

        TextField tf = fxRobot.lookup("#errorField").queryAs(TextField.class);
        while (tf.getText().equals("")) {
        }

        assertEquals("Invalid username or password!", tf.getText());
    }

    @Test
    @Order(2)
    public void forgotPassword(FxRobot fxRobot) {
        fxRobot.sleep(250);

        fxRobot.clickOn("#forgotPassword");

        fxRobot.sleep(250);
        fxRobot.clickOn("#inputField");
        fxRobot.write("amandalinjo");

        fxRobot.clickOn("#nextButton");

        Text text = fxRobot.lookup("#statusMessage").queryAs(Text.class);
        while (text.getText().equals("")) {
        }

        assertTrue(text.getText().contains("doesn't belong"));

        fxRobot.sleep(500);
        fxRobot.clickOn("#cancelButton");
    }

    @Test
    @Order(3)
    public void validPassword(FxRobot fxRobot) {
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
        boolean query = fxRobot.lookup("#first").tryQuery().isPresent();
        assertTrue(query);
        fxRobot.sleep(250);
    }

    /*@Test
    @Order(4)
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
        fxRobot.sleep(8000);
    }*/

    /*@Test
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
    }*/

}
