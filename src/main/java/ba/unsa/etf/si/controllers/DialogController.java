package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.utility.HttpUtils;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import javax.persistence.criteria.CriteriaBuilder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;
import static ba.unsa.etf.si.controllers.PrimaryController.currentUser;

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


    Consumer<String> callback = (String str) -> {
        System.out.println(str);
        buttonBlock(false);
        if (str.contains("500") || str.contains("404"))  { dialogStatus.setCancel(true); }
        Platform.runLater(
                () -> {
                    Stage stage = (Stage) cancelReceipt.getScene().getWindow();
                    stage.close();
                }
        );
    };

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
            buttonBlock(true);
               HttpRequest getSuppliesData = HttpUtils.DELETE(DOMAIN + "/api/receipts/" + id, "Authorization", "Bearer " + currentUser.getToken());
                HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), callback, () -> {
                    dialogStatus.setCancel(false);
                    Stage stage = (Stage) cancelReceipt.getScene().getWindow();
                    stage.close();
                });

        });

        receiptField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue!=null && newValue.equals(id))   buttonBlock(false);
            else buttonBlock(true);
        });

        buttonBlock(true);

    }

    public void setId (String x) {
        id = x;
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
