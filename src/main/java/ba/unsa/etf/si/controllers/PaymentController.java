package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.models.Product;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

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
    }

    public void setTotalAmount(String totalAmount) {
        totalAmountField.setText(totalAmount + " KM");
    }

    public void setProducts(ArrayList<Product> products) {
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
