package ba.unsa.etf.si.controllers;

import java.io.IOException;

import ba.unsa.etf.si.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class PrimaryController {


    public BorderPane pane;

    public void setFirst(ActionEvent actionEvent) {
        Parent root = null;
        try {
            root = App.loadFXML("fxml/first.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        pane.setCenter(root);
    }

    public void setSecond(ActionEvent actionEvent) {
        Parent root = null;
        try {
            root = App.loadFXML("fxml/second.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        pane.setCenter(root);
    }
}
