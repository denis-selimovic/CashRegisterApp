package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.routes.LoginRoutes;
import ba.unsa.etf.si.interfaces.TokenReceiver;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.json.JSONObject;
import java.util.function.Consumer;

public class LoginDialogController {

    @FXML private PasswordField password;
    @FXML private Label title, header;
    @FXML private JFXButton login;

    private final TokenReceiver receiver;
    private final String dialogTitle;
    private final String dialogHeader;

    public LoginDialogController(TokenReceiver receiver, String dialogTitle, String dialogHeader) {
        this.receiver = receiver;
        this.dialogTitle = dialogTitle;
        this.dialogHeader = dialogHeader;
    }

    private final Consumer<String> callback = response -> {
        JSONObject objResponse = new JSONObject(response);
        Platform.runLater(() -> login.setDisable(false));
        if(!objResponse.isNull("error")) wrongPassword();
        else closeDialog(objResponse.getString("token"));
    };

    @FXML
    private void initialize() {
        title.setText(dialogTitle);
        header.setText(dialogHeader);
        password.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            password.getStyleClass().removeAll("wrong-pw");
        });
        login.setOnAction(e -> login());
    }

    private void login() {
        login.setDisable(true);
        LoginRoutes.sendLoginRequest(PrimaryController.currentUser.getUsername(), password.getText(), callback, this::wrongPassword);
    }

    private void closeDialog(String token) {
        Platform.runLater(() -> {
            ((Stage) login.getScene().getWindow()).close();
            receiver.onTokenReceived(token);
        });
    }

    private void wrongPassword() {
        Platform.runLater(() -> {
            password.getStyleClass().add("wrong-pw");
            password.setText("");
        });
    }
}
