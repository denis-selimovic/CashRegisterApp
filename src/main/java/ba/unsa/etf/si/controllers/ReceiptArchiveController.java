package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.gui.factory.ArchivedReceiptCellFactory;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.utility.date.DateConverter;
import ba.unsa.etf.si.utility.date.DateUtils;
import ba.unsa.etf.si.utility.stream.FilterUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Predicate;

public class ReceiptArchiveController {

    @FXML private JFXButton cancelCombo, cancelPicker;
    @FXML private DatePicker datePicker;
    @FXML private JFXComboBox<String> comboBox;
    @FXML private ListView<Receipt> receiptList;

    private ObservableList<Receipt> list = FXCollections.observableArrayList(new Receipt(123L, LocalDateTime.of(2020, 3, 12, 20, 48), "Neko Nekić", 21.31),
            new Receipt(124L, LocalDateTime.now(), "Oki Okić", 107.32));

    private final Predicate<Receipt> filter = receipt -> DateUtils.compareDates(getDate(), LocalDate.from(receipt.getDate())) && compareCashiers(getCashier(), receipt.getCashier());

    @FXML
    public void initialize() {
        receiptList.setCellFactory(new ArchivedReceiptCellFactory());
        receiptList.setItems(getReceipts());
        datePicker.setConverter(new DateConverter());

        datePicker.valueProperty().addListener((observableValue, localDate, newLocalDate) -> {
            receiptList.setItems(filter());
        });

        comboBox.setOnAction(e -> {
            receiptList.setItems(filter());
        });

        cancelPicker.setOnAction(e -> {
            datePicker.setValue(null);
            receiptList.setItems(filter());
        });

        cancelCombo.setOnAction(e -> {
            comboBox.setValue(null);
            receiptList.setItems(filter());
        });
    }

    private ObservableList<Receipt> getReceipts() {
        return list;
    }

    private ObservableList<Receipt> filter() {
        return FXCollections.observableArrayList(FilterUtils.filter(receiptList.getItems(), filter));
    }

    private LocalDate getDate() {
        return datePicker.getValue();
    }

    private String getCashier() {
        return comboBox.getSelectionModel().getSelectedItem();
    }

    private static boolean compareCashiers(String combo, String receipt) {
        return (combo == null) || (combo.isEmpty()) || receipt.equals(combo);
    }
}
