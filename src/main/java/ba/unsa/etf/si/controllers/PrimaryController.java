package ba.unsa.etf.si.controllers;

import java.io.IOException;

import ba.unsa.etf.si.App;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PrimaryController {


    public BorderPane pane;
    @FXML private JFXButton hideBtn, showBtn, first, second;

    @FXML
    public void initialize() {
        first.setOnAction(e -> setController("fxml/first.fxml"));
        second.setOnAction(e -> setController("fxml/second.fxml"));
        hideBtn.setOnAction(e -> hideMenu());
        showBtn.setOnAction(e -> showMenu());
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
