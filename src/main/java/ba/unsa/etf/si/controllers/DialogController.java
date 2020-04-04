package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.User;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DialogController   {
    public Button cancelReceipt;
    public TextField receiptField;
    public Button revertReceipt;
    public Button abort;
    public Button exitButton;
    public Label warningLabel;

    @FXML
    public void initialize() {
        System.out.println("!!!!");
        exitButton.setOnAction(e -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        });

    }

    public void setId (Long x) {
        String newString =  warningLabel.getText();
        newString=  newString.replace("rec_id", Long.toString(x));
        warningLabel.setText(newString);
    }
}
