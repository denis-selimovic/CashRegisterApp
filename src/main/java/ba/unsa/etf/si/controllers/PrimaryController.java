package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.models.status.Connection;
import ba.unsa.etf.si.utility.HttpUtils;
import ba.unsa.etf.si.utility.JavaFXUtils;
import ba.unsa.etf.si.utility.interfaces.ConnectivityObserver;
import ba.unsa.etf.si.utility.interfaces.ReceiptLoader;
import ba.unsa.etf.si.utility.interfaces.TokenReceiver;
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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;
import static ba.unsa.etf.si.App.primaryStage;

public class PrimaryController implements ReceiptLoader, ConnectivityObserver, TokenReceiver {

    @FXML
    private BorderPane pane;
    @FXML
    private JFXButton hideBtn, showBtn, first, second, third, invalidation, orders,
            lockButton, cashierBalancingButton;
    @FXML
    private Text welcomeText;
    @FXML
    private StackPane parentContainer;

    public static User currentUser;

    private Connection connection = Connection.ONLINE;
    private boolean cashRegisterSet = false;
    private boolean dialogShown = false;

    public PrimaryController(User user) {
        currentUser = user;
        App.connectivity.subscribe(this);
    }

    @FXML
    public void initialize() {
        first.setOnAction(e -> setController("fxml/first.fxml"));
        second.setOnAction(e -> setController("fxml/second.fxml"));
        third.setOnAction(e -> setController("fxml/archive.fxml"));
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
        cashRegisterSet = false;
    }


    public void setController(String fxml) {
        cashRegisterSet = fxml.equals("fxml/first.fxml");
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
            JavaFXUtils.centerStage(primaryStage, 800, 600);
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
            Scene scene = pane.getScene();
            root.translateYProperty().set(-scene.getHeight());
            parentContainer.getChildren().add(root);
            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
            timeline.getKeyFrames().add(kf);
            timeline.setOnFinished(e -> {
                parentContainer.getChildren().remove(pane);
            });
            timeline.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setOfflineMode() {
        Platform.runLater(() -> {
            if(connection != Connection.OFFLINE) {
                showNotification(Pos.BASELINE_RIGHT, "Server not available", "Working in offline mode", 10);
                if(!cashRegisterSet) setController("fxml/first.fxml");
            }
            connection = Connection.OFFLINE;
            second.setDisable(true);
            if(currentUser.getUserRole() == User.UserRole.ROLE_OFFICEMAN) third.setDisable(true);
            invalidation.setDisable(true);
            orders.setDisable(true);
        });
    }

    @Override
    public void setOnlineMode() {
        Platform.runLater(() -> {
            if(connection != Connection.ONLINE && PrimaryController.currentUser.getToken() == null && !dialogShown) showTextDialog();
            else if(connection != Connection.ONLINE) showNotification(Pos.BASELINE_RIGHT, "Server available", "Working in online mode", 10);
            connection = Connection.ONLINE;
            second.setDisable(false);
            if(currentUser.getUserRole() == User.UserRole.ROLE_OFFICEMAN) third.setDisable(false);
            invalidation.setDisable(false);
            orders.setDisable(false);
        });
    }

    @Override
    public void onTokenReceived(String token) {
        dialogShown = false;
        currentUser.setToken(token);
        Platform.runLater(() -> showNotification(Pos.BASELINE_RIGHT, "Server available", "Working in online mode", 10));
    }

    private void showTextDialog() {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/loginDialog.fxml"));
        loader.setControllerFactory(c -> new LoginDialogController(this, "Server available", "Enter your password to start online mode."));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(new Scene(root, 400, 272));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        dialogShown = true;
        stage.show();
    }

    private void showNotification(Pos pos, String title, String text, int duration) {
        Notifications.create().position(pos).owner(primaryStage).title(title).text(text).hideCloseButton().hideAfter(Duration.seconds(duration)).showInformation();
    }

    public void cashierBalancing() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(App.class.getResource("css/alert.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure you want to do this?");
        alert.setContentText("This will close out the cash register and generate a balancing report.\n");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.YES) {
                Parent root = null;
                try {
                    Consumer<String> callback = (String str) -> {
                        Alert closeAlert = new Alert(Alert.AlertType.INFORMATION);
                        closeAlert.setTitle("Information Dialog");
                        closeAlert.setHeaderText("The cash register is now closed!");
                        closeAlert.show();
                    };

                    HttpRequest.BodyPublisher bodyPublisher =
                            HttpRequest.BodyPublishers.ofString("");

                    HttpRequest closeCashRegister = HttpUtils.POST(bodyPublisher, DOMAIN +
                                    "/api/cash-register/close?cash_register_id=" + App.getCashRegisterID(),
                            "Authorization", "Bearer " + currentUser.getToken());

                    HttpUtils.send(closeCashRegister, HttpResponse.BodyHandlers.ofString(), s -> Platform.runLater(() -> {
                        Alert closeAlert = new Alert(Alert.AlertType.INFORMATION);
                        closeAlert.setTitle("Information Dialog");
                        closeAlert.setHeaderText("The cash register is now closed!");
                        closeAlert.show();
                    }), () -> System.out.println("Something went wrong. Please try again."));

                    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/invalidateForm.fxml"));
                    fxmlLoader.setControllerFactory(c -> new InvalidationController(true, this));
                    root = fxmlLoader.load();
                    pane.setCenter(root);
                    first.setDisable(true);
                    invalidation.setDisable(true);
                    cashierBalancingButton.setDisable(true);
                    lockButton.setDisable(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else
                alert.close();
        }
    }
}
