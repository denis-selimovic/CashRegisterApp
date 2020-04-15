package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.gui.factory.EditingCellFactory;
import ba.unsa.etf.si.gui.factory.RemoveButtonCellFactory;
import ba.unsa.etf.si.gui.factory.TotalPriceCellFactory;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.ReceiptItem;
import ba.unsa.etf.si.persistance.ProductRepository;
import ba.unsa.etf.si.routes.OrderRoutes;
import ba.unsa.etf.si.routes.ReceiptRoutes;
import ba.unsa.etf.si.utility.interfaces.ConnectivityObserver;
import ba.unsa.etf.si.utility.interfaces.PDFGenerator;
import ba.unsa.etf.si.utility.interfaces.PaymentProcessingListener;
import ba.unsa.etf.si.utility.javafx.CustomFXMLLoader;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import ba.unsa.etf.si.utility.json.ProductUtils;
import ba.unsa.etf.si.utility.pdfutil.PDFReceiptFactory;
import ba.unsa.etf.si.utility.server.HttpUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Pair;
import org.json.JSONArray;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ba.unsa.etf.si.App.DOMAIN;
import static ba.unsa.etf.si.utility.javafx.StageUtils.centerStage;
import static ba.unsa.etf.si.utility.javafx.StageUtils.setStage;


public class MyCashRegisterController implements PaymentProcessingListener, ConnectivityObserver, PDFGenerator {

    private static String TOKEN;

    public TableColumn<Product, String> productName;
    public TableColumn<Product, String> productPrice;
    public TableColumn<Product, String> productQuantity;
    public TableColumn<Product, String> productDiscount;
    public TableColumn<Product, String> total;
    public TableColumn<Product, Void> removeCol = new TableColumn<>();
    public TableView<Product> receiptTable;

    public JFXButton payButton;
    public JFXButton cancelButton;
    public Text title;

    public long sellerReceiptID;

    @FXML
    private ListView<Product> productsTable;
    @FXML
    private ChoiceBox<String> myCashRegisterSearchFilters;
    @FXML
    private TextField myCashRegisterSearchInput;
    @FXML
    private Label price;
    public Text importLabel = new Text();
    public JFXButton importButton = new JFXButton();
    private ObservableList<Product> products = FXCollections.observableArrayList();

    //podaci potrebni za storniranje racuna
    private Receipt revertedReceipt = null;
    private ArrayList<Product> revertedProducts = new ArrayList<>();

    private final ProductRepository productRepository = new ProductRepository();

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
        productQuantity.setCellFactory(new EditingCellFactory(this::removeFromReceipt));
        productQuantity.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getTotal())));
        removeCol.setCellFactory(new RemoveButtonCellFactory(this::removeFromReceipt));
        receiptTable.getColumns().add(removeCol);


        productsTable.setCellFactory(new ProductCellFactory());
        getProducts();
        myCashRegisterSearchFilters.getSelectionModel().selectFirst();
        myCashRegisterSearchInput.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                productsTable.setItems(products);
                return;
            }
            if (!oldValue.equals(newValue)) search();
        });
    }

    public double price() {
        return receiptTable.getItems().stream().mapToDouble(p -> {
            String format = String.format("%.2f", p.getTotalPrice());
            if (format.contains(",")) format = format.replace(",", ".");
            return Double.parseDouble(format);
        }).sum();
    }

    public String showPrice() {
        BigDecimal decimal = BigDecimal.valueOf(price());
        decimal = decimal.setScale(2, RoundingMode.HALF_UP);
        return String.format("%.2f", decimal.doubleValue());
    }

    public void search() {
        String filter = myCashRegisterSearchFilters.getValue();
        switch (filter) {
            case "Search by ID":
                productsTable.setItems(filterByID(getID()));
                break;
            case "Search by name":
                productsTable.setItems(filterByName(getName()));
                break;
        }
    }

    public String getName() {
        return myCashRegisterSearchInput.getText();
    }

    public int getID() {
        String text = myCashRegisterSearchInput.getText();
        int id;
        try {
            id = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            id = -1;
        }
        return id;
    }

    private ObservableList<Product> filterByID(int id) {
        if(id == -1) return FXCollections.observableArrayList();
        return products.stream().filter(p -> p.getServerID() == id).collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
    }

    private ObservableList<Product> filterByName(String name) {
        return products.stream().filter(p -> p.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
    }

    public void getProducts() {
        HttpRequest GET = HttpUtils.GET(DOMAIN + "/api/products", "Authorization", "Bearer " + TOKEN);
        HttpUtils.send(GET, HttpResponse.BodyHandlers.ofString(), response -> {
            try {
                products = ProductUtils.getObservableProductListFromJSON(response);
                products = products.stream().distinct().collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
                if(revertedReceipt != null) revertedProducts = getProductsFromReceipt(revertedReceipt);

                new Thread(() -> {
                    List<Product> hibernate = productRepository.getAll();
                    products.forEach(p -> {
                        hibernate.forEach(h -> {
                            if(h.equals(p)) p.setId(h.getId());
                        });
                        productRepository.update(p);
                    });

                }).start();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            setupTables();
        }, () -> {
            new Thread(() -> {
                products = FXCollections.observableList(productRepository.getAll());
                products = products.stream().distinct().collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
                if(revertedReceipt != null) revertedProducts = getProductsFromReceipt(revertedReceipt);
                setupTables();
            }).start();
        });
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

    public void clickImportButton(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("SellerApp Bills List");
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/sellerappbillslist.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setWidth(500);
        stage.setHeight(600);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        stage.setOnHiding(event -> {

            SellerAppBillsListController sellerAppBillsListController = fxmlLoader.getController();
            if (sellerAppBillsListController.getInfoOnImportButtonClick()) {
                importLabel.setText("Receipt imported from SellerApp.\n No additional editting allowed.");
                importButton.setDisable(true);
                productsTable.setDisable(true);
                receiptTable.setDisable(true);
                myCashRegisterSearchInput.setDisable(true);
                myCashRegisterSearchFilters.setDisable(true);
                receiptTable.getItems().clear();
                Pair<String, JSONArray> selectedSellerAppReceipt = sellerAppBillsListController.getSelectedSellerAppReceipt();

                //Unpack JSONArray + receipt ID fetched
                JSONArray jsonReceiptProducts = selectedSellerAppReceipt.getValue();
                sellerReceiptID = Long.parseLong(selectedSellerAppReceipt.getKey());
                for (Product p : products) {
                    for (int i = 0; i < jsonReceiptProducts.length(); i++) {
                        if (p.getServerID().toString().equals(jsonReceiptProducts.getJSONObject(i).get("id").toString())) {
                            Product receiptProduct = p;
                            double doubleTotal = (double) jsonReceiptProducts.getJSONObject(i).get("quantity");
                            receiptProduct.setTotal((int) doubleTotal);
                            receiptTable.getItems().add(receiptProduct);
                        }
                    }
                }

                price.setText(showPrice());
            }

        });
    }

    public Receipt createReceiptFromTable () {
        revertedReceipt = null;
        Receipt receipt = new Receipt(LocalDateTime.now(), PrimaryController.currentUser.getUsername(), price());
        for(Product p : receiptTable.getItems()) receipt.getReceiptItems().add(new ReceiptItem(p));
        if(sellerReceiptID != -1) receipt.setServerID(sellerReceiptID);
        return receipt;
    }


    public ArrayList<Product> getProductsFromReceipt(Receipt receipt) {
        ArrayList<Product> pr = new ArrayList<>();
        for (Product p : products) {
            for (ReceiptItem r : receipt.getReceiptItems()) {
                if (r.getProductID().longValue() == p.getServerID().longValue()) {
                    p.setTotal((int) r.getQuantity());
                    pr.add(p);
                }
            }
        }
        return pr;
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

    public final class ProductCell extends ListCell<Product> {

        @FXML
        private Label productID, name;
        @FXML
        private JFXButton addBtn;

        public ProductCell() {
            loadFXML();
        }

        private void loadFXML() {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/product.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void updateItem(Product product, boolean empty) {
            super.updateItem(product, empty);
            if (empty) {
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
            else {
                productID.setText(Long.toString(product.getServerID()));
                name.setText(product.getName());
                addBtn.setTooltip(new Tooltip("Add to cart"));
                addBtn.setOnAction(e -> {
                    importButton.setDisable(true);
                    if (!receiptTable.getItems().contains(product) && product.getQuantity() >= 1) {
                        product.setTotal(1);
                        receiptTable.getItems().add(product);
                        price.setText(showPrice());
                    }
                });
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    public class ProductCellFactory implements Callback<ListView<Product>, ListCell<Product>> {

        @Override
        public ListCell<Product> call(ListView<Product> param) {
            return new ProductCell();
        }
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
                for(Product p : products) {
                    p.setQuantity(p.getQuantity() - p.getTotal());
                    p.setTotal(0);
                    if(p.getId() != null) productRepository.update(p);
                }
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
