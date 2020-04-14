package ba.unsa.etf.si.gui.cell;

import javafx.scene.control.DateCell;

import java.time.LocalDate;

public class DisabledDateCell extends DateCell {

    @Override
    public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (item.isAfter(LocalDate.now())) {
            setDisable(true);
            setStyle("-fx-background-color: #AB656A");
        }
    }
}
