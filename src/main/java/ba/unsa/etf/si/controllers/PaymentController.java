package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.Product;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

public class PaymentController {

    @FXML
    private TextField amountDisplay, totalAmountField;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private HBox firstRow, secondRow, thirdRow, fourthRow;
    @FXML
    private Button doubleZeroKey, plusKey, minusKey,
            equalKey, backspaceKey;


    ArrayList<Product> products;

    public void setTotalAmount(String totalAmount){
        totalAmountField.setText(totalAmount + " KM");
    }

    public void setProducts(ArrayList<Product> products){
        this.products = products;
    }

    public void askForReceipt() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Would you like to print out a receipt?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.YES) {
                System.out.println("Yes");
            } else
                alert.close();
        }
    }

    public void cashButtonClick() {
        askForReceipt();
    }

    public void cardButtonClick() {
        askForReceipt();
    }

    public void qrCodeButtonClick() {
        askForReceipt();
    }

    public void cancelButtonClick() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}
