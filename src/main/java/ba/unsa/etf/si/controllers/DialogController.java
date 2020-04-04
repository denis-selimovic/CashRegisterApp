package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.utility.HttpUtils;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.persistence.criteria.CriteriaBuilder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static ba.unsa.etf.si.App.DOMAIN;

public class DialogController   {
    public JFXButton cancelReceipt;
    public TextField receiptField;
    public JFXButton revertReceipt;
    public JFXButton abort;
    public Button exitButton;
    public Label warningLabel;

    private DialogStatus dialogStatus = new DialogStatus();
    private String id = "error";
    private String text = "Kliknut je abort button!";
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

        cancelReceipt.setOnAction(e -> {

            dialogStatus.setCancel(true);
            Stage stage = (Stage) cancelReceipt.getScene().getWindow();
            stage.close();
            //route is not available
            if (false) {
                HttpRequest getSuppliesData = HttpUtils.DELETE(DOMAIN + "/api/receipts", "Authorization", "Bearer " + "<token_placeholder>");
                HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), null, () -> {
                    System.out.println("Something went wrong.");
                });
            }
        });

        receiptField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue!=null && newValue.equals(id))   buttonBlock(false);
            else buttonBlock(true);
        });

        buttonBlock(true);

    }

    public void setId (Long x) {
        id = Long.toString(x);
        String newString =  warningLabel.getText();
        newString=  newString.replace("rec_id", id);
        warningLabel.setText(newString);
    }

    public void buttonBlock (boolean block) {
        cancelReceipt.setDisable(block);
        revertReceipt.setDisable(block);
        if (block) {
            revertReceipt.getStyleClass().add("buttonBlocked");
            cancelReceipt.getStyleClass().add("buttonBlocked");
        }
        else {
            revertReceipt.getStyleClass().removeAll("buttonBlocked");
            cancelReceipt.getStyleClass().removeAll("buttonBlocked");
        }
    }

    public String getText () {
        return text;
    }

    public DialogStatus getStatus () {
        return dialogStatus;
    }

    public static class DialogStatus {
        boolean cancel;

        public DialogStatus () {
            cancel = false;
        }
        public DialogStatus(boolean cancel) {
            this.cancel = cancel;
        }

        public boolean isCancel() {
            return cancel;
        }

        public void setCancel(boolean cancel) {
            this.cancel = cancel;
        }
    }

}
