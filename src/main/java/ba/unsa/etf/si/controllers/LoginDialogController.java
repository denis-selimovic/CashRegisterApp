package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.utility.interfaces.TokenReceiver;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class LoginDialogController {

    @FXML
    private PasswordField password;
    @FXML
    private Label title, header;
    @FXML
    private JFXButton login;

    private TokenReceiver receiver;
    private final String dialogTitle;
    private final String dialogHeader;

    public LoginDialogController(TokenReceiver receiver, String dialogTitle, String dialogHeader) {
        this.receiver = receiver;
        this.dialogTitle = dialogTitle;
        this.dialogHeader = dialogHeader;
    }

    @FXML
    private void initialize() {
        title.setText(dialogTitle);
        header.setText(dialogHeader);

    }
}
