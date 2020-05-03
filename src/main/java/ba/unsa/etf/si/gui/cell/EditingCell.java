package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.models.Product;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.function.Consumer;

public class EditingCell extends TableCell<Product, String> {

    private TextField textField;
    private GridPane gridPane;
    private Button plusButton;
    private Button minusButton;
    private final Consumer<Product> action;
    private final Runnable price;

    public EditingCell(Consumer<Product> action, Runnable price) {
        this.action = action;
        this.price = price;
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            createPlusButton();
            createMinusButton();
            fillGridPane();
            setText(null);
            setGraphic(gridPane);
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
        textField.setMinWidth(40);
        textField.setPrefWidth(40);
        textField.setOnAction((e) -> commitEdit(textField.getText()));
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("[0-9\u0008]*")) {
                textField.setText("");
                textField.setText(newValue.replaceAll("[^\\d\b]", ""));
            }
        });
        textField.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER)) {
                int current = indexProperty().get();
                Product p = getTableView().getItems().get(current);
                if(getText().isEmpty()) {
                    getTableView().getItems().get(current).setTotal(1);
                    setText("1");
                }
                if(getText().equals("0")) {
                    action.accept(p);
                    return;
                }
                Platform.runLater(() -> {
                    if(p.getQuantity() < Integer.parseInt(getText())) {
                        p.setTotal((int)p.getQuantity().doubleValue());
                        setText(Integer.toString(p.getTotal()));
                    }
                    else p.setTotal(Integer.parseInt(getText()));
                    getTableView().refresh();
                    price.run();
                });
            }
        });
    }

    private void fillGridPane() {
        gridPane = new GridPane();
        gridPane.setMinWidth(90);
        gridPane.setPrefWidth(90);
        ColumnConstraints cConstraints = new ColumnConstraints(45);
        cConstraints.setHalignment(HPos.CENTER);
        gridPane.getColumnConstraints().addAll(cConstraints,cConstraints);
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setFillHeight(true);
        gridPane.getRowConstraints().addAll(rowConstraints,rowConstraints);
        gridPane.add( textField, 0, 0, 1, 2 );
        gridPane.add( plusButton, 1, 0 );
        gridPane.add( minusButton, 1, 1 );
    }

    private void createPlusButton() {
        plusButton = new Button();
        plusButton.setMinWidth(30);
        plusButton.setPrefWidth(30);
        plusButton.setId("quantityPlusButton");
        plusButton.setText("+");
        plusButton.setOnAction(e -> {
            double currentValue = Double.parseDouble(textField.getText());
            int intCurrentValue = (int)currentValue;
            int currentIndex = indexProperty().get();
            Product p = getTableView().getItems().get(currentIndex);
            if( intCurrentValue < p.getQuantity() )
                intCurrentValue++;
            textField.setText(String.valueOf(intCurrentValue));
        });
    }

    private void createMinusButton() {
        minusButton = new Button();
        minusButton.setMinWidth(30);
        minusButton.setPrefWidth(30);
        minusButton.setId("quantityMinusButton");
        minusButton.setText("-");
        minusButton.setOnAction(e -> {
            double currentValue = Double.parseDouble(textField.getText());
            int intCurrentValue = (int)currentValue;
            if( intCurrentValue != 0 )
                    intCurrentValue--;
            textField.setText(String.valueOf(intCurrentValue));
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem();
    }
}
