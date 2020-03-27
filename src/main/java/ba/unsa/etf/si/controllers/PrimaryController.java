package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.User;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class PrimaryController {

    public enum Role {CASHIER, CHIEF}


    @FXML private BorderPane pane;
    @FXML private JFXButton hideBtn, showBtn, first, second, third;

<<<<<<< HEAD
    private User currentUser;

    public PrimaryController(User user) {
        this.currentUser = user;
=======
    private Role role = null;

    public PrimaryController(Role role) {
        this.role = role;
>>>>>>> Added roles
    }

    @FXML
    public void initialize() {
        first.setOnAction(e -> setController("fxml/first.fxml"));
        second.setOnAction(e -> setController("fxml/second.fxml"));
        hideBtn.setOnAction(e -> hideMenu());
        showBtn.setOnAction(e -> showMenu());

        System.out.println(currentUser);
    }

    public void setController(String fxml) {
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
            root = fxmlLoader.load();
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
