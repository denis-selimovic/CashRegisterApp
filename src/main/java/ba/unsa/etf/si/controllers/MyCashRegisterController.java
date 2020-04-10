package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.ReceiptItem;
import ba.unsa.etf.si.persistance.ProductRepository;
import ba.unsa.etf.si.utility.HttpUtils;
import ba.unsa.etf.si.utility.PDFReceiptFactory;
import ba.unsa.etf.si.utility.interfaces.ConnectivityObserver;
import ba.unsa.etf.si.utility.interfaces.IKonverzija;
import ba.unsa.etf.si.utility.interfaces.PDFGenerator;
import ba.unsa.etf.si.utility.interfaces.PaymentProcessingListener;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static ba.unsa.etf.si.App.DOMAIN;
import static ba.unsa.etf.si.App.centerStage;



public class MyCashRegisterController implements PaymentProcessingListener, ConnectivityObserver, PDFGenerator {

    private static String TOKEN;

    public TableColumn<Product, String> productName;
    public TableColumn<Product, String> productPrice;
    public TableColumn<Product, String> productQuantity;
    public TableColumn<Product, String> productDiscount;
    public TableColumn<Product, String> total;
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



    private ProductRepository productRepository = new ProductRepository();

    public MyCashRegisterController() {
        App.connectivity.subscribe(this);
    }

    public MyCashRegisterController(Receipt receipt) {
        revertedReceipt = receipt;
        App.connectivity.subscribe(this);
    }

    @FXML
    public void initialize() {
        sellerReceiptID = -1;
        TOKEN = PrimaryController.currentUser.getToken();

        importButton.setDisable(true);

        Callback<TableColumn<Product, String>, TableCell<Product, String>> cellFactory
                = (TableColumn<Product, String> param) -> new EditingCell();

        productName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        productPrice.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getPrice())));
        productDiscount.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getDiscount())));
        total.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (!empty) {
                    int current = indexProperty().getValue();
                    Product p = param.getTableView().getItems().get(current);
                    setText(String.format("%.2f", p.getTotalPrice()));
                } else {
                    setText(null);
                }
            }
        });
        productQuantity.setCellFactory(cellFactory);
        productQuantity.setCellValueFactory(cellData -> {
            Product p = cellData.getValue();
            return new SimpleStringProperty(Integer.toString(p.getTotal()));
        });
        addRemoveButtonToTable();


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
                products = IKonverzija.getObservableProductListFromJSON(response);
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
            Platform.runLater(() -> {
                productsTable.setItems(products);
                importButton.setDisable(false);
                receiptTable.setItems(FXCollections.observableList(revertedProducts));
                if (revertedReceipt != null) price.setText(showPrice());
            });
        }, () -> {
            new Thread(() -> {
                products = FXCollections.observableList(productRepository.getAll());
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

    private void addRemoveButtonToTable() {
        TableColumn<Product, Void> colBtn = new TableColumn<>();

        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<TableColumn<Product, Void>, TableCell<Product, Void>>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                final TableCell<Product, Void> cell = new TableCell<Product, Void>() {

                    private final Button btn = new Button();

                    {
                        btn.setOnAction(e -> removeFromReceipt(indexProperty().get()));
                        btn.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("img/cancel.png"))));
                        btn.getStyleClass().add("btn");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);
        colBtn.setResizable(false);
        receiptTable.getColumns().add(colBtn);
    }

    public void removeFromReceipt(int index) {
        receiptTable.getItems().remove(index).setTotal(0);
        receiptTable.refresh();
        price.setText(showPrice());
        if (receiptTable.getItems().size() == 0) importButton.setDisable(false);
    }


    public void clickCancelButton(ActionEvent actionEvent) {
        if (receiptTable.getItems().size() == 0 && sellerReceiptID == -1) return;
        showAlert("CONFIRMATON", "Do you want to cancel this receipt?", Alert.AlertType.CONFIRMATION);
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
        Receipt receipt = new Receipt(LocalDateTime.now(), PrimaryController.currentUser.getUsername(), price());
        for(Product p : receiptTable.getItems()) receipt.getReceiptItems().add(new ReceiptItem(p));
        if(sellerReceiptID != -1) receipt.setServerID(sellerReceiptID);
        return receipt;
    }


    public ArrayList<Product> getProductsFromReceipt(Receipt receipt) {
        ArrayList<Product> pr = new ArrayList<>();
        for (Product p : products) {
            for (ReceiptItem r : receipt.getReceiptItems()) {
                if (r.getProductID().longValue() == p.getId().longValue()) {
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
        });
    }

    class EditingCell extends TableCell<Product, String> {

        private TextField textField;

        private EditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(item);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnAction((e) -> commitEdit(textField.getText()));
            textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
                if (!newValue.matches("[0-9\u0008]*")) {
                    textField.setText(newValue.replaceAll("[^\\d\b]", ""));
                }
            });
            textField.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    int current = indexProperty().get();
                    if (getText().isEmpty()) {
                        getTableView().getItems().get(current).setTotal(1);
                        setText("1");
                    }
                    if (getText().equals("0")) {
                        removeFromReceipt(current);
                        return;
                    }
                    Product p = getTableView().getItems().get(current);
                    if (p.getQuantity() < Integer.parseInt(getText())) {
                        p.setTotal((int) p.getQuantity().doubleValue());
                        setText(Integer.toString(p.getTotal()));
                    } else p.setTotal(Integer.parseInt(getText()));
                    getTableView().getColumns().get(current).setVisible(false);
                    getTableView().getColumns().get(current).setVisible(true);
                    price.setText(showPrice());
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
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
                productID.setText(Long.toString(product.getId()));
                name.setText(product.getName());
                addBtn.setTooltip(new Tooltip("Add to cart"));
                addBtn.setOnAction(e -> {
                    importButton.setDisable(true);
                    if (!receiptTable.getItems().contains(product)) {
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please add items in the receipt!");
            alert.show();
        } else
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/payment.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                PaymentController paymentController = fxmlLoader.getController();
                paymentController.setTotalAmount(price.getText());
                paymentController.setReceipt(this.createReceiptFromTable());
                paymentController.setPaymentProcessingListener(this);
                paymentController.setPDFGenerator(this);
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Payment");
                centerStage(stage, 800, 600);
                stage.setScene(scene);
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
                Platform.runLater(() -> showAlert("PDF error", "PDF could not be generated", Alert.AlertType.ERROR));
            }
        }).start();
    }

    private void showAlert(String title, String headerText, Alert.AlertType type) {
        Alert alert = new Alert(type, "", ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.getDialogPane().getStylesheets().add(App.class.getResource("css/alert.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (sellerReceiptID != -1) {
                HttpRequest deleteSellerReceipt = HttpUtils.DELETE(DOMAIN + "/api/orders/" + sellerReceiptID, "Authorization", "Bearer " + TOKEN);
                HttpUtils.send(deleteSellerReceipt, HttpResponse.BodyHandlers.ofString(), response -> {
                    sellerReceiptID = -1;
                }, () -> {
                    System.out.println("Something went wrong.");
                });
            }
            Platform.runLater(this::restart);
            for (Product p : products) p.setTotal(1);
        } else if (result.isPresent() && result.get() == ButtonType.CANCEL) alert.hide();
    }
}
