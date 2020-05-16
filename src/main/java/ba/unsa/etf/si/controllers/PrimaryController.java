package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.User;
import ba.unsa.etf.si.models.enums.Connection;
import ba.unsa.etf.si.interfaces.ConnectivityObserver;
import ba.unsa.etf.si.interfaces.ReceiptLoader;
import ba.unsa.etf.si.interfaces.TokenReceiver;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.javafx.NotificationUtils;
import ba.unsa.etf.si.utility.javafx.StageUtils;
import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.time.LocalDate;

import static ba.unsa.etf.si.App.dailyReportService;
import static ba.unsa.etf.si.App.primaryStage;
import static ba.unsa.etf.si.services.DailyReportService.dbMinDate;

public class PrimaryController implements ReceiptLoader, ConnectivityObserver, TokenReceiver {

    @FXML
    private BorderPane pane;
    @FXML
    private JFXButton hideBtn, showBtn, first, second, invalidation, orders, tables;
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
        invalidation.setOnAction(e -> loadCustomController("fxml/invalidateForm.fxml", c -> new InvalidationController(this)));
        orders.setOnAction(e -> loadCustomController("fxml/orders.fxml", c -> new OrdersController(this)));
        hideBtn.setOnAction(e -> hideMenu());
        showBtn.setOnAction(e -> showMenu());
        tables.setOnAction(e -> setController("fxml/tables.fxml"));
        tables.setText(App.cashRegister.getPlaceName());
        tables.setVisible(App.cashRegister.isRestaurant());
        welcomeText.setText("Welcome, " + currentUser.getName());
        first.fire();

        dailyReportService.setPrimaryController(this);
        dailyReportService.run();
        if (currentUser.isUsingOtp())
            settings();
    }

    private void loadCustomController(String fxml, Callback<Class<?>, Object> controllerFactory) {
        cashRegisterSet = false;
        pane.setCenter(FXMLUtils.loadCustomController(fxml, controllerFactory));
    }

    public void setController(String fxml) {
        cashRegisterSet = fxml.equals("fxml/first.fxml");
        pane.setCenter(FXMLUtils.loadController(fxml));
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
        dailyReportService.resetDailyReportCheck();
        StageUtils.centerStage(primaryStage, 800, 600);
        primaryStage.setScene(new Scene(FXMLUtils.loadController("fxml/loginForm.fxml")));
        primaryStage.show();
    }

    @Override
    public void onReceiptLoaded(Receipt receipt) {
        loadCustomController("fxml/first.fxml", c -> new MyCashRegisterController(receipt));
    }

    public void lock() {
        Parent root = FXMLUtils.loadCustomController("fxml/lock.fxml", c -> new LockController(currentUser));
        Scene scene = pane.getScene();
        root.translateYProperty().set(-scene.getHeight());
        parentContainer.getChildren().add(root);
        StageUtils.setAnimation(root, e -> parentContainer.getChildren().remove(pane)).play();
    }

    public void settings() {
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
    }

    @Override
    public void setOfflineMode() {
        Platform.runLater(() -> {
            if (connection != Connection.OFFLINE) {
                NotificationUtils.showInformation(Pos.BASELINE_RIGHT, "Server not available", "Working in offline mode", 10);
                if (!cashRegisterSet) setController("fxml/first.fxml");
            }
            connection = Connection.OFFLINE;
            second.setDisable(true);
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
                NotificationUtils.showInformation(Pos.BASELINE_RIGHT, "Server available", "Working in online mode", 10);
            connection = Connection.ONLINE;
            second.setDisable(false);
            invalidation.setDisable(false);
            orders.setDisable(false);
        });
    }

    @Override
    public void onTokenReceived(String token) {
        dialogShown = false;
        currentUser.setToken(token);
        Platform.runLater(() -> NotificationUtils.showInformation(Pos.BASELINE_RIGHT, "Server available", "Working in online mode", 10));
    }

    private void showTextDialog() {
        dialogShown = true;
        Parent root = FXMLUtils.loadCustomController("fxml/loginDialog.fxml", c -> new LoginDialogController(this, "Server available", "Enter your password to start online mode."));
        Stage stage = new Stage();
        StageUtils.setStage(stage, "Login", false, StageStyle.UNDECORATED, Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root, 400, 272));
        stage.show();
    }

    public void dailyReport(boolean closeOut) {
        if (closeOut || dbMinDate.isBefore(LocalDate.now()))
            loadCustomController("fxml/invalidateForm.fxml",
                    c -> new InvalidationController(closeOut, true, this));
    }
}
