package co.blackground.selormfashion;

import co.blackground.selormfashion.managers.PersistenceManager;
import co.blackground.selormfashion.models.Customer;
import co.blackground.selormfashion.models.Job;
import co.blackground.selormfashion.models.Measurement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("views/sample.fxml"));
        primaryStage.setTitle(Constants.APP_NAME);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        PersistenceManager.init();

        Customer customer = new Customer("Sir Alan Burns");
        Measurement measurement = new Measurement();
        measurement.setSleeveLength(23);

        Job j = new Job(customer, measurement);
        PersistenceManager.getInstance().save(j);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
