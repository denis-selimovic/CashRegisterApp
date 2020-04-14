package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.gui.factory.DisabledDateCellFactory;
import ba.unsa.etf.si.gui.factory.ReceiptCellFactory;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.utility.javafx.JavaFXUtils;
import ba.unsa.etf.si.utility.pdf.PDFCashierBalancingFactory;
import ba.unsa.etf.si.utility.date.DateConverter;
import ba.unsa.etf.si.utility.date.DateUtils;
import ba.unsa.etf.si.utility.interfaces.ReceiptLoader;
import ba.unsa.etf.si.utility.javafx.CustomFXMLLoader;
import ba.unsa.etf.si.utility.json.ProductUtils;
import ba.unsa.etf.si.utility.json.ReceiptUtils;
import ba.unsa.etf.si.utility.routes.ProductRoutes;
import ba.unsa.etf.si.utility.routes.ReceiptRoutes;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    private final Consumer<String> receiptsCallback = (String str) -> {
        receipts = ReceiptUtils.getReceipts(new JSONArray(str), productList);
        Platform.runLater(() -> receiptList.setItems(FXCollections.observableList(receipts)));
        if (isCloseOut) {
            PDFCashierBalancingFactory pdfCashierBalancingFactory = new PDFCashierBalancingFactory(receiptList.getItems());
            pdfCashierBalancingFactory.generatePdf();
            receiptList.setDisable(true);
        }
    };

    private final Consumer<String> productsCallback = str -> {
        productList = ProductUtils.getProductsFromJSON(str);
        ReceiptRoutes.getReceipts(TOKEN, receiptsCallback, () -> System.out.println("Could not load receipts!"));
    };

    @FXML
    public void initialize() {
        ProductRoutes.getProducts(TOKEN, productsCallback, () -> System.out.println("Could not load products!"));

        receiptList.setCellFactory(new ReceiptCellFactory());
        datePicker.setConverter(new DateConverter());
        datePicker.setDayCellFactory(new DisabledDateCellFactory());
        datePicker.valueProperty().addListener((observableValue, localDate, newLocalDate) -> {
            receiptList.setItems(FXCollections.observableList(DateUtils.sortByDate(getDate(), receipts)));
        });

        cancelPicker.setOnAction(e -> {
            datePicker.setValue(null);
            receiptList.setItems(FXCollections.observableList(DateUtils.sortByDate(getDate(), receipts)));
        });

        receiptList.itemsProperty().addListener((observableValue, receipts, t1) -> {
            Platform.runLater(() -> income.setText(getIncomeAsString()));
        });

        receiptList.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                selectedReceipt = receiptList.getSelectionModel().getSelectedItem();
                receiptList.getSelectionModel().clearSelection();
                CustomFXMLLoader<DialogController> customFXMLLoader = JavaFXUtils.getCustomLoader("fxml/dialog.fxml", DialogController.class);
                DialogController dialogController = customFXMLLoader.controller;
                dialogController.setId(selectedReceipt.getTimestampID());
                Stage stage = new Stage();
                JavaFXUtils.setStage(stage, "Invalidation Dialog", false, StageStyle.UNDECORATED, Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(customFXMLLoader.root));
                stage.showAndWait();
                dialogHandler(dialogController);
            }
        });
    }

    private double getIncome() {
        return receiptList.getItems().stream().mapToDouble(Receipt::getAmount).sum();
    }

    private String getIncomeAsString() {
        return BigDecimal.valueOf(getIncome()).setScale(2, RoundingMode.HALF_UP).toString();
    }

    private LocalDate getDate() {
        return datePicker.getValue();
    }

    private void dialogHandler(DialogController dialogController) {
        DialogController.DialogStatus stat = dialogController.getStatus();
        if (stat.isCancel()) {
            CustomFXMLLoader<InfoDialogController> customFXMLLoader = JavaFXUtils.getCustomLoader("fxml/informationDialog.fxml", InfoDialogController.class);
            InfoDialogController infoDialogController = customFXMLLoader.controller;
            if (stat.getStatus() == 505) {
                infoDialogController.setWarning();
                infoDialogController.setInformationLabel("Receipt couldn't been cancelled due to server error!");
            }
            Stage stage = new Stage();
            JavaFXUtils.setStage(stage, "Information dialog", false, StageStyle.UNDECORATED, Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(customFXMLLoader.root));
            stage.showAndWait();

            ProductRoutes.getProducts(TOKEN, productsCallback, () -> System.out.println("Could not load products!"));
        } else if (stat.isRevert()) {
            receiptLoader.onReceiptLoaded(selectedReceipt);
        }
    }
}
