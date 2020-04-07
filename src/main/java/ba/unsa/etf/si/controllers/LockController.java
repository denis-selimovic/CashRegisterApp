package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.utility.HttpUtils;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static ba.unsa.etf.si.App.primaryStage;

public class LockController {

    @FXML
    private Label usernameLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private JFXButton loginBtn, logoutBtn;;

    private User user;

    public LockController(User user) {
        this.user = user;
    }

    @FXML
    public void initialize() {
        usernameLabel.textProperty().bind(new SimpleStringProperty(user.getUsername()));
        loginBtn.setOnAction(e -> login());
        logoutBtn.setOnAction(e -> logout());
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

    private void login() {
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers
                .ofString("{\"username\": \"" + usernameLabel.getText() + "\","
                        + "\"password\": \"" + passwordField.getText() + "\"}");
        HttpRequest POST = HttpUtils.POST(bodyPublisher,App.DOMAIN + "/api/login", "Content-Type", "application/json");
        HttpUtils.send(POST, HttpResponse.BodyHandlers.ofString(), response -> {
            JSONObject jsonResponse = new JSONObject(response);
            if(!jsonResponse.isNull("error")) showError();
            else startApp();
        }, () -> {
            System.out.println("ERROR!");
        });
    }

    private void startApp() {

    }

    private void showError() {

    }
}
