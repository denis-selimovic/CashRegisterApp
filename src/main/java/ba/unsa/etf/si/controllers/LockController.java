package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.routes.LoginRoutes;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import org.json.JSONObject;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.primaryStage;

public class LockController {

    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label usernameLabel;
    @FXML private PasswordField passwordField;
    @FXML private JFXButton loginBtn, logoutBtn;;

    private final User user;

    private final Consumer<String> loginCallback = response -> {
        progressIndicator.setVisible(false);
        JSONObject jsonResponse = new JSONObject(response);
        if(!jsonResponse.isNull("error")) showError();
        else Platform.runLater(this::startApp);
    };

    public LockController(User user) {
        this.user = user;
    }

    @FXML
    public void initialize() {
        progressIndicator.setVisible(false);
        usernameLabel.textProperty().bind(new SimpleStringProperty(user.getUsername()));
        loginBtn.setOnAction(e -> login());
        logoutBtn.setOnAction(e -> logout());
        passwordField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            passwordField.getStyleClass().removeAll("error");
        });
    }

    private void logout() {
        try {
            StageUtils.centerStage(primaryStage,800, 600);
            primaryStage.setScene(new Scene(FXMLUtils.loadController("fxml/loginForm.fxml")));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login() {
        loginBtn.setDisable(true);
        progressIndicator.setVisible(true);
        LoginRoutes.sendLoginRequest(usernameLabel.getText(), passwordField.getText(), loginCallback, this::showError);
    }

    private void startApp() {
        try {
            StageUtils.setStageDimensions(primaryStage);
            primaryStage.setScene(new Scene(FXMLUtils.loadCustomController("fxml/primary.fxml", c -> new PrimaryController(user))));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError() {
        loginBtn.setDisable(false);
        passwordField.getStyleClass().add("error");
        passwordField.setText("");
    }
}
