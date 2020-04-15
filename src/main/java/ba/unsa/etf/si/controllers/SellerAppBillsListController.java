package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.utility.server.HttpUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.json.JSONArray;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class SellerAppBillsListController {

    private String TOKEN;

    public ListView billsList = new ListView();
    public Label errorMessageSABL = new Label();
    public JFXButton importButton = new JFXButton();
    public TextField searchInput = new TextField();

    private ArrayList<Pair<String, JSONArray>> sellerAppReceipts = new ArrayList<>();
    private boolean isImportButtonClicked;

    @FXML
    public void initialize(){
        importButton.setDisable(true);
        isImportButtonClicked = false;
        searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBillsList(newValue);
        });
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

    private void filterBillsList(String newValue) {
        if( newValue != null  ) {
            billsList.getItems().clear();
            for (Pair<String, JSONArray> pair : sellerAppReceipts) {
                String key = pair.getKey();
                if( key.length() >= newValue.length()  ){
                    int i = 0;
                    for( i = 0; i < newValue.length(); i++ )
                        if( newValue.charAt(i) != key.charAt(i) )break;
                    if( i == newValue.length() ) billsList.getItems().add( key );
                }
            }
        }
    }

    public void clickImportButton(ActionEvent actionEvent) {
        if( billsList.getSelectionModel().getSelectedItem() != null ) {
            isImportButtonClicked = true;
            Stage stage = (Stage) billsList.getScene().getWindow();
            stage.close();
        }
        else {
            errorMessageSABL.setText("No SellerApp receipt selected!");
        }
    }

    public boolean getInfoOnImportButtonClick(){
        return isImportButtonClicked;
    }

    public Pair<String,JSONArray> getSelectedSellerAppReceipt(){
        return sellerAppReceipts.get( billsList.getSelectionModel().getSelectedIndex() );
    }

}
