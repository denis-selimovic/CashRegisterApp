package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.routes.OrderRoutes;
import ba.unsa.etf.si.utility.stream.StreamUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SellerAppBillsListController {

    @FXML
    private ListView<String> billsList;
    @FXML
    private Label errorMessageSABL;
    @FXML
    private JFXButton importButton;
    @FXML
    private TextField searchInput;

    private final ArrayList<Pair<String, JSONArray>> sellerAppReceipts = new ArrayList<>();
    private boolean isImportButtonClicked;

    @FXML
    public void initialize() {
        importButton.setDisable(true);
        isImportButtonClicked = false;
        searchInput.textProperty().addListener((observable, oldValue, newValue) -> StreamUtils.filter(billsList.getItems(), item -> item.toLowerCase().contains(newValue)));
        billsList.getSelectionModel().selectedItemProperty().addListener((observableValue, o, t1) -> errorMessageSABL.setText(""));
        OrderRoutes.getOrders(orderImport, () -> System.out.println("Could not load orders!"));
    }

    private final Consumer<String> orderImport = response -> {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                Pair<String, JSONArray> sellerAppReceipt = new Pair<>(jsonArray.getJSONObject(i).get("id").toString(), jsonArray.getJSONObject(i).getJSONArray("receiptItems"));
                sellerAppReceipts.add(sellerAppReceipt);
                billsList.getItems().add(Long.toString(jsonArray.getJSONObject(i).getLong("id")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> importButton.setDisable(false));
    };

    public void clickImportButton(ActionEvent actionEvent) {
        if (billsList.getSelectionModel().getSelectedItems() == null) {
            errorMessageSABL.setText("No SellerApp receipt selected!");
            return;
        }
        isImportButtonClicked = true;
        Stage stage = (Stage) billsList.getScene().getWindow();
        stage.close();
    }

    public boolean getInfoOnImportButtonClick() {
        return isImportButtonClicked;
    }

    public Pair<String, JSONArray> getSelectedSellerAppReceipt() {
        return sellerAppReceipts.get(billsList.getSelectionModel().getSelectedIndex());
    }
}
