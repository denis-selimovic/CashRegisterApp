package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.status.PaymentMethod;
import ba.unsa.etf.si.server.CreditCardServer;
import ba.unsa.etf.si.utility.QRUtils;
import ba.unsa.etf.si.utility.interfaces.MessageReceiver;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.commons.validator.routines.CodeValidator;
import org.apache.commons.validator.routines.CreditCardValidator;
import org.json.JSONObject;

import java.time.LocalDate;

public class PaymentProcessingController {
    @FXML
    private JFXProgressBar paymentProgress;
    @FXML
    private Text infoText, statusText;
    @FXML
    private ImageView qrCode;

    private PaymentController paymentController;
    private PaymentMethod paymentMethod;

    private class CreditInfoReceiver implements MessageReceiver {

        Double priceToPay;

        public CreditInfoReceiver(Double priceToPay) {
            this.priceToPay = priceToPay;
        }

        @Override
        public void onMessageReceived(String msg) {
            JSONObject infoJson = new JSONObject(msg);
            fillProgressBar(checkIfValid(infoJson));
        }

        private boolean checkIfValid(JSONObject infoJson) {
            try {
                CreditCardValidator creditCardValidator = new CreditCardValidator(CreditCardValidator.MASTERCARD + CreditCardValidator.VISA);
                String[] expiryDate = infoJson.getString("expiryDate").split("/");
                int expiryMonth = Integer.parseInt(expiryDate[0]);
                int expiryYear = Integer.parseInt(expiryDate[1]);


                return LocalDate.now().compareTo(LocalDate.of(expiryYear, expiryMonth, 28)) > 0 && infoJson.getString("cvvCode").length() == 3
                        && creditCardValidator.isValid(infoJson.getString("creditCardNumber")) && infoJson.getDouble("balance") >= priceToPay;
            } catch (Exception e) {
                System.out.println("IZUZETAK: " + e.getMessage());
                return false;
            }
        }
    }

    @FXML
    public void initialize() {

    }

    public void processPayment(PaymentMethod paymentMethod, PaymentController paymentController, double totalAmount) {
        this.paymentMethod = paymentMethod;
        this.paymentController = paymentController;

        if (paymentMethod == PaymentMethod.CASH || paymentMethod == PaymentMethod.PAY_APP)
            fillProgressBar(true);
        else
            fetchCreditCardInfo(totalAmount);
    }

    public void fetchCreditCardInfo(Double totalAmount) {
        CreditInfoReceiver creditInfoReceiver = new CreditInfoReceiver(totalAmount);
        CreditCardServer creditCardServer = new CreditCardServer(5000, creditInfoReceiver);
        new Thread(creditCardServer).start();
    }

    public void fillProgressBar(boolean isValid) {
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i++) {
                    double progress = i * 0.01;
                    Platform.runLater(() -> {
                        paymentProgress.setProgress(progress);
                    });
                    Thread.sleep(15);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            statusText.setText("Success!");
            statusText.setFill(Color.GREEN);

            if (paymentMethod == PaymentMethod.CASH)
                paymentController.saveReceipt();

            if (paymentMethod == PaymentMethod.PAY_APP) {
                try {
                    qrCode.setImage(QRUtils.getQRImage("test code", 300, 300));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                paymentProgress.setVisible(false);
            }

            if (paymentMethod == PaymentMethod.CREDIT_CARD)
                if (!isValid) {
                    infoText.setText("Credit card is not valid!");
                    paymentProgress.setVisible(false);
                } else
                    paymentController.saveReceipt();
        }).start();
    }
}
