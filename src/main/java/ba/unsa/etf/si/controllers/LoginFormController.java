package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Credentials;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.models.status.ReceiptStatus;
import ba.unsa.etf.si.persistance.CredentialsRepository;
import ba.unsa.etf.si.persistance.ReceiptRepository;
import ba.unsa.etf.si.utility.HashUtils;
import ba.unsa.etf.si.utility.HttpUtils;
import ba.unsa.etf.si.utility.UserDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ba.unsa.etf.si.App.DOMAIN;
import static ba.unsa.etf.si.App.primaryStage;

public class LoginFormController {

    @FXML
    private TextField usernameField, passwordField, errorField;
    @FXML
    private JFXButton submitButton;
    @FXML
    private ProgressIndicator progressIndicator;

    private final ReceiptRepository receiptRepository = new ReceiptRepository();
    private final CredentialsRepository credentialsRepository = new CredentialsRepository();

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

    /**
     * onAction method for the 'Submit' button
     */
    public void submitButtonClick() {
        try {
            String username = usernameField.getText();
            //String password = getHashedPassword(passwordField.getText()); -- soon to be hashed
            String password = passwordField.getText();

            // Request body for the POST login (raw JSON)
            HttpRequest.BodyPublisher bodyPublisher =
                    HttpRequest.BodyPublishers.ofString("{\"username\":\"" + username + "\"," +
                            "\"password\":\"" + password + "\"}");

            HttpRequest httpRequest = HttpUtils.POST(bodyPublisher, DOMAIN + "/api/login",
                    "Content-Type", "application/json");

            // The callback after receiving the response for the login request
            Consumer<String> consumer = loginResponse -> Platform.runLater(
                    () -> {
                        JSONObject loginResponseJson = new JSONObject(loginResponse);

                        // JSON contains field "error" => JSON request body is invalid
                        if (!loginResponseJson.isNull("error")) {
                            displayError("Invalid username or password!");
                            usernameField.getStyleClass().add("poljeNeispravno");
                            passwordField.getStyleClass().add("poljeNeispravno");
                        } else {
                            // At this point, send a GET request to receive
                            // more info about the User who is trying to log in

                            token = loginResponseJson.getString("token");
                            HttpRequest getUserInfoRequest = HttpUtils.GET(DOMAIN + "/api/profile",
                                    "Authorization", "Bearer " + token);

                            // The callback after receveing the response for the user info request
                            Consumer<String> infoConsumer = infoResponse -> Platform.runLater(
                                    () -> {
                                        try {
                                            ObjectMapper userMapper = new ObjectMapper();
                                            SimpleModule module = new SimpleModule();
                                            module.addDeserializer(User.class, new UserDeserializer());
                                            userMapper.registerModule(module);

                                            User user = userMapper.readValue(infoResponse, User.class);
                                            user.setToken(loginResponseJson.getString("token"));

                                            new Thread(() -> {
                                                if(credentialsRepository.getByUsername(user.getUsername()) == null) {
                                                    Credentials credentials = new Credentials(user.getUsername(), HashUtils.generateSHA256(password), user.getName(), user.getUserRole());
                                                    credentialsRepository.add(credentials);
                                                }
                                            }).start();

                                            startApplication(user);
                                        } catch (JsonProcessingException e) {
                                            displayError("Something went wrong. Please try again.");
                                        }
                                    });

                            HttpUtils.send(getUserInfoRequest, HttpResponse.BodyHandlers.ofString(), infoConsumer,
                                    () -> displayError("Something went wrong. Please try again."));
                        }
                    });

            progressIndicator.setVisible(true);
            HttpUtils.send(httpRequest, HttpResponse.BodyHandlers.ofString(), consumer, () -> {
                new Thread(() -> {
                    Credentials c = credentialsRepository.getByUsername(username);
                    if(c == null || !HashUtils.comparePasswords(c.getPassword(), password)) displayError("Something went wrong. Please try again.");
                    else {
                        displayError("Logging in offline mode!");
                        Platform.runLater(() -> startApplication(new User(c)));
                    }
                }).start();

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Display appropriate error message
     */
    private void displayError(String errorMessage) {
        progressIndicator.setVisible(false);
        errorField.setText(errorMessage);
    }

    /**
     * To change the scene of the stage from login to home
     *
     * @param loggedInUser - the user that is trying to log in;
     *                     should be forwarded to PrimaryController for further needs
     */
    private void startApplication(User loggedInUser) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/primary.fxml"));
            fxmlLoader.setControllerFactory(c -> new PrimaryController(loggedInUser));
            Scene scene = new Scene(fxmlLoader.load());

            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            primaryStage.setX(bounds.getMinX());
            primaryStage.setY(bounds.getMinY());
            primaryStage.setWidth(bounds.getWidth());
            primaryStage.setHeight(bounds.getHeight());
            primaryStage.setScene(scene);
            primaryStage.getScene().getStylesheets().add(App.class.getResource("css/notification.css").toExternalForm());
            primaryStage.show();
            sendReceipts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendReceipts() {
        new Thread(() -> {
            List<Receipt> receiptList = receiptRepository.getAll().stream().filter(r -> r.getReceiptStatus() == null).collect(Collectors.toList());
            receiptList.forEach(r -> {
                HttpRequest POST = HttpUtils.POST(HttpRequest.BodyPublishers.ofString(r.toString()), DOMAIN + "/api/receipts",
                        "Content-Type", "application/json", "Authorization", "Bearer " + token);
                HttpUtils.send(POST, HttpResponse.BodyHandlers.ofString(), response -> {
                    if(new JSONObject(response).getInt("statusCode") == 200) {
                        r.setReceiptStatus(ReceiptStatus.PAID);
                        receiptRepository.update(r);
                    }
                }, () -> System.out.println("ERROR"));
            });
        }).start();
    }
}
