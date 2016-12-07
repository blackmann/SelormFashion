package co.blackground.selormfashion;

import co.blackground.selormfashion.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Utils.setMainApp(this);
        Utils.setUpEnvironment();
        Parent root = FXMLLoader.load(getClass().getResource("views/view_home.fxml"));
        primaryStage.setTitle(Constants.APP_NAME);
        primaryStage.setScene(new Scene(root));
        mainStage = primaryStage;
        Utils.setIcon(mainStage, this);
        if (showLogInScreen()) {
            primaryStage.show();
        } else {
            close();
        }
    }

    public Stage getPrimaryStage() {
        return mainStage;
    }

    public void close() {
        mainStage.close();
        System.exit(0);
    }

    private boolean showLogInScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/view_login.fxml"));
            AnchorPane pane = loader.load();

            LoginController controller = loader.getController();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(pane));
            loginStage.setResizable(false);
            controller.setStage(loginStage);
            loginStage.setTitle("Login");
            Utils.setIcon(loginStage, this);

            loginStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Utils.isLogged();
    }
}
