package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.status.PaymentMethod;
import ba.unsa.etf.si.models.status.ReceiptStatus;
import ba.unsa.etf.si.persistance.ReceiptRepository;
import ba.unsa.etf.si.utility.HttpUtils;
import ba.unsa.etf.si.utility.interfaces.ConnectivityObserver;
import ba.unsa.etf.si.utility.interfaces.PDFGenerator;
import ba.unsa.etf.si.utility.interfaces.PaymentProcessingListener;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Optional;

import static ba.unsa.etf.si.App.DOMAIN;
import static ba.unsa.etf.si.App.centerStage;
import static ba.unsa.etf.si.controllers.PrimaryController.currentUser;


public class PaymentController implements PaymentProcessingListener, ConnectivityObserver {

    @FXML
    private Button qrCodePayment;
    @FXML
    private TextField amountDisplay, totalAmountField;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private HBox firstRow, secondRow, thirdRow, fourthRow;
    @FXML
    private Button doubleZeroKey, plusKey, minusKey,
            equalKey, backspaceKey;
    @FXML
    private JFXToggleButton qrCodeType;

    Receipt currentReceipt;
    private PaymentProcessingListener paymentProcessingListener;
    private PDFGenerator pdfGenerator;
    private final ReceiptRepository receiptRepository = new ReceiptRepository();
    private boolean add = true;


    public PaymentController() {
        App.connectivity.subscribe(this);
    }

    @Override
    public void onPaymentProcessed(boolean isValid) {
        Platform.runLater(() -> ((Stage) cancelButton.getScene().getWindow()).close());
        paymentProcessingListener.onPaymentProcessed(isValid);
        if(add) {
            new Thread(() -> {
                currentReceipt.setReceiptStatus(ReceiptStatus.PAID);
                receiptRepository.add(currentReceipt);
                pdfGenerator.generatePDF(currentReceipt);
            }).start();
        }
        add = true;
    }

    public void setPaymentProcessingListener(PaymentProcessingListener paymentProcessingListener) {
        this.paymentProcessingListener = paymentProcessingListener;
    }


    public void setPDFGenerator(PDFGenerator generator) {
        this.pdfGenerator = generator;
    }

    @Override
    public void setOfflineMode() {
        qrCodePayment.setDisable(true);
    }

    @Override
    public void setOnlineMode() {
        qrCodePayment.setDisable(false);
    }

    private enum Op {NOOP, ADD, SUBTRACT}

    private class Calculator {
        private StringProperty value = new SimpleStringProperty("");
        private boolean decimalMode = false;
        private boolean firstDecimal = true;
        private Double stackValue = 0.0;

        private Op stackOp = Op.NOOP;

        private Calculator() {
            ArrayList<Node> keys = new ArrayList<>();
            keys.addAll(firstRow.getChildrenUnmodifiable());
            keys.addAll(secondRow.getChildrenUnmodifiable());
            keys.addAll(thirdRow.getChildrenUnmodifiable());
            keys.addAll(fourthRow.getChildrenUnmodifiable());

            amountDisplay.textProperty().bindBidirectional(value);

            initializeNumericKeys(keys);

            plusKey.setOnMouseClicked(mouseEvent -> {
                handleOperator();
                stackOp = Op.ADD;
                value.setValue("");
                setDecimalMode(false);

            });

            minusKey.setOnMouseClicked(mouseEvent -> {
                handleOperator();
                stackOp = Op.SUBTRACT;
                value.setValue("");
                setDecimalMode(false);
            });

            equalKey.setOnMouseClicked(mouseEvent -> {
                if (stackValue.equals(0.0))
                    value.setValue("");
                else {
                    double valueOnDisplay;
                    try {
                        valueOnDisplay = Double.parseDouble(value.getValue());
                    } catch (NumberFormatException nfe) {
                        valueOnDisplay = 0.0;
                    }

                    double result = 0.0;
                    if (stackOp == Op.ADD)
                        result = stackValue + valueOnDisplay;
                    else if (stackOp == Op.SUBTRACT)
                        result = stackValue - valueOnDisplay;

                    value.setValue(String.valueOf(Math.round(result * 100.0) / 100.0));
                }

                stackOp = Op.NOOP;
                stackValue = 0.0;
            });

            doubleZeroKey.setOnMouseClicked(mouseEvent -> {
                setDecimalMode(!decimalMode);
                try {
                    if (decimalMode && !amountDisplay.getText().contains("."))
                        value.setValue(value.getValue() + ".00");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });

            backspaceKey.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2) {
                    value.setValue("");
                    setDecimalMode(false);
                } else {
                    try {
                        char charToBeDeleted = value.getValue().charAt(value.getValue().length() - 1);
                        if (charToBeDeleted == '.')
                            setDecimalMode(false);
                        value.setValue(StringUtils.chop(value.getValue()));
                    } catch (Exception e) {
                        value.setValue("");
                        System.out.println(e.getMessage());
                    }
                }
            });
        }

        private void initializeNumericKeys(ArrayList<Node> numberKeys) {
            for (Node key : numberKeys) {
                Button calcButton = (Button) key;
                String calcButtonText = calcButton.getText();

                if (calcButtonText.matches("[0-9]")) {
                    char number = calcButtonText.charAt(0);
                    calcButton.setOnMouseClicked(mouseEvent -> {
                        int point = -1;
                        if (!value.getValue().isEmpty())
                            point = amountDisplay.getText().indexOf(".");

                        if (decimalMode && point != -1) {
                            StringBuilder stringBuilder = new StringBuilder(value.getValue());
                            try {
                                stringBuilder.setCharAt(point + (firstDecimal ? 1 : 2), number);
                            } catch (Exception e) {
                                stringBuilder.append(number);
                            }
                            firstDecimal = !firstDecimal;
                            value.setValue(stringBuilder.toString());
                        } else {
                            if (point == -1)
                                value.setValue(value.getValue() + number);
                            else {
                                StringBuilder stringBuilder = new StringBuilder(value.getValue());
                                stringBuilder.insert(point, number);
                                value.setValue(stringBuilder.toString());
                            }
                        }
                    });
                }
            }
        }

        private void setDecimalMode(boolean isDecimal) {
            decimalMode = isDecimal;
            doubleZeroKey.setOpacity(1 - (decimalMode ? 0.4 : 0));
        }

        private void handleOperator() {
            if (value.getValue().isEmpty())
                value.setValue("0");
            else if (stackOp == Op.ADD)
                stackValue += Double.parseDouble(value.getValue());
            else if (stackOp == Op.SUBTRACT)
                stackValue -= Double.parseDouble(value.getValue());
            else
                stackValue = Double.parseDouble(value.getValue());
        }
    }

    @FXML
    public void initialize() {
        new Calculator();

        qrCodeType.setOnAction(actionEvent -> {
            if (qrCodeType.isSelected())
                qrCodeType.setText("Dynamic QR");
            else
                qrCodeType.setText("Static QR");
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

    public void askForReceiptPrint() {
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

    public void saveReceipt() {
        // Request body for the POST login (raw JSON)
        HttpRequest.BodyPublisher bodyPublisher =
                HttpRequest.BodyPublishers.ofString(currentReceipt.toString());

        HttpRequest saveReceiptRequest = HttpUtils.POST(bodyPublisher, DOMAIN + "/api/receipts",
                "Content-Type", "application/json", "Authorization", "Bearer " + currentUser.getToken());

        String response = "";
        try {
            response = HttpUtils.sendSync(saveReceiptRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            new Thread(() -> {
                receiptRepository.add(currentReceipt);
                add = false;
            }).start();
            return;
        }
        JSONObject json = new JSONObject(response);
        if(json.getInt("statusCode") != 200) throw new RuntimeException();
    }

    public void pollForResponse() {
        //polling

        HttpRequest GET = HttpUtils.GET(DOMAIN + "/api/receipts/" + getReceipt().getTimestampID(), "Authorization", "Bearer " + currentUser.getToken());
        String response = HttpUtils.sendSync(GET, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response);
        while (json.getString("status").equals("PENDING")) {
            response = HttpUtils.sendSync(GET, HttpResponse.BodyHandlers.ofString());
            json = new JSONObject(response);
        }
        if(!json.getString("status").equals("PAID")) throw new RuntimeException();
    }

    public void displayPaymentInformation(boolean positiveResponse, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE);

        if (positiveResponse)
            alert.setAlertType(Alert.AlertType.INFORMATION);
        else
            alert.setAlertType(Alert.AlertType.WARNING);

        alert.setTitle("Response Dialog");
        alert.setHeaderText(message);
    }

    public PaymentProcessingController loadPaymentProcessing() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/paymentProcessing.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Hold up");
            centerStage(stage, 600, 400);
            stage.setScene(scene);
            stage.show();

            return fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void cashButtonClick() {
        currentReceipt.setPaymentMethod(PaymentMethod.CASH);

        PaymentProcessingController paymentProcessingController = loadPaymentProcessing();
        if (paymentProcessingController == null) {
            displayPaymentInformation(false, "Something went wrong.\nPlease try again.");
            return;
        }

        paymentProcessingController.processPayment(PaymentMethod.CASH, this, Double.parseDouble(totalAmountField.getText().replaceAll(",", ".")));
        //askForReceiptPrint();
    }

    public void cardButtonClick() {
        currentReceipt.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        PaymentProcessingController paymentProcessingController = loadPaymentProcessing();
        if (paymentProcessingController == null) {
            displayPaymentInformation(false, "Something went wrong.\nPlease try again.");
            return;
        }

        paymentProcessingController.processPayment(PaymentMethod.CREDIT_CARD, this, Double.parseDouble(totalAmountField.getText().replaceAll(",", ".")));
        //askForReceiptPrint();
    }

    public void qrCodeButtonClick() {
        currentReceipt.setPaymentMethod(PaymentMethod.PAY_APP);
        //saveReceipt();

        PaymentProcessingController paymentProcessingController = loadPaymentProcessing();
        if (paymentProcessingController == null) {
            displayPaymentInformation(false, "Something went wrong.\nPlease try again.");
            return;
        }

        paymentProcessingController.setQRTypeAndCode(currentReceipt, qrCodeType.isSelected());
        paymentProcessingController.processPayment(PaymentMethod.PAY_APP, this, Double.parseDouble(totalAmountField.getText().replaceAll(",", ".")));
        //askForReceiptPrint();
    }

    public void cancelButtonClick() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}
