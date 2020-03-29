package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.utility.HttpUtils;
import ba.unsa.etf.si.utility.UserDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jfoenix.controls.JFXButton;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.control.ProgressIndicator;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;

public class LoginFormController {

    @FXML
    private TextField usernameField, passwordField, errorField;
    @FXML
    private JFXButton submitButton;
    @FXML
    private ProgressIndicator progressIndicator;

    private Stage stage;
    public static String token = null;

    /**
     * @param stage - eventually the stage from App.java, the primary stage
     */
    public LoginFormController(Stage stage) {
        this.stage = stage;
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
            HttpUtils.send(httpRequest, HttpResponse.BodyHandlers.ofString(), consumer,
                    () -> displayError("Something went wrong. Please try again."));
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
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Screen screen = Screen.getPrimary();
            Rectangle2D rect = screen.getBounds();
            stage.setWidth(rect.getWidth());
            stage.setHeight(rect.getHeight());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * @param password - plain text from password text field
     * @return the same password hashed with SHA256
     * @throws NoSuchAlgorithmException

    private String getHashedPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1)
                hexString.append('0');

            hexString.append(hex);
        }

        return hexString.toString();
    }
     */

}
