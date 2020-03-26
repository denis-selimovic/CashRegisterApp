package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.utility.HttpUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;


public class LoginFormController {

    @FXML
    private TextField usernameField, passwordField, errorField;
    @FXML
    private JFXButton submitButton;

    private Stage stage;

    /**
     * @param stage - hopefully the stage from App.java, the primary stage
     */
    public LoginFormController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
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
            String password = getHashedPassword(passwordField.getText());

            HttpRequest.BodyPublisher bodyPublisher =
                    HttpRequest.BodyPublishers.ofString("{\"username\":\"" + username + "\"," +
                            "\"password\":\"" + password + "\"}");

            HttpRequest httpRequest = HttpUtils.POST(bodyPublisher, "http://localhost:8080/api/login", "Content-Type", "application/json");
            Consumer<String> consumer = response -> Platform.runLater(
                    () -> {
                        JSONObject responseJson = new JSONObject(response);
                        if (!responseJson.isNull("error")) {
                            usernameField.getStyleClass().add("poljeNeispravno");
                            passwordField.getStyleClass().add("poljeNeispravno");
                            errorField.setText("Invalid username or password!");
                        } else {
                            // User user = new User(responseJson.get("firstName").toString(), username, password);
                            startApplication();
                        }
                    });

            HttpUtils.send(httpRequest, HttpResponse.BodyHandlers.ofString(), consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To change the scene of the stage from login to home
     */
    private void startApplication() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/primary.fxml"));
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

    /**
     * @param password - plain text from password text field
     * @return the same password hashed with SHA256
     * @throws NoSuchAlgorithmException
     */
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

}
