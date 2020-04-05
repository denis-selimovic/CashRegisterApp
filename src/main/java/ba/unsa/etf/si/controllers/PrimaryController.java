package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.utility.interfaces.ReceiptReverter;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;

import static ba.unsa.etf.si.App.primaryStage;

public class PrimaryController implements ReceiptReverter {

    @FXML
    private BorderPane pane;
    @FXML
    private JFXButton hideBtn, showBtn, first, second, third, invalidation;
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
        invalidation.setOnAction(e -> loadInvalidationController());
        hideBtn.setOnAction(e -> hideMenu());
        showBtn.setOnAction(e -> showMenu());
        third.visibleProperty().bind(new SimpleBooleanProperty(currentUser.getUserRole() == User.UserRole.ROLE_OFFICEMAN));

        welcomeText.setText("Welcome, " + currentUser.getName());
    }

    private void loadInvalidationController() {
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/invalidateForm.fxml"));
            fxmlLoader.setControllerFactory(e -> new InvalidationController(this));
            root = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        pane.setCenter(root);
    }

    public void showMyCashRegTab (FXMLLoader fxml) {
       Parent root = null;
        try {
            root = fxml.load();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        pane.setCenter(root);
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
            Scene scene = new Scene(fxmlLoader.load());
            App.centerStage(primaryStage,800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiptReverted(Receipt receipt) {
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/first.fxml"));
            fxmlLoader.setControllerFactory(c -> new MyCashRegisterController(receipt, this));
            root = fxmlLoader.load();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        pane.setCenter(root);
    }

    @Override
    public void loadInvalidationTab() {
        loadInvalidationController();
    }
}
