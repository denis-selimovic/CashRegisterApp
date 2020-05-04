package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.Credentials;
import ba.unsa.etf.si.persistance.CredentialsRepository;
import ba.unsa.etf.si.routes.PasswordRoutes;
import ba.unsa.etf.si.utility.db.HashUtils;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.json.JSONObject;

import java.util.function.Consumer;

import static ba.unsa.etf.si.controllers.PrimaryController.currentUser;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

public class SettingsController {

    @FXML
    private BorderPane settingsPane;

    @FXML
    private JFXButton settingsProfileButton, settingsPasswordButton;

    @FXML
    private Text userInfo, userRole;

    private boolean loginMode;
    private String userInfoString;

    public SettingsController(boolean loginMode) {
        this.loginMode = loginMode;
    }

    public SettingsController(boolean loginMode, String userInfo) {
        this.loginMode = loginMode;
        userInfoString = userInfo;
    }

    @FXML
    public void initialize() {
        if (loginMode) {
            settingsPasswordButton.fire();
            settingsProfileButton.setDisable(true);
            settingsProfileButton.setStyle("-fx-background-color: slategray");
            userInfo.setText(userInfoString);
        } else {
            userInfo.setText(currentUser.getName() + " " + currentUser.getSurname());
            userRole.setText(currentUser.getUserRole().getRole().substring(5));

            settingsProfileButton.fire();
        }
    }

    @FXML
    private void displayProfileInformation() {

        class ProfileInformationController {
            @FXML
            private TextField usernameField, emailField, phoneField, countryField;

            @FXML
            public void initialize() {
                usernameField.setText(currentUser.getUsername());
                emailField.setText(currentUser.getEmail());
                phoneField.setText(currentUser.getPhoneNumber());
                countryField.setText(currentUser.getCity() + ", " + currentUser.getCountry());
            }
        }

        try {
            FXMLLoader profileInformationLoader = FXMLUtils.getFXMLLoader("fxml/settings_profile.fxml");
            profileInformationLoader.setController(new ProfileInformationController());
            Parent profilePane = profileInformationLoader.load();
            settingsPane.setCenter(profilePane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void displayPasswordSettings() {

        class PasswordSettingsController {
            @FXML
            private PasswordField passField, newPassField, cNewPassField;
            @FXML
            private JFXButton submitButton;
            @FXML
            private Text passwordStatusMessage;
            @FXML
            private ProgressIndicator progressIndicator;

            private void displayPasswordError(String message){
                progressIndicator.setVisible(false);
                passwordStatusMessage.setFill(RED);
                passwordStatusMessage.setText(message);
            }

            @FXML
            public void initialize() {
                if (loginMode)
                    passField.setDisable(true);

                progressIndicator.setVisible(false);

                submitButton.setOnMouseClicked(mouseEvent -> {
                            passwordStatusMessage.setText("");
                            progressIndicator.setVisible(true);

                            CredentialsRepository credentialsRepository = new CredentialsRepository();
                            Credentials currentUserCredentials = credentialsRepository
                                    .getByUsername(loginMode ? userInfoString : currentUser.getUsername());

                            if (!loginMode && !HashUtils.comparePasswords(currentUserCredentials.getPassword(), passField.getText()))
                                displayPasswordError("Current password is incorrect!");
                            else if (!newPassField.getText().equals(cNewPassField.getText()))
                                displayPasswordError("Passwords do not match!");
                            else if (!newPassField.getText().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"))
                                displayPasswordError("Password must meet complexity requirements:\n" +
                                        "- At least one digit\n" +
                                        "- At least one uppercase and lowercase letter\n" +
                                        "- At least 8 characters long");
                            else{
                                Consumer<String> consumer = codeResponse -> Platform.runLater(
                                        () -> {
                                            JSONObject passwordResetJsonResponse = new JSONObject(codeResponse);
                                            String responseMessage = passwordResetJsonResponse.getString("message");

                                            if (responseMessage.contains("successfully")){
                                                if(!loginMode)
                                                    currentUser.clearOneTimePassword();
                                                currentUserCredentials.setPassword(HashUtils.generateSHA256(newPassField.getText()));
                                                credentialsRepository.update(currentUserCredentials);
                                                passwordStatusMessage.setFill(GREEN);
                                            }
                                            else
                                                passwordStatusMessage.setFill(RED);

                                            progressIndicator.setVisible(false);
                                            passwordStatusMessage.setText(responseMessage);
                                        });

                                PasswordRoutes.setNewPassword(loginMode ? userInfoString : currentUser.getUsername(), newPassField.getText(), consumer,
                                        () -> System.out.println("setNewPassword error"));
                            }
                        }
                );
            }
        }

        try {
            FXMLLoader passwordSettingsLoader = FXMLUtils.getFXMLLoader("fxml/settings_password.fxml");
            passwordSettingsLoader.setController(new PasswordSettingsController());
            Parent passwordPane = passwordSettingsLoader.load();
            settingsPane.setCenter(passwordPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
