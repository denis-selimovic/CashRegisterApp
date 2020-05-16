package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.enums.PaymentMethod;
import ba.unsa.etf.si.models.enums.ReceiptStatus;
import ba.unsa.etf.si.persistance.repository.ReceiptRepository;
import ba.unsa.etf.si.routes.ReceiptRoutes;
import ba.unsa.etf.si.interfaces.ConnectivityObserver;
import ba.unsa.etf.si.interfaces.PDFGenerator;
import ba.unsa.etf.si.interfaces.PaymentProcessingListener;
import ba.unsa.etf.si.utility.javafx.CustomFXMLLoader;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.NotificationUtils;
import ba.unsa.etf.si.utility.payment.Calculator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;
import static ba.unsa.etf.si.controllers.PrimaryController.currentUser;
import static ba.unsa.etf.si.utility.javafx.StageUtils.centerStage;
import static ba.unsa.etf.si.utility.javafx.StageUtils.setStage;

public class PaymentController implements PaymentProcessingListener, ConnectivityObserver {

    @FXML private Button qrCodePayment;
    @FXML private TextField amountDisplay, totalAmountField;
    @FXML private JFXButton cancelButton;
    @FXML private HBox firstRow, secondRow, thirdRow, fourthRow;
    @FXML private Button doubleZeroKey, plusKey, minusKey, equalKey, backspaceKey;
    @FXML private JFXToggleButton qrCodeType;

    private Receipt currentReceipt;
    private PaymentProcessingListener paymentProcessingListener;
    private PDFGenerator pdfGenerator;
    private final ReceiptRepository receiptRepository = new ReceiptRepository();
    private boolean add = true;

    public PaymentController() {
        App.connectivity.subscribe(this);
    }

    public PaymentController(PaymentProcessingListener paymentProcessingListener, PDFGenerator pdfGenerator, Receipt currentReceipt) {
        App.connectivity.subscribe(this);
        this.paymentProcessingListener = paymentProcessingListener;
        this.pdfGenerator = pdfGenerator;
        this.currentReceipt = currentReceipt;
    }

    @Override
    public void onPaymentProcessed(boolean isValid) {
        Platform.runLater(() -> ((Stage) cancelButton.getScene().getWindow()).close());
        paymentProcessingListener.onPaymentProcessed(isValid);
        if(add) {
            new Thread(() -> {
                currentReceipt.setReceiptStatus(ReceiptStatus.PAID);
                receiptRepository.add(currentReceipt);
            }).start();
        }
        if(isValid) pdfGenerator.generatePDF(currentReceipt);
        add = true;
    }

    @Override
    public void setOfflineMode() {
        qrCodePayment.setDisable(true);
    }

    @Override
    public void setOnlineMode() {
        qrCodePayment.setDisable(false);
    }

    @FXML
    public void initialize() {
        new Calculator(firstRow, secondRow, thirdRow, fourthRow, doubleZeroKey, plusKey, minusKey, equalKey, backspaceKey, amountDisplay);

        qrCodeType.setOnAction(actionEvent -> {
            if (qrCodeType.isSelected()) qrCodeType.setText("Dynamic QR");
            else qrCodeType.setText("Static QR");
        });

        amountDisplay.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d?\\d?)?")) {
                amountDisplay.setText(oldValue);
            }
        });
        amountDisplay.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                double total = Double.parseDouble(totalAmountField.getText());
                double tenderAmount = Double.parseDouble(amountDisplay.getText());
                if (tenderAmount >= total)
                    amountDisplay.setText(String.valueOf(Math.round((tenderAmount - total) * 100) / 100.0));
            }
        });
    }

    public void setReceipt(Receipt receipt) {
        this.currentReceipt = receipt;
    }

    public Receipt getReceipt() {
        return this.currentReceipt;
    }

    public void setTotalAmount(String totalAmount) {
        totalAmountField.setText(totalAmount);
    }

    public void saveReceipt() {
        try {
            String response = ReceiptRoutes.sendReceiptSync(currentReceipt, currentUser.getToken());
            if(new JSONObject(response).getInt("statusCode") != 200) throw new RuntimeException();
        } catch (Exception e) {
            new Thread(() -> {
                receiptRepository.add(currentReceipt);
                add = false;
            }).start();
        }
    }

    public PaymentProcessingController loadPaymentProcessing() {
        Stage stage = new Stage();
        CustomFXMLLoader<PaymentProcessingController> customFXMLLoader = FXMLUtils.getCustomLoader("fxml/paymentProcessing.fxml", PaymentProcessingController.class);
        setStage(stage, "Hold up", false, StageStyle.UNDECORATED, Modality.APPLICATION_MODAL);
        centerStage(stage, 600, 400);
        stage.setScene(new Scene(customFXMLLoader.root));
        stage.show();
        return customFXMLLoader.controller;
    }

    public void cashButtonClick() {
        currentReceipt.setPaymentMethod(PaymentMethod.CASH);
        PaymentProcessingController paymentProcessingController = loadPaymentProcessing();
        if (paymentProcessingController == null) {
            NotificationUtils.showAlert("Response Dialog", "Something went wrong.\n Try again.", Alert.AlertType.WARNING, ButtonType.OK);
            return;
        }
        paymentProcessingController.processPayment(PaymentMethod.CASH, this, Double.parseDouble(totalAmountField.getText().replaceAll(",", ".")));
    }

    public void cardButtonClick() {
        currentReceipt.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        PaymentProcessingController paymentProcessingController = loadPaymentProcessing();
        if (paymentProcessingController == null) {
            NotificationUtils.showAlert("Response Dialog", "Something went wrong.\n Try again.", Alert.AlertType.WARNING, ButtonType.OK);
            return;
        }
        paymentProcessingController.processPayment(PaymentMethod.CREDIT_CARD, this, Double.parseDouble(totalAmountField.getText().replaceAll(",", ".")));
    }

    public void qrCodeButtonClick() {
        currentReceipt.setPaymentMethod(PaymentMethod.PAY_APP);
        PaymentProcessingController paymentProcessingController = loadPaymentProcessing();  //loada se prozor za procesiranje
        if (paymentProcessingController == null) {
            NotificationUtils.showAlert("Response Dialog", "Something went wrong.\n Try again.", Alert.AlertType.WARNING, ButtonType.OK);
            return;
        }
        paymentProcessingController.setQRTypeAndCode(currentReceipt, qrCodeType.isSelected());
        paymentProcessingController.processPayment(PaymentMethod.PAY_APP, this, Double.parseDouble(totalAmountField.getText().replaceAll(",", "."))); //1.
    }

    public void cancelButtonClick() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}
