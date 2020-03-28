package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class PrimaryController {

    public enum Role {CASHIER, CHIEF}


    @FXML private BorderPane pane;
    @FXML private JFXButton hideBtn, showBtn, first, second, third;

    private Role role = null;

    public PrimaryController(Role role) {
        this.role = role;
    }

    @FXML
    public void initialize() {
        first.setOnAction(e -> setController("fxml/first.fxml"));
        second.setOnAction(e -> setController("fxml/second.fxml"));
        third.setOnAction(e -> setController("fxml/archive.fxml"));
        hideBtn.setOnAction(e -> hideMenu());
        showBtn.setOnAction(e -> showMenu());
        third.visibleProperty().bind(new SimpleBooleanProperty(role == Role.CHIEF));
    }

    public void setController(String fxml) {
        Parent root = null;
        try {
            root = App.loadFXML(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pane.setCenter(root);
    }

    public void hideMenu() {
        pane.getLeft().setVisible(false);
        hideBtn.setVisible(false);
        showBtn.setVisible(true);
    }

    public void showMenu() {
        pane.getLeft().setVisible(true);
        hideBtn.setVisible(true);
        showBtn.setVisible(false);
    }
}
