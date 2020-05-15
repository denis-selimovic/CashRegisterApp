package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.utility.db.HashUtils;
import ba.unsa.etf.si.utility.properties.PropertiesReader;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class PathChooserController {

    @FXML
    private JFXButton submitBtn, closeBtn;
    @FXML
    private PasswordField pwField;

    private final Runnable action, close;

    public PathChooserController(Runnable action, Runnable close) {
        this.action = action;
        this.close = close;
    }

    @FXML
    private void initialize() {
        submitBtn.setOnAction(e -> showDirectoryChooser());
        closeBtn.setOnAction(e -> close());
        pwField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            pwField.getStyleClass().removeAll("pwField-incorrect");
            pwField.getStyleClass().add("pwField-correct");
        });
    }

    private void showDirectoryChooser() {
        if(HashUtils.comparePasswords(PropertiesReader.getValue("administrator"), pwField.getText())) {
            action.run();
            close.run();
            close();
        }
        else {
            Platform.runLater(() -> {
                pwField.getStyleClass().removeAll("pwField-correct");
                pwField.getStyleClass().add("pwField-incorrect");
                pwField.setText("");
            });
        }
    }

    private void close() {
        ((Stage)submitBtn.getScene().getWindow()).close();
    }
}
