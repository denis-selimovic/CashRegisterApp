package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.routes.LoginRoutes;
import ba.unsa.etf.si.routes.PasswordRoutes;
import ba.unsa.etf.si.utility.http.HttpUtils;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import ba.unsa.etf.si.utility.modelutils.UserDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;

public class ForgotPasswordController {

    @FXML
    private JFXButton cancelButton, nextButton;
    @FXML
    private TextField inputField;
    @FXML
    private Text inputLabel, statusMessage;
    @FXML
    private ProgressIndicator progressIndicator;

    private String userInfo;

    @FXML
    public void initialize() {
        progressIndicator.setVisible(false);

        nextButton.setOnAction(event -> sendCode());
    }

    private void resetPassword() {
        Platform.runLater(() -> {
            try {
                Thread.sleep(2500);
                cancelButton.fire();

                Parent settings = FXMLUtils.loadCustomController("fxml/settings.fxml", c -> new SettingsController(true));
                Stage stage = new Stage();
                StageUtils.setStage(stage, "Settings", false, StageStyle.DECORATED, Modality.APPLICATION_MODAL);
                StageUtils.centerStage(stage, 700, 500);
                stage.setScene(new Scene(settings));
                stage.getIcons().add(new Image("/ba/unsa/etf/si/img/settings.png"));
                stage.show();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private void sendCode() {
        progressIndicator.setVisible(true);
        userInfo = inputField.getText();
        
        Consumer<String> consumer = codeResponse -> Platform.runLater(
                () -> {
                    JSONObject codeResponseJson = new JSONObject(codeResponse);
                    String codeResponseMessage = codeResponseJson.getString("message");
                    if (codeResponseMessage.charAt(1) == 'h') {
                        progressIndicator.setVisible(false);
                        statusMessage.setText(codeResponseMessage);
                        statusMessage.setFill(Color.RED);
                    } else {
                        inputLabel.setText("The token was sent to your e-mail. Please enter it below for verification.");
                        nextButton.setText("Reset password");
                        nextButton.setOnAction(event -> resetPassword());
                    }
                });

        PasswordRoutes.getResetToken(userInfo, consumer, () -> System.out.println("getResetToken error"));
    }

    @FXML
    private void cancelButtonClick() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}
