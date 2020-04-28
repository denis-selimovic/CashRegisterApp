package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class ReceiptArchiveController {


    @FXML
    private JFXButton cancelCombo, cancelPicker;
    @FXML
    private DatePicker datePicker;
    @FXML
    private JFXComboBox<String> comboBox;
    @FXML
    private ListView<Receipt> receiptList;

    private ObservableList<Receipt> list = FXCollections.observableArrayList(new Receipt(123L, LocalDateTime.of(2020, 3, 12, 20, 48), "Neko Nekić", 21.31),
            new Receipt(124L, LocalDateTime.now(), "Oki Okić", 107.32));

    @FXML
    public void initialize() {
        receiptList.setCellFactory(new ReceiptCellFactory());
        receiptList.setItems(getReceipts());
        datePicker.setConverter(new StringConverter<LocalDate>() {
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

        datePicker.valueProperty().addListener((observableValue, localDate, newLocalDate) -> {
            receiptList.setItems(sort(getDate(), getCashier()));
        });

        comboBox.setOnAction(e -> {
            receiptList.setItems(sort(getDate(), getCashier()));
        });

        cancelPicker.setOnAction(e -> {
            datePicker.setValue(null);
            receiptList.setItems(sort(getDate(), getCashier()));
        });

        cancelCombo.setOnAction(e -> {
            comboBox.setValue(null);
            receiptList.setItems(sort(getDate(), getCashier()));
        });
    }

    public ObservableList<Receipt> getReceipts() {
        return list;
    }

    public ObservableList<Receipt> sort(LocalDate date, String cashier) {
        return getReceipts().stream().filter(r -> compareDates(date, LocalDate.from(r.getDate())))
                .filter(r -> compareCashiers(cashier, r.getCashier()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
    }

    private LocalDate getDate() {
        return datePicker.getValue();
    }

    private String getCashier() {
        return comboBox.getSelectionModel().getSelectedItem();
    }

    private static boolean compareDates(LocalDate picker, LocalDate receipt) {
        return (picker == null) || picker.isEqual(receipt);
    }

    private static boolean compareCashiers(String combo, String receipt) {
        return (combo == null) || (combo.isEmpty()) || receipt.equals(combo);
    }

    public static final class ReceiptCell extends ListCell<Receipt> {

        @FXML
        private Label receiptID, cashier, date;

        public ReceiptCell() {
            loadFXML();
        }

        private void loadFXML() {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/receipt.fxml"));
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
            if (empty) {
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            } else {
                receiptID.setText(Long.toString(receipt.getId()));
                cashier.setText(receipt.getCashier());
                date.setText(receipt.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    public static class ReceiptCellFactory implements Callback<ListView<Receipt>, ListCell<Receipt>> {

        @Override
        public ListCell<Receipt> call(ListView<Receipt> param) {
            return new ReceiptCell();
        }
    }

}
