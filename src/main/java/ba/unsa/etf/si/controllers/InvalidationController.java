package ba.unsa.etf.si.controllers;


import ba.unsa.etf.si.App;
import ba.unsa.etf.si.gui.factory.DisabledDateCellFactory;
import ba.unsa.etf.si.gui.factory.ReceiptCellFactory;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.utility.HttpUtils;
import ba.unsa.etf.si.utility.PDFCashierBalancingFactory;
import ba.unsa.etf.si.utility.interfaces.ReceiptLoader;
import ba.unsa.etf.si.utility.json.ProductUtils;
import ba.unsa.etf.si.utility.json.ReceiptUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ba.unsa.etf.si.App.DOMAIN;
import static ba.unsa.etf.si.controllers.PrimaryController.currentUser;

public class InvalidationController {

    @FXML
    private DatePicker datePicker;
    @FXML
    private JFXButton cancelPicker;
    @FXML
    private JFXListView<Receipt> receiptList;
    @FXML
    private TextField income;

    private Receipt selectedReceipt = new Receipt();
    private List<Receipt> receipts = new ArrayList<>();
    public static List<Product> productList = new ArrayList<>();
    private boolean isCloseOut = false;
    private final ReceiptLoader receiptLoader;
    String TOKEN = currentUser.getToken();

    public InvalidationController(boolean isCloseOut, ReceiptLoader receiptLoader) {
        this.isCloseOut = isCloseOut;
        this.receiptLoader = receiptLoader;
    }

    public InvalidationController(ReceiptLoader receiptLoader) {
        this.receiptLoader = receiptLoader;
    }

    Consumer<String> callback = (String str) -> {
        receipts = ReceiptUtils.getReceipts(new JSONArray(str), productList);
        Platform.runLater(() -> receiptList.setItems(FXCollections.observableList(receipts)));

        if (isCloseOut) {
            PDFCashierBalancingFactory pdfCashierBalancingFactory = new PDFCashierBalancingFactory(receiptList.getItems());
            pdfCashierBalancingFactory.generatePdf();
            receiptList.setDisable(true);
        } else {
            receiptList.setOnMouseClicked(new EventHandler<>() {
                @Override
                public void handle(MouseEvent click) {
                    if (click.getClickCount() == 2) {
                        selectedReceipt = receiptList.getSelectionModel().getSelectedItem();
                        receiptList.getSelectionModel().clearSelection();
                        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/dialog.fxml"));
                        Parent parent = null;
                        try {
                            parent = fxmlLoader.load();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        DialogController dialogController = fxmlLoader.<DialogController>getController();
                        dialogController.setId(selectedReceipt.getTimestampID());

                        Scene scene = new Scene(parent);
                        Stage stage = new Stage();

                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(scene);
                        stage.showAndWait();
                        dialogHandler(dialogController);
                    }
                }
            });
        }
    };

    Consumer<String> callback1 = (String str) -> {
        productList = ProductUtils.getProductsFromJSON(str);
        HttpRequest getSuppliesData = HttpUtils.GET(DOMAIN + "/api/receipts?cash_register_id=" + App.getCashRegisterID(), "Authorization", "Bearer " + TOKEN);

        HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), callback, () -> {
            System.out.println("Something went wrong.");
        });

    };

    @FXML
    public void initialize() {
        receiptList.setCellFactory(new ReceiptCellFactory());
        HttpRequest getSuppliesData = HttpUtils.GET(DOMAIN + "/api/products", "Authorization", "Bearer " + TOKEN);
        HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), callback1, () -> {
            System.out.println("Something went wrong.");
        });

        datePicker.setConverter(new StringConverter<>() {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                }
                return "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) return LocalDate.parse(string, dateFormatter);
                return null;
            }
        });

        datePicker.setDayCellFactory(new DisabledDateCellFactory());
        datePicker.valueProperty().addListener((observableValue, localDate, newLocalDate) -> {
            receiptList.setItems(sort(getDate()));
        });

        cancelPicker.setOnAction(e -> {
            datePicker.setValue(null);
            receiptList.setItems(sort(getDate()));
        });

        receiptList.itemsProperty().addListener((observableValue, receipts, t1) -> {
            Platform.runLater(() -> income.setText(getIncomeAsString()));
        });
    }

    private double getIncome() {
        return receiptList.getItems().stream().mapToDouble(Receipt::getAmount).sum();
    }

    private String getIncomeAsString() {
        return BigDecimal.valueOf(getIncome()).setScale(2, RoundingMode.HALF_UP).toString();
    }


    private void dialogHandler(DialogController dialogController) {
        DialogController.DialogStatus stat = dialogController.getStatus();
        if (stat.isCancel()) {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/informationDialog.fxml"));
            Parent parent = null;
            try {
                parent = fxmlLoader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
            InfoDialogController infoDialogController = fxmlLoader.<InfoDialogController>getController();
            if (stat.getStatus() == 505) {
                infoDialogController.setWarning();
                infoDialogController.setInformationLabel("Receipt couldn't been cancelled due to server error!");
            }

            Scene scene = new Scene(parent);
            Stage stage = new Stage();

            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            HttpRequest getSuppliesData = HttpUtils.GET(DOMAIN + "/api/products", "Authorization", "Bearer " + TOKEN);

            HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), callback1, () -> {
                System.out.println("Something went wrong.");
            });
        } else if (stat.isRevert()) {
            receiptLoader.onReceiptLoaded(selectedReceipt);
        }
    }

    private static boolean compareDates(LocalDate picker, LocalDate receipt) {
        return (picker == null) || picker.isEqual(receipt);
    }

    private LocalDate getDate() {
        return datePicker.getValue();
    }

    private ObservableList<Receipt> sort(LocalDate date) {
        return receipts.stream().filter(r -> compareDates(date, LocalDate.from(r.getDate())))
                .collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
    }

}
