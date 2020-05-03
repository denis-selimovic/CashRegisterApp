package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.models.enums.Connection;
import ba.unsa.etf.si.utility.javafx.CustomFXMLLoader;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.interfaces.ConnectivityObserver;
import ba.unsa.etf.si.utility.interfaces.ReceiptLoader;
import ba.unsa.etf.si.utility.interfaces.TokenReceiver;
import ba.unsa.etf.si.utility.javafx.NotificationUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import ba.unsa.etf.si.routes.CashRegisterRoutes;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import java.util.Set;
import org.json.JSONObject;
import java.io.IOException;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.primaryStage;

public class PrimaryController implements ReceiptLoader, ConnectivityObserver, TokenReceiver {

    @FXML
    private BorderPane pane;
    @FXML
    private JFXButton hideBtn, showBtn, first, second, third, invalidation, orders, lockButton, cashierBalancingButton;
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

    private final Consumer<String> uuidSetterCallback = str -> {
        try {
            JSONObject js = new JSONObject(str);
            App.setUUID(js.getString("uuid"));
            first.setDisable(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    };
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
        if (currentUser.isUsingOtp())
            settings();
        //
        //Mjesto gdje se trenutno nalazi UUID poziv
        if (connection==Connection.ONLINE) {
            first.setDisable(true);
            CashRegisterRoutes.getCashRegisterData(currentUser.getToken(), uuidSetterCallback, () -> {
                System.out.println("error has occured");
                first.setDisable(false);
            });
        }
    }

    private void loadCustomController(String fxml, Callback<Class<?>, Object> controllerFactory) {
        cashRegisterSet = false;
        try {
            pane.setCenter(FXMLUtils.loadCustomController(fxml, controllerFactory));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setController(String fxml) {
        cashRegisterSet = fxml.equals("fxml/first.fxml");
        try {
            pane.setCenter(FXMLUtils.loadController(fxml));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            StageUtils.centerStage(primaryStage, 800, 600);
            primaryStage.setScene(new Scene(FXMLUtils.loadController("fxml/loginForm.fxml")));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiptLoaded(Receipt receipt) {
        loadCustomController("fxml/first.fxml", c -> new MyCashRegisterController(receipt));
    }

    public void lock(ActionEvent event) {
        try {
            Parent root = FXMLUtils.loadCustomController("fxml/lock.fxml", c -> new LockController(currentUser));
            Scene scene = pane.getScene();
            root.translateYProperty().set(-scene.getHeight());
            parentContainer.getChildren().add(root);
            StageUtils.setAnimation(root, e -> parentContainer.getChildren().remove(pane)).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void settings() {
        try {
            Parent settings = FXMLUtils.loadCustomController("fxml/settings.fxml", c -> new SettingsController(false));
            Stage stage = new Stage();


            stage.setOnCloseRequest(windowEvent -> {
                if (currentUser.isUsingOtp()) {
                    NotificationUtils.showAlert("Warning Dialog", "Oops! Looks like you're using your one-time password." +
                                    "\nYou cannot close this window until you change your password!",
                            Alert.AlertType.WARNING);
                    windowEvent.consume();
                }
            });

            StageUtils.setStage(stage, "Settings", false, StageStyle.DECORATED, Modality.APPLICATION_MODAL);
            StageUtils.centerStage(stage, 700, 500);
            stage.setScene(new Scene(settings));
            stage.getIcons().add(new Image("/ba/unsa/etf/si/img/settings.png"));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOfflineMode() {
        Platform.runLater(() -> {
            if (connection != Connection.OFFLINE) {
                NotificationUtils.showNotification(Pos.BASELINE_RIGHT, "Server not available", "Working in offline mode", 10);
                if (!cashRegisterSet) setController("fxml/first.fxml");
            }
            connection = Connection.OFFLINE;
            second.setDisable(true);
            if (currentUser.getUserRole() == User.UserRole.ROLE_OFFICEMAN) third.setDisable(true);
            invalidation.setDisable(true);
            orders.setDisable(true);

        });
    }

    @Override
    public void setOnlineMode() {
        Platform.runLater(() -> {
            if (connection != Connection.ONLINE && PrimaryController.currentUser.getToken() == null && !dialogShown)
                showTextDialog();
            else if (connection != Connection.ONLINE)
                NotificationUtils.showNotification(Pos.BASELINE_RIGHT, "Server available", "Working in online mode", 10);
            connection = Connection.ONLINE;
            second.setDisable(false);
            if (currentUser.getUserRole() == User.UserRole.ROLE_OFFICEMAN) third.setDisable(false);
            invalidation.setDisable(false);
            orders.setDisable(false);
        });
    }

    @Override
    public void onTokenReceived(String token) {
        dialogShown = false;
        currentUser.setToken(token);
        Platform.runLater(() -> NotificationUtils.showNotification(Pos.BASELINE_RIGHT, "Server available", "Working in online mode", 10));
    }

    private void showTextDialog() {
        dialogShown = true;
        try {
            Parent root = FXMLUtils.loadCustomController("fxml/loginDialog.fxml", c -> new LoginDialogController(this, "Server available", "Enter your password to start online mode."));
            Stage stage = new Stage();
            StageUtils.setStage(stage, "Login", false, StageStyle.UNDECORATED, Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 400, 272));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cashierBalancing() {
        NotificationUtils.showAlert("Confirmation dialog", "Are you sure you want to do this?\n" +
                        "This will close out the cash register and generate a balancing report.",
                Alert.AlertType.CONFIRMATION, ButtonType.YES, ButtonType.NO).ifPresent(buttonType -> {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.YES) {
                CashRegisterRoutes.closeCashRegister(currentUser.getToken(), s -> Platform.runLater(() ->
                                NotificationUtils.showAlert("Information Dialog", "The cash register is now closed!", Alert.AlertType.INFORMATION)),
                        () -> System.out.println("Could not close cash register!"));
                loadCustomController("fxml/invalidateForm.fxml", c -> new InvalidationController(true, this));
                first.setDisable(true);
                invalidation.setDisable(true);
                cashierBalancingButton.setDisable(true);
                lockButton.setDisable(true);
            }
        });
    }
}
