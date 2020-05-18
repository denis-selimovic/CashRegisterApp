package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Credentials;
import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.persistance.repository.CashRegisterRepository;
import ba.unsa.etf.si.persistance.repository.CredentialsRepository;
import ba.unsa.etf.si.routes.CashRegisterRoutes;
import ba.unsa.etf.si.routes.LoginRoutes;
import ba.unsa.etf.si.routes.ReceiptRoutes;
import ba.unsa.etf.si.utility.db.HashUtils;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import ba.unsa.etf.si.utility.modelutils.UserDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;

import java.util.function.Consumer;

import static ba.unsa.etf.si.App.cashRegister;
import static ba.unsa.etf.si.App.primaryStage;

public class LoginFormController {

    @FXML
    private TextField usernameField, passwordField, errorField;
    @FXML
    private JFXButton submitButton;
    @FXML
    private ProgressIndicator progressIndicator;

    private final static CredentialsRepository credentialsRepository = new CredentialsRepository();
    private static final CashRegisterRepository cashRegisterRepository = new CashRegisterRepository();
    public static String token = null;

    private boolean testing = false;

    public LoginFormController() {
        this.testing = false;
    }

    public LoginFormController(boolean testing) {
        this.testing = testing;
    }

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
                                            if(!testing) addCredentials(user, password);
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
            Credentials userCredentials = credentialsRepository.getByUsername(user.getUsername());
            if (userCredentials == null) {
                Credentials credentials = new Credentials(user.getUsername(), HashUtils.generateSHA256(password),
                        user.getName(), user.getUserRole());
                credentialsRepository.add(credentials);
            } else {
                userCredentials.setPassword(HashUtils.generateSHA256(password));
                credentialsRepository.update(userCredentials);
            }
        }).start();
    }

    private void offlineLogin(String username, String password) {
        new Thread(() -> {
            Credentials c = credentialsRepository.getByUsername(username);
            if (c == null || !HashUtils.comparePasswords(c.getPassword(), password))
                displayError("Something went wrong. Please try again.");
            else Platform.runLater(() -> startApplication(new User(c)));
        }).start();
    }

    private void startApplication(User loggedInUser) {
        CashRegisterRoutes.getCashRegisterData(token, response -> {
            cashRegister.initialize(new JSONObject(response));
            if(!testing) {
                cashRegisterRepository.configureCashRegister();
                ReceiptRoutes.sendReceipts(token);
            }
            Platform.runLater(() -> setScene(loggedInUser));
        }, () -> System.out.println("Could not fetch cash register data!"));
    }

    private void setScene(User loggedInUser) {
        StageUtils.setStageDimensions(primaryStage);
        primaryStage.setScene(new Scene(FXMLUtils.loadCustomController("fxml/primary.fxml", c -> new PrimaryController(loggedInUser))));
        primaryStage.getScene().getStylesheets().add(App.class.getResource("css/notification.css").toExternalForm());
        primaryStage.show();
    }

    @FXML
    private void forgotPassword() {
        Parent forgotP4s5w0rd = FXMLUtils.loadController("fxml/forgot_password.fxml");
        Stage stage = new Stage();
        StageUtils.setStage(stage, "Forgot password", false, StageStyle.DECORATED, Modality.APPLICATION_MODAL);
        StageUtils.centerStage(stage, 450, 300);
        stage.setScene(new Scene(forgotP4s5w0rd));
        stage.getIcons().add(new Image("/ba/unsa/etf/si/img/loginForm/loginPass.png"));
        stage.show();
    }
}
