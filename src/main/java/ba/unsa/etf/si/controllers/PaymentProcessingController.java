package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.utility.QRUtils;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class PaymentProcessingController {
    @FXML
    private JFXProgressBar paymentProgress;
    @FXML
    private Text infoText;
    @FXML
    private ImageView qrCode;

    @FXML
    public void initialize() {

    }

    public void setPaymentSuccessful(boolean pay_app) {
        if (pay_app) {
            try {
                qrCode.setImage(QRUtils.getQRImage("test code", 300, 300));
            } catch (Exception e) {
                e.printStackTrace();
            }
            paymentProgress.setVisible(false);
        } else {
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

                infoText.setText("Success!");
                infoText.setFill(Color.GREEN);
            }).start();
        }
    }

    public void setPaymentUnsuccessful(boolean pay_app) {
        if(pay_app){

        }
    }
}
