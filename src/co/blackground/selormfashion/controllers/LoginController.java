package co.blackground.selormfashion.controllers;

import co.blackground.selormfashion.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private PasswordField pfPassword;

    @FXML
    private Label lblFeedback;

    private Stage stage;


    @FXML
    private void initialize() {
    }

    @FXML
    private void verifyPassword() {
        if (Utils.verifyPassword(pfPassword.getText())) {
            stage.close();
        } else {
            lblFeedback.setText("Wrong password. Retry!");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        stage.close();
    }
}
