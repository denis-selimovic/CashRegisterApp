package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.routes.PasswordRoutes;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;

import java.util.function.Consumer;

import static javafx.scene.paint.Color.BLACK;

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

        inputField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            statusMessage.setFill(BLACK);
            statusMessage.setText("");
        });
    }

    private void resetPassword() {
        Platform.runLater(() -> {
            try {
                String resetToken = inputField.getText();

                Consumer<String> consumer = codeResponse -> Platform.runLater(
                        () -> {
                            JSONObject codeResponseJson = new JSONObject(codeResponse);
                            String codeResponseMessage = codeResponseJson.getString("message");
                            nextButton.setDisable(false);
                            progressIndicator.setVisible(false);

                            if (!codeResponseMessage.equals("OK")) {
                                statusMessage.setText(codeResponseMessage);
                                statusMessage.setFill(Color.RED);
                            } else {
                                cancelButton.fire();
                                Parent settings = FXMLUtils.loadCustomController("fxml/settings.fxml",
                                        c -> new SettingsController(true, userInfo));
                                Stage stage = new Stage();
                                StageUtils.setStage(stage, "Settings", false, StageStyle.DECORATED, Modality.APPLICATION_MODAL);
                                StageUtils.centerStage(stage, 700, 500);
                                stage.setScene(new Scene(settings));
                                stage.getIcons().add(new Image("/ba/unsa/etf/si/img/settings.png"));
                                stage.show();
                            }
                        });

                PasswordRoutes.sendVerificationInfo(userInfo, resetToken, consumer,
                        () -> System.out.println("sendVerificationInfo error"));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private void sendCode() {
        progressIndicator.setVisible(true);
        nextButton.setDisable(true);
        userInfo = inputField.getText();

        Consumer<String> consumer = codeResponse -> Platform.runLater(
                () -> {
                    JSONObject codeResponseJson = new JSONObject(codeResponse);
                    String codeResponseMessage = codeResponseJson.getString("message");
                    nextButton.setDisable(false);
                    progressIndicator.setVisible(false);

                    if (!codeResponseMessage.equals("Token is sent!")) {
                        statusMessage.setText(codeResponseMessage);
                        statusMessage.setFill(Color.RED);
                    } else {
                        statusMessage.setText(codeResponseMessage);
                        statusMessage.setFill(Color.GREEN);

                        inputLabel.setText("Please enter the token below for verification.");
                        nextButton.setText("Reset password");
                        inputField.setText("");
                        nextButton.setPrefWidth(130);
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
