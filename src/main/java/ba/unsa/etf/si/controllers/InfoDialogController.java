package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.App;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class InfoDialogController {

    @FXML private Button exitButton;
    @FXML private JFXButton abort;
    @FXML private Label informationLabel;
    @FXML private ImageView imageView;

    @FXML
    public void initialize() {
        exitButton.setOnAction(e -> ((Stage) exitButton.getScene().getWindow()).close());
        abort.setOnAction(e -> ((Stage) abort.getScene().getWindow()).close());
    }

    public void setInformationLabel (String input) {
        informationLabel.setText(input);
    }

    public void setWarning () {
        Image  image = new Image(App.class.getResourceAsStream("img/warning.png"));
        imageView.setImage(image);
    }
}
