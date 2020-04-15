package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.enums.PaymentMethod;
import ba.unsa.etf.si.utility.image.QRUtils;
import ba.unsa.etf.si.utility.json.QRJsonUtils;
import ba.unsa.etf.si.utility.payment.CreditCardServer;
import ba.unsa.etf.si.utility.payment.CreditInfoReceiver;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.concurrent.CompletableFuture;

public class PaymentProcessingController {

    @FXML private Text txt;
    @FXML private JFXProgressBar paymentProgress;
    @FXML private Text infoText, statusText;
    @FXML private ImageView qrCode;

    private String qrCodeString;
    private PaymentController paymentController;
    private PaymentMethod paymentMethod;

    @FXML
    public void initialize() { }

    public void setQRTypeAndCode(Receipt receipt, boolean isDynamic) {
        qrCodeString = (isDynamic) ? QRJsonUtils.getDynamicQRCode(receipt) : QRJsonUtils.getStaticQRCode();
    }

    public void processPayment(PaymentMethod paymentMethod, PaymentController paymentController, double totalAmount) {
        this.paymentMethod = paymentMethod;
        this.paymentController = paymentController;
        if (paymentMethod == PaymentMethod.CASH || paymentMethod == PaymentMethod.PAY_APP) fillProgressBar(true, "");
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

            if (paymentMethod == PaymentMethod.CASH) {
                CompletableFuture.runAsync(() -> paymentController.saveReceipt())
                        .handle((obj, ex) -> {
                            showMessage(ex == null);
                            return null;
                        });
            }
            if (paymentMethod == PaymentMethod.PAY_APP) {
                CompletableFuture.runAsync(() -> paymentController.saveReceipt())
                        .thenRunAsync(() -> Platform.runLater(() -> {
                                    try {
                                        paymentProgress.setVisible(false);
                                        qrCode.setVisible(true);
                                        qrCode.setImage(QRUtils.getQRImage(qrCodeString, 300, 300));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                })

                        ).thenRunAsync(() -> sleep(100000)).thenRunAsync(() -> paymentController.pollForResponse())
                        .handle((obj, ex) -> {
                            showMessage(ex == null);
                            return null;
                        });
            }
            if (paymentMethod == PaymentMethod.CREDIT_CARD) {
                if (!isValid) {
                    infoText.setText(creditCardInfo);
                    paymentProgress.setVisible(false);
                    showMessage(false);
                } else {
                    CompletableFuture.runAsync(() -> paymentController.saveReceipt())
                            .handle((obj, ex) -> {
                                showMessage(ex == null);
                                return null;
                            });
                }
            }
        }).start();
    }

    private void loading() {
        for (int i = 0; i <= 100; i++) {
            double progress = i * 0.01;
            Platform.runLater(() -> paymentProgress.setProgress(progress));
            sleep(15);
        }
    }

    private void showMessage(boolean valid) {
        txt.setText("Processing finished!");
        if(valid) statusText.setText("Transaction successful!");
        else statusText.setText("Transaction failed! Please try again!");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
