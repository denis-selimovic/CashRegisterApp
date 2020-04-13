package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.utility.HttpUtils;
import ba.unsa.etf.si.utility.interfaces.TokenReceiver;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginDialogController {

    @FXML
    private PasswordField password;
    @FXML
    private Label title, header;
    @FXML
    private JFXButton login;

    private final TokenReceiver receiver;
    private final String dialogTitle;
    private final String dialogHeader;

    public LoginDialogController(TokenReceiver receiver, String dialogTitle, String dialogHeader) {
        this.receiver = receiver;
        this.dialogTitle = dialogTitle;
        this.dialogHeader = dialogHeader;
    }

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
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers
                .ofString("{\"username\": \"" + PrimaryController.currentUser.getUsername() + "\","
                        + "\"password\": \"" + password.getText() + "\"}");
        HttpRequest POST = HttpUtils.POST(bodyPublisher, App.DOMAIN + "/api/login", "Content-Type", "application/json");
        HttpUtils.send(POST, HttpResponse.BodyHandlers.ofString(), response -> {
            JSONObject objResponse = new JSONObject(response);
            Platform.runLater(() -> login.setDisable(false));
            if(!objResponse.isNull("error")) wrongPassword();
            else closeDialog(objResponse.getString("token"));
        }, this::wrongPassword);
    }

    private void closeDialog(String token) {
        Platform.runLater(() -> {
            Stage stage = (Stage) login.getScene().getWindow();
            stage.close();
            if(stage.isShowing()) stage.close();
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
