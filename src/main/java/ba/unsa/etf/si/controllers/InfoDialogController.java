package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.utility.HttpUtils;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import static ba.unsa.etf.si.App.DOMAIN;

public class InfoDialogController {
    public Button exitButton;
    public JFXButton abort;
    public Label informationLabel;


    @FXML
    public void initialize() {
        exitButton.setOnAction(e -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        });

        abort.setOnAction(e -> {
            Stage stage = (Stage) abort.getScene().getWindow();
            stage.close();
        });

    }

    public void setInformationLabel (String input) {
        informationLabel.setText(input);
    }
}
