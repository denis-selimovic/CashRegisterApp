package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.User;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

import static ba.unsa.etf.si.App.primaryStage;

public class PrimaryController {

    @FXML
    private BorderPane pane;
    @FXML
    private JFXButton hideBtn, showBtn, first, second, third;
    @FXML
    private Text welcomeText;

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

        welcomeText.setText("Welcome, " + currentUser.getName());
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

    public void logOut() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/loginForm.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            primaryStage.setWidth(scene.getWidth());
            primaryStage.setHeight(scene.getHeight());
            primaryStage.setScene(scene);
            primaryStage.setMaximized(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
