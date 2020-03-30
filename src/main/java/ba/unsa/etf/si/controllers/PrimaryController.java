package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.User;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class PrimaryController {

    @FXML private BorderPane pane;
    @FXML private JFXButton hideBtn, showBtn, first, second, third;

    public static User currentUser;

    public PrimaryController(User user) {
        currentUser = user;
    }

    @FXML
    public void initialize() {
        first.setOnAction(e -> setController("fxml/first.fxml", e));
        second.setOnAction(e -> setController("fxml/second.fxml", e));
        third.setOnAction(e -> setController("fxml/archive.fxml", e));
        hideBtn.setOnAction(e -> hideMenu());
        showBtn.setOnAction(e -> showMenu());
        third.visibleProperty().bind(new SimpleBooleanProperty(currentUser.getUserRole() == User.UserRole.ROLE_OFFICEMAN));
    }

    public void setController(String fxml, ActionEvent e) {
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
            root = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        pane.setCenter(root);
        enableMenu();
        JFXButton button = (JFXButton) e.getSource();
        button.setDisable(true);
    }

    public void enableMenu() {
        first.setDisable(false);
        second.setDisable(false);
        third.setDisable(false);
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
