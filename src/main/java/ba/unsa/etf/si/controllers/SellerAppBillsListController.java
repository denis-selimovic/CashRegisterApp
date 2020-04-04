package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.utility.HttpUtils;
import ba.unsa.etf.si.utility.IKonverzija;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class SellerAppBillsListController {

    private String TOKEN;

    public ListView billsList = new ListView();
    public Label errorMessageSABL = new Label();
    public JFXButton importButton = new JFXButton();

    private ArrayList<Pair<String, JSONArray>> sellerAppReceipts = new ArrayList<>();

    @FXML
    public void initialize(){
        importButton.setDisable(true);
        TOKEN = PrimaryController.currentUser.getToken();
        billsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                errorMessageSABL.setText("");
            }
        });
        HttpRequest GET = HttpUtils.GET(App.DOMAIN + "/api/orders", "Authorization", "Bearer " + TOKEN);
        HttpUtils.send(GET, HttpResponse.BodyHandlers.ofString(), response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Pair<String,JSONArray> sellerAppReceipt = new Pair<>( jsonArray.getJSONObject(i).get("id").toString(), jsonArray.getJSONObject(i).getJSONArray("receiptItems") );
                    sellerAppReceipts.add( sellerAppReceipt );
                    billsList.getItems().add( jsonArray.getJSONObject(i).get("id") );
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                importButton.setDisable(false);
            });
        }, () -> {
            System.out.println("ERROR!");
        });

    }

    public void clickImportButton(ActionEvent actionEvent) {
        if( billsList.getSelectionModel().getSelectedItem() != null ) {
            Stage stage = (Stage) billsList.getScene().getWindow();
            stage.close();
        }
        else {
            errorMessageSABL.setText("No SellerApp receipt selected!");
        }
    }

    public Pair<String,JSONArray> getSelectedSellerAppReceipt(){
        return sellerAppReceipts.get( billsList.getSelectionModel().getSelectedIndex() );
    }

}
