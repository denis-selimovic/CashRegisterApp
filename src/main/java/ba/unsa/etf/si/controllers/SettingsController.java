package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import static ba.unsa.etf.si.controllers.PrimaryController.currentUser;

public class SettingsController {

    @FXML
    private BorderPane settingsPane;

    @FXML
    private JFXButton settingsProfileButton, settingsPasswordButton;

    @FXML
    private Text userInfo, userRole;

    @FXML
    public void initialize() {
        userInfo.setText(currentUser.getName() + " " + currentUser.getSurname());
        userRole.setText(currentUser.getUserRole().getRole().substring(5));

        settingsProfileButton.fire();
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
            public void initialize() {
                System.out.println("TEST PASSWORD SETTINGS");

                submitButton.setOnMouseClicked(mouseEvent -> {

                });
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
