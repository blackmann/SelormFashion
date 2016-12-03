package co.blackground.selormfashion.controllers;

import co.blackground.selormfashion.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ResetPasswordController {

    private Stage stage;

    @FXML
    private PasswordField pfOld;

    @FXML
    private PasswordField pfNew;

    @FXML
    private PasswordField pfConfirm;

    @FXML
    private Label lblFeedback;

    void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void resetPassword() {
        if (Utils.verifyPassword(pfOld.getText())) {
            String newPass = pfNew.getText();
            if (newPass == null || newPass.isEmpty() || newPass.length() < 4) {
                lblFeedback.setText("Enter a new password. Longer than 4 characters.");
                return;
            }
            String confirmed = pfConfirm.getText();
            if (!newPass.equals(confirmed)) {
                lblFeedback.setText("New passwords does not match.");
                return;
            }

            Utils.setNewPassword(confirmed);
        } else {
            lblFeedback.setText("The old password you entered is incorrect!");
        }

        stage.close();
    }
}
