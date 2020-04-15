package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.ReceiptItem;
import ba.unsa.etf.si.models.enums.PaymentMethod;
import ba.unsa.etf.si.utility.payment.CreditCardServer;
import ba.unsa.etf.si.utility.image.QRUtils;
import ba.unsa.etf.si.utility.payment.CreditInfoReceiver;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
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
    public void initialize() {

    }

    public void setQRTypeAndCode(Receipt receipt, boolean isDynamic) {
        if (isDynamic) {

            StringBuilder receiptItems = new StringBuilder();
            List<ReceiptItem> receiptItemArrayList = receipt.getReceiptItems();
            for (ReceiptItem receiptItem : receiptItemArrayList) {
                int itemQuantity = (int) receiptItem.getQuantity();
                receiptItems.append(receiptItem.getName()).append(" (").append(itemQuantity).append(")");
                receiptItems.append(",");
            }
            String receiptItemsString = StringUtils.chop(receiptItems.toString());

            qrCodeString = "{\n" +
                    "\"cashRegisterId\": " + App.getCashRegisterID() + ",\n" +
                    "\"officeId\": " + App.getBranchID() + ",\n" +
                    "\"businessName\": \"BINGO\",\n" +
                    "\"receiptId\": \"" + receipt.getTimestampID() + "\",\n" +
                    "\"service\": \"" + receiptItemsString + "\",\n" +
                    "\"totalPrice\": " + receipt.getAmount() + "\n" +
                    "}";
        } else {
            qrCodeString = "{\n" +
                    "\"cashRegisterId\": " + App.getCashRegisterID() + ",\n" +
                    "\"officeId\": " + App.getBranchID() + ",\n" +
                    "\"businessName\": \"BINGO\"\n" +
                    "}";
        }
    }

    public void processPayment(PaymentMethod paymentMethod, PaymentController paymentController, double totalAmount) {
        this.paymentMethod = paymentMethod;
        this.paymentController = paymentController;
        if (paymentMethod == PaymentMethod.CASH || paymentMethod == PaymentMethod.PAY_APP)
            fillProgressBar(true, "");
        else
            fetchCreditCardInfo(totalAmount);
    }

    public void fetchCreditCardInfo(Double totalAmount) {
        CreditInfoReceiver creditInfoReceiver = new CreditInfoReceiver(this::fillProgressBar, totalAmount);
        CreditCardServer creditCardServer = new CreditCardServer(5000, creditInfoReceiver);
        new Thread(creditCardServer).start();
    }

    public void fillProgressBar(boolean isValid, String creditCardInfo) {
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i++) {
                    double progress = i * 0.01;
                    Platform.runLater(() -> paymentProgress.setProgress(progress));
                    Thread.sleep(15);
                }

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

                            ).thenRunAsync(() -> {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).thenRunAsync(() -> paymentController.pollForResponse())
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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
}
