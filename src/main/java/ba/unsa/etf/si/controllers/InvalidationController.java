package ba.unsa.etf.si.controllers;


import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.persistance.ReceiptRepository;
import ba.unsa.etf.si.utility.HttpUtils;
import ba.unsa.etf.si.utility.IKonverzija;
import com.jfoenix.controls.JFXListView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import org.json.JSONArray;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.WatchEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;
import static ba.unsa.etf.si.App.primaryStage;
import static ba.unsa.etf.si.controllers.PrimaryController.currentUser;

public class InvalidationController {


    @FXML private TextField searchField;
    @FXML private JFXListView<Receipt> receiptList;

    public static ArrayList<Product> productList = new ArrayList<Product>();
    String TOKEN = currentUser.getToken();

    Consumer<String> callback = (String str) -> {
        System.out.println(str);
        receiptList.setCellFactory(new ReceiptCellFactory());
        fillLocalDatabase(new JSONArray(str));


        receiptList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    Receipt selectedReceipt = receiptList.getSelectionModel().getSelectedItem();
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
    };

    Consumer<String> callback1 = (String str) -> {
        productList = IKonverzija.getProductArrayFromJSON(str);
        System.out.println("Lista produkta učitana: " + productList.size());
        HttpRequest getSuppliesData = HttpUtils.GET(DOMAIN + "/api/receipts?cash_register_id=1", "Authorization", "Bearer " + TOKEN);

        HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), callback, () -> {
            System.out.println("Something went wrong.");
        });

    };
    @FXML
    public void initialize() {

        HttpRequest getSuppliesData = HttpUtils.GET(DOMAIN + "/api/products", "Authorization", "Bearer " + TOKEN);

        HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), callback1, () -> {
            System.out.println("Something went wrong.");
        });

    }

    public static class ReceiptCell extends ListCell<Receipt>{

        @FXML private Label receiptID, date, cashier, amount;

        public ReceiptCell() {
            loadFXML();
        }

        private void loadFXML() {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/invalidation.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void updateItem(Receipt receipt, boolean empty) {
            super.updateItem(receipt, empty);
            if(empty) {
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
            else {
                receiptID.setText(receipt.getTimestampID());
                date.setText(receipt.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm")));
                cashier.setText(receipt.getCashier());
                amount.setText(Double.toString(receipt.getAmount()));
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    private void dialogHandler (DialogController dialogController) {
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
        }
    }

    private static class ReceiptCellFactory implements javafx.util.Callback<javafx.scene.control.ListView<Receipt>, javafx.scene.control.ListCell<Receipt>> {
        @Override
        public ListCell<Receipt> call(ListView<Receipt> receiptListView) {
            return new ReceiptCell();
        }
    }


    //punjenje baze ne radi trenutno
    private void fillLocalDatabase (JSONArray arr) {
      //  System.out.println();
        ReceiptRepository repo = new ReceiptRepository();
        for (int i =0 ; i<arr.length() ; i++) {
            Receipt newRecp = new Receipt(arr.getJSONObject(i), productList);
            receiptList.getItems().add(newRecp);
        //   repo.add(newRecp);
        }
    }
}
