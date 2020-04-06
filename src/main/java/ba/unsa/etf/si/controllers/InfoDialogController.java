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
    public Button exitButton;
    public JFXButton abort;
    public Label informationLabel;

    public ImageView imageView;


    @FXML
    public void initialize() {
        exitButton.setOnAction(e -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        });

        abort.setOnAction(e -> {
            Stage stage = (Stage) abort.getScene().getWindow();
            stage.close();
        });

    }

    public void setInformationLabel (String input) {
        informationLabel.setText(input);
    }

    public void setWarning () {
        Image  image = new Image(App.class.getResourceAsStream("img/warning.png"));
        try {
          imageView.setImage(image);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
