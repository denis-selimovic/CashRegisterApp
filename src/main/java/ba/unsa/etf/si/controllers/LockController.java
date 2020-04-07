package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import static ba.unsa.etf.si.App.primaryStage;

public class LockController {

    @FXML
    private Label usernameLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private JFXButton loginBtn, logoutBtn;;

    @FXML
    public void initialize() {
        usernameLabel.textProperty().bind(new SimpleStringProperty(PrimaryController.currentUser.getUsername()));
        loginBtn.setOnAction(e -> login());
        logoutBtn.setOnAction(e -> logout());
    }

    private void login() {

    }

    private void logout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/loginForm.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            App.centerStage(primaryStage,800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
