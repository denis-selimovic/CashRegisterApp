package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.utility.interfaces.ConnectivityObserver;
import ba.unsa.etf.si.utility.interfaces.ReceiptLoader;
import com.jfoenix.controls.JFXButton;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;

import static ba.unsa.etf.si.App.primaryStage;

public class PrimaryController implements ReceiptLoader, ConnectivityObserver {

    @FXML
    private BorderPane pane;
    @FXML
    private JFXButton hideBtn, showBtn, first, second, third, invalidation, orders;
    @FXML
    private Text welcomeText;
    @FXML
    private StackPane parentContainer;

    public static User currentUser;

    public PrimaryController(User user) {
        currentUser = user;
        App.connectivity.subscribe(this);
    }



    @FXML
    public void initialize() {
        first.setOnAction(e -> setController("fxml/first.fxml", e));
        second.setOnAction(e -> setController("fxml/second.fxml", e));
        third.setOnAction(e -> setController("fxml/archive.fxml", e));
        invalidation.setOnAction(e -> loadCustomController("fxml/invalidateForm.fxml", c -> new InvalidationController(this)));
        orders.setOnAction(e -> loadCustomController("fxml/orders.fxml", c -> new OrdersController(this)));
        hideBtn.setOnAction(e -> hideMenu());
        showBtn.setOnAction(e -> showMenu());
        third.visibleProperty().bind(new SimpleBooleanProperty(currentUser.getUserRole() == User.UserRole.ROLE_OFFICEMAN));

        welcomeText.setText("Welcome, " + currentUser.getName());
    }

    private void loadCustomController(String fxml, Callback<Class<?>, Object> controllerFactory) {
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
            fxmlLoader.setControllerFactory(controllerFactory);
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
    public void onReceiptLoaded(Receipt receipt) {
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/first.fxml"));
            fxmlLoader.setControllerFactory(c -> new MyCashRegisterController(receipt));
            root = fxmlLoader.load();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        pane.setCenter(root);
    }

    public void lock(ActionEvent event) {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/lock.fxml"));
        try {
            loader.setControllerFactory(c -> new LockController(currentUser));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = pane.getScene();
        root.translateYProperty().set(-scene.getHeight());
        parentContainer.getChildren().add(root);
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(root.translateYProperty(),0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(e -> {
            parentContainer.getChildren().remove(pane);
        });
        timeline.play();
    }


    @Override
    public void setOfflineMode() {
        Platform.runLater(() -> {
            second.setVisible(false);
            if(currentUser.getUserRole() == User.UserRole.ROLE_OFFICEMAN) third.setVisible(false);
            invalidation.setVisible(false);
            orders.setVisible(false);
        });
    }

    @Override
    public void setOnlineMode() {
        Platform.runLater(() -> {
            second.setVisible(true);
            if(currentUser.getUserRole() == User.UserRole.ROLE_OFFICEMAN) third.setVisible(true);
            invalidation.setVisible(true);
            orders.setVisible(true);
        });
    }
}
