package co.blackground.selormfashion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Utils.setMainApp(this);
        Parent root = FXMLLoader.load(getClass().getResource("views/view_home.fxml"));
        primaryStage.setTitle(Constants.APP_NAME);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        mainStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return mainStage;
    }
}
