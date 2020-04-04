package ba.unsa.etf.si.controllers;


import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import com.jfoenix.controls.JFXListView;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;

import java.io.IOException;
import java.nio.file.WatchEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ba.unsa.etf.si.App.primaryStage;

public class InvalidationController {


    @FXML private TextField searchField;
    @FXML private JFXListView<Receipt> receiptList;



    @FXML
    public void initialize() {
        receiptList.setCellFactory(new ReceiptCellFactory());
        receiptList.getItems().add(new Receipt(Integer.toUnsignedLong(12355), LocalDateTime.now(), "Denis", 20.0));
        receiptList.getItems().add(new Receipt(Integer.toUnsignedLong(12355), LocalDateTime.now(), "Neko", 40.0));

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
                    dialogController.setId(selectedReceipt.getId());

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
                receiptID.setText(Long.toString(receipt.getId()));
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
        }
    }

    private static class ReceiptCellFactory implements javafx.util.Callback<javafx.scene.control.ListView<Receipt>, javafx.scene.control.ListCell<Receipt>> {
        @Override
        public ListCell<Receipt> call(ListView<Receipt> receiptListView) {
            return new ReceiptCell();
        }
    }
}
