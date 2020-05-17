package ba.unsa.etf.si.gui.cell;

import ba.unsa.etf.si.models.Product;
import javafx.application.Platform;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.function.Consumer;

public class EditingCell extends TableCell<Product, String> {

    private TextField textField;
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
                Product p = getTableView().getItems().get(current);
                if (getText().isEmpty()) {
                    getTableView().getItems().get(current).setTotal(1);
                    setText("1");
                }
                if (getText().equals("0")) {
                    action.accept(p);
                    return;
                }
                Platform.runLater(() -> {
                    if (p.getQuantity() < Integer.parseInt(getText())) {
                        p.setTotal((int) p.getQuantity().doubleValue());
                        setText(Integer.toString(p.getTotal()));
                    } else p.setTotal(Integer.parseInt(getText()));
                    getTableView().refresh();
                    price.run();
                });
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem();
    }
}
