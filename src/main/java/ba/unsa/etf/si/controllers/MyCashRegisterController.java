package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.gui.factory.EditingCellFactory;
import ba.unsa.etf.si.gui.factory.ProductCellFactory;
import ba.unsa.etf.si.gui.factory.RemoveButtonCellFactory;
import ba.unsa.etf.si.gui.factory.TotalPriceCellFactory;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.persistance.ProductRepository;
import ba.unsa.etf.si.routes.OrderRoutes;
import ba.unsa.etf.si.routes.ProductRoutes;
import ba.unsa.etf.si.routes.ReceiptRoutes;
import ba.unsa.etf.si.utility.interfaces.ConnectivityObserver;
import ba.unsa.etf.si.utility.interfaces.PDFGenerator;
import ba.unsa.etf.si.utility.interfaces.PaymentProcessingListener;
import ba.unsa.etf.si.utility.javafx.CustomFXMLLoader;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import ba.unsa.etf.si.utility.modelutils.ProductUtils;
import ba.unsa.etf.si.utility.modelutils.ReceiptUtils;
import ba.unsa.etf.si.utility.pdfutils.PDFReceiptFactory;
import ba.unsa.etf.si.utility.stream.StreamUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.json.JSONArray;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static ba.unsa.etf.si.utility.javafx.StageUtils.centerStage;
import static ba.unsa.etf.si.utility.javafx.StageUtils.setStage;

public class MyCashRegisterController implements PaymentProcessingListener, ConnectivityObserver, PDFGenerator {

    private static String TOKEN;

    @FXML private TableColumn<Product, String> productName;
    @FXML private TableColumn<Product, String> productPrice;
    @FXML private TableColumn<Product, String> productQuantity;
    @FXML private TableColumn<Product, String> productDiscount;
    @FXML private TableColumn<Product, String> total;
    private final TableColumn<Product, Void> removeCol = new TableColumn<>();
    @FXML private TableView<Product> receiptTable;
    public JFXButton payButton;
    public JFXButton cancelButton;
    public Text title;

    @FXML private ListView<Product> productsTable;
    @FXML private ChoiceBox<String> myCashRegisterSearchFilters;
    @FXML private TextField myCashRegisterSearchInput;
    @FXML private Label price;
    @FXML private Text importLabel;
    @FXML private JFXButton importButton;
    private ObservableList<Product> products = FXCollections.observableArrayList();

    private Receipt revertedReceipt = null;
    private ArrayList<Product> revertedProducts = new ArrayList<>();
    public long sellerReceiptID;
    private final ProductRepository productRepository = new ProductRepository();

    private final Consumer<Product> addProduct = product -> {
        importButton.setDisable(true);
        if (!receiptTable.getItems().contains(product) && product.getQuantity() >= 1) {
            product.setTotal(1);
            receiptTable.getItems().add(product);
            price.setText(showPrice());
        }
    };

    public MyCashRegisterController() {
        App.connectivity.subscribe(this);
        sellerReceiptID = -1;
    }

    public MyCashRegisterController(Receipt receipt) {
        revertedReceipt = receipt;
        if(receipt.getServerID() != null) sellerReceiptID = receipt.getServerID();
        else sellerReceiptID = -1;
        App.connectivity.subscribe(this);
    }

    @FXML
    public void initialize() {
        TOKEN = PrimaryController.currentUser.getToken();
        importButton.setDisable(true);
        productName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        productPrice.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getPrice())));
        productDiscount.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getDiscount())));
        total.setCellFactory(new TotalPriceCellFactory());
        productQuantity.setCellFactory(new EditingCellFactory(this::removeFromReceipt, () -> price.setText(showPrice())));
        productQuantity.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getTotal())));
        removeCol.setCellFactory(new RemoveButtonCellFactory(this::removeFromReceipt));
        receiptTable.getColumns().add(removeCol);
        productsTable.setCellFactory(new ProductCellFactory(addProduct));
        productsTable.itemsProperty().addListener((observableValue, products, t1) -> price.setText(showPrice()));
        getProducts();
        myCashRegisterSearchFilters.getSelectionModel().selectFirst();
        myCashRegisterSearchInput.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) productsTable.setItems(products);
            else if(!oldValue.equals(newValue)) search();
        });
    }

    private String showPrice() {
        BigDecimal decimal = BigDecimal.valueOf(StreamUtils.price(receiptTable.getItems()));
        decimal = decimal.setScale(2, RoundingMode.HALF_UP);
        return String.format("%.2f", decimal.doubleValue());
    }

    private void search() {
        String filter = myCashRegisterSearchFilters.getValue();
        switch (filter) {
            case "Search by ID" -> productsTable.setItems(FXCollections.observableList(StreamUtils.filter(products, p -> p.getId() == getID())));
            case "Search by name" -> productsTable.setItems(FXCollections.observableList(StreamUtils.filter(products, p-> p.getName().toLowerCase().contains(getName().toLowerCase()))));
        }
    }

    private String getName() {
        return myCashRegisterSearchInput.getText();
    }

    private int getID() {
        return StreamUtils.getNumberFromString(myCashRegisterSearchInput.getText());
    }

    private final Runnable offlineMode = () -> {
        products = FXCollections.observableList(productRepository.getAll());
        products = products.stream().distinct().collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
        if(revertedReceipt != null) revertedProducts = ProductUtils.getProductsFromReceipt(products, revertedReceipt);
        setupTables();
    };

    private final Consumer<String> productsCallback = response -> {
        products = ProductUtils.getObservableProductListFromJSON(response);
        products = products.stream().distinct().collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
        if(revertedReceipt != null) revertedProducts = ProductUtils.getProductsFromReceipt(products, revertedReceipt);
        new Thread(() -> ProductUtils.updateLocalDatabase(products)).start();
        setupTables();
    };

    public void getProducts() {
        ProductRoutes.getProducts(TOKEN, productsCallback, () -> new Thread(offlineMode).start());
    }

    private void setupTables() {
        Platform.runLater(() -> {
            productsTable.setItems(products);
            importButton.setDisable(false);
            receiptTable.setItems(FXCollections.observableList(revertedProducts));
            if(revertedReceipt != null) price.setText(showPrice());
        });
    }

    private void removeFromReceipt(Product p) {
        p.setTotal(0);
        receiptTable.getItems().remove(p);
        receiptTable.refresh();
        price.setText(showPrice());
        if (receiptTable.getItems().size() == 0) importButton.setDisable(false);
    }

    public void clickCancelButton(ActionEvent actionEvent) {
        if (receiptTable.getItems().size() == 0 && sellerReceiptID == -1) return;
        StageUtils.showAlert("CONFIRMATON", "Do you want to cancel this receipt?", Alert.AlertType.CONFIRMATION, ButtonType.YES, ButtonType.CANCEL)
                .ifPresent(btnType -> {
                    if(btnType.getButtonData() == ButtonBar.ButtonData.YES) {
                        if(sellerReceiptID != -1) OrderRoutes.deleteOrder(sellerReceiptID, res -> {}, () -> System.out.println("Could not delete order!"));
                        restart();
                        sellerReceiptID = -1;
                    }
                });
    }

    private void restart() {
        receiptTable.getItems().clear();
        importButton.setDisable(false);
        productsTable.setDisable(false);
        productsTable.refresh();
        receiptTable.setDisable(false);
        receiptTable.refresh();
        myCashRegisterSearchInput.setDisable(false);
        myCashRegisterSearchFilters.setDisable(false);
        price.setText("0.00");
    }

    private void importBill() {
        importLabel.setText("Receipt imported from SellerApp.\n No additional editting allowed.");
        importButton.setDisable(true);
        productsTable.setDisable(true);
        receiptTable.setDisable(true);
        myCashRegisterSearchInput.setDisable(true);
        myCashRegisterSearchFilters.setDisable(true);
        receiptTable.getItems().clear();
    }

    public void clickImportButton(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        CustomFXMLLoader<SellerAppBillsListController> controllerCustomFXMLLoader = FXMLUtils.getCustomLoader("fxml/sellerappbillslist.fxml", SellerAppBillsListController.class);
        stage.setScene(new Scene(controllerCustomFXMLLoader.root));
        setStage(stage, "SellerApp Bills List", false, StageStyle.DECORATED, Modality.APPLICATION_MODAL);
        centerStage(stage, 500, 600);
        stage.show();
        stage.setOnHiding(event -> {
            SellerAppBillsListController sellerAppBillsListController = controllerCustomFXMLLoader.controller;
            if (sellerAppBillsListController.getInfoOnImportButtonClick()) {
                importBill();
                Pair<String, JSONArray> selectedSellerAppReceipt = sellerAppBillsListController.getSelectedSellerAppReceipt();
                sellerReceiptID = Long.parseLong(selectedSellerAppReceipt.getKey());
                receiptTable.setItems(ProductUtils.getProductsFromOrder(products, selectedSellerAppReceipt.getValue()));
                price.setText(showPrice());
            }
        });
    }

    public Receipt createReceiptFromTable () {
        revertedReceipt = null;
        return ReceiptUtils.createReceiptFromTable(receiptTable.getItems(), LocalDateTime.now(), PrimaryController.currentUser.getUsername(), sellerReceiptID);
    }

    @Override
    public void setOfflineMode() {
        Platform.runLater(() -> {
            importButton.setDisable(true);
        });
    }

    @Override
    public void setOnlineMode() {
        Platform.runLater(() -> {
            if(receiptTable.getItems().size() == 0) importButton.setDisable(false);
            new Thread(() -> ReceiptRoutes.sendReceipts(TOKEN)).start();
        });
    }

    public void generatePDFReceipt (Receipt receipt) throws IOException {
        PDFReceiptFactory pdfReceiptFactory = new PDFReceiptFactory(receipt);
        pdfReceiptFactory.createPdf();
    }

    public void paymentButtonClick() {
        if (receiptTable.getItems().isEmpty()) {
            StageUtils.showAlert("Error", "Please add items to the receipt", Alert.AlertType.ERROR, ButtonType.CANCEL);
            return;
        }
        try {
            CustomFXMLLoader<PaymentController> customFXMLLoader = FXMLUtils.getCustomLoader("fxml/payment.fxml", c -> new PaymentController(this, this, createReceiptFromTable()));
            customFXMLLoader.controller.setTotalAmount(price.getText());
            Stage stage = new Stage();
            setStage(stage, "Payment", false, StageStyle.UNDECORATED, Modality.APPLICATION_MODAL);
            centerStage(stage, 800, 600);
            stage.setScene(new Scene(customFXMLLoader.root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentProcessed(boolean valid) {
        if(valid) {
            new Thread(() -> {
                ProductUtils.restartProducts(products);
                new Thread(this::getProducts).start();
            }).start();
            Platform.runLater(() -> {
                receiptTable.getItems().clear();
                price.setText("0.00");
                restart();
            });
            sellerReceiptID = -1;
        }
    }

    @Override
    public void generatePDF(Receipt receipt) {
        new Thread(() -> {
            try {
                generatePDFReceipt(receipt);
            } catch (IOException e) {
                Platform.runLater(() -> StageUtils.showAlert("PDF error", "PDF could not be generated", Alert.AlertType.ERROR, ButtonType.CANCEL));
            }
        }).start();
    }
}
