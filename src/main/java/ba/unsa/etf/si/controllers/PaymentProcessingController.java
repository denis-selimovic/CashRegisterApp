package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.enums.PaymentMethod;
import ba.unsa.etf.si.utility.image.QRUtils;
import ba.unsa.etf.si.utility.modelutils.QRJsonUtils;
import ba.unsa.etf.si.utility.payment.CreditCardServer;
import ba.unsa.etf.si.utility.payment.CreditInfoReceiver;
import ba.unsa.etf.si.utility.payment.Payment;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.function.BiFunction;

public class PaymentProcessingController {

    @FXML private Text txt;
    @FXML private JFXProgressBar paymentProgress;
    @FXML private Text infoText, statusText;
    @FXML private ImageView qrCode;

    private String qrCodeString;
    private PaymentController paymentController;
    private PaymentMethod paymentMethod;

    private final BiFunction<? super Void, Throwable, ? super  Void> handle = (obj, ex) -> {
        showMessage(ex == null);
        return null;
    };

    @FXML
    public void initialize() { }

    public void setQRTypeAndCode(Receipt receipt, boolean isDynamic) {
        new Thread(() -> qrCodeString = (isDynamic) ? QRJsonUtils.getDynamicQRCode(receipt) : QRJsonUtils.getStaticQRCode()).start();
    }

    public void processPayment(PaymentMethod paymentMethod, PaymentController paymentController, double totalAmount) {
        this.paymentMethod = paymentMethod;
        this.paymentController = paymentController;
        if (paymentMethod == PaymentMethod.CASH || paymentMethod == PaymentMethod.PAY_APP) fillProgressBar(true, "");  //2. daljnje procesiranje paymenta
        else fetchCreditCardInfo(totalAmount);
    }

    public void fetchCreditCardInfo(Double totalAmount) {
        CreditInfoReceiver creditInfoReceiver = new CreditInfoReceiver(this::fillProgressBar, totalAmount);
        CreditCardServer creditCardServer = new CreditCardServer(5000, creditInfoReceiver);
        new Thread(creditCardServer).start();
    }

    public void fillProgressBar(boolean isValid, String creditCardInfo) {
        new Thread(() -> {
            loading();
            switch (paymentMethod) {
                case CASH -> Payment.cashPayment(paymentController::saveReceipt, handle);
                case PAY_APP -> Payment.qrPayment(this::setQRImage, paymentController::pollForResponse,() -> sleep(5000),  handle);
                case CREDIT_CARD -> Payment.creditCardPayment(isValid, () -> showCreditCardInfo(creditCardInfo), paymentController::saveReceipt, handle);
            }
        }).start();
    }

    private void setQRImage() {
        Platform.runLater(() -> {
            paymentProgress.setVisible(false);
            qrCode.setVisible(true);
            qrCode.setImage(QRUtils.getQRImage(qrCodeString, 300, 300));
        });
    }

    private void loading() {
        for (int i = 0; i <= 100; i++) {
            double progress = i * 0.01;
            Platform.runLater(() -> paymentProgress.setProgress(progress));
            sleep(15);
        }
    }

    private void showCreditCardInfo(String creditCardInfo) {
        infoText.setText(creditCardInfo);
        paymentProgress.setVisible(false);
        showMessage(false);
    }

    private void showMessage(boolean valid) {
        txt.setText("Processing finished!");
        if(valid) statusText.setText("Transaction successful!");
        else statusText.setText("Transaction failed! Please try again!");
        sleep(5000);
        Platform.runLater(() -> ((Stage) statusText.getScene().getWindow()).close());
        paymentController.onPaymentProcessed(valid);
    }

    private void sleep(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
