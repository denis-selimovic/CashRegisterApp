package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Credentials;
import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.persistance.CredentialsRepository;
import ba.unsa.etf.si.utility.db.HashUtils;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import ba.unsa.etf.si.utility.modelutils.UserDeserializer;
import ba.unsa.etf.si.routes.CashRegisterRoutes;
import ba.unsa.etf.si.routes.LoginRoutes;
import ba.unsa.etf.si.routes.ReceiptRoutes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.json.JSONObject;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.primaryStage;

public class LoginFormController {

    @FXML private TextField usernameField, passwordField, errorField;
    @FXML private JFXButton submitButton;
    @FXML private ProgressIndicator progressIndicator;

    private final static CredentialsRepository credentialsRepository = new CredentialsRepository();
    public static String token = null;

    @FXML
    public void initialize() {
        progressIndicator.setVisible(false);
        submitButton.setOnAction(e -> submitButtonClick());

        ChangeListener<Boolean> loginFieldListener = (observableValue, aBoolean, t1) -> {
            usernameField.getStyleClass().removeAll("poljeNeispravno");
            passwordField.getStyleClass().removeAll("poljeNeispravno");
            errorField.setText("");
        };
        usernameField.focusedProperty().addListener(loginFieldListener);
        passwordField.focusedProperty().addListener(loginFieldListener);
    }

    public void submitButtonClick() {
        try {
            String username = usernameField.getText(), password = passwordField.getText();
            Consumer<String> consumer = loginResponse -> Platform.runLater(
                    () -> {
                        JSONObject loginResponseJson = new JSONObject(loginResponse);
                        if (!loginResponseJson.isNull("error")) {
                            displayError("Invalid username or password!");
                            usernameField.getStyleClass().add("poljeNeispravno");
                            passwordField.getStyleClass().add("poljeNeispravno");
                        } else {
                            token = loginResponseJson.getString("token");
                            Consumer<String> infoConsumer = infoResponse -> Platform.runLater(
                                    () -> {
                                        try {
                                            User user = UserDeserializer.getUserFromResponse(infoResponse);
                                            user.setToken(loginResponseJson.getString("token"));
                                            addCredentials(user, password);
                                            startApplication(user);
                                        } catch (JsonProcessingException e) {
                                            displayError("Something went wrong. Please try again.");
                                        }
                                    });
                            LoginRoutes.getProfile(token, infoConsumer, () -> displayError("Something went wrong. Please try again."));
                        }
                    });
            progressIndicator.setVisible(true);
            LoginRoutes.sendLoginRequest(username, password, consumer, () -> offlineLogin(username, password));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayError(String errorMessage) {
        progressIndicator.setVisible(false);
        errorField.setText(errorMessage);
    }

    private void addCredentials(User user, String password) {
        new Thread(() -> {
            if(credentialsRepository.getByUsername(user.getUsername()) == null) {
                Credentials credentials = new Credentials(user.getUsername(), HashUtils.generateSHA256(password), user.getName(), user.getUserRole());
                credentialsRepository.add(credentials);
            }
        }).start();
    }

    private void offlineLogin(String username, String password) {
        new Thread(() -> {
            Credentials c = credentialsRepository.getByUsername(username);
            if(c == null || !HashUtils.comparePasswords(c.getPassword(), password)) displayError("Something went wrong. Please try again.");
            else Platform.runLater(() -> startApplication(new User(c)));
        }).start();
    }

    private void startApplication(User loggedInUser) {
        try {
            CashRegisterRoutes.openCashRegister(token, response -> Platform.runLater(() -> StageUtils.showAlert("Information Dialog", "The cash register is now open!", Alert.AlertType.INFORMATION)),
                    () -> System.out.println("Cannot open cash register"));
            ReceiptRoutes.sendReceipts(token);
            StageUtils.setStageDimensions(primaryStage);
            primaryStage.setScene(new Scene(FXMLUtils.loadCustomController("fxml/primary.fxml", c -> new PrimaryController(loggedInUser))));
            primaryStage.getScene().getStylesheets().add(App.class.getResource("css/notification.css").toExternalForm());
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
