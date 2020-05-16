package ba.etf.unsa.si;

import ba.unsa.etf.si.controllers.LoginFormController;
import ba.unsa.etf.si.utility.javafx.CustomFXMLLoader;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class ControllerTest {

    private LoginFormController loginFormController;

    @Start
    public void start(Stage stage) {
        CustomFXMLLoader<LoginFormController> fxmlLoader = FXMLUtils.getCustomLoader("fxml/loginForm.fxml", LoginFormController.class);
        loginFormController = fxmlLoader.controller;
        stage.setScene(new Scene(fxmlLoader.root));
        stage.show();
        stage.toFront();
    }

    @Test
    public void test(FxRobot fxRobot) {

    }
}
