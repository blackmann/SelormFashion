package co.blackground.selormfashion.controllers;

import co.blackground.selormfashion.Main;
import co.blackground.selormfashion.Utils;
import co.blackground.selormfashion.managers.PersistenceManager;
import co.blackground.selormfashion.models.Job;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class is responsible for managing the view of home
 * page, as well its interaction.
 */
public class HomeController {

    private ArrayList<Job> jobs;
    private ArrayList<Job.Filter> filters;
    private String searchKeyword;

    @FXML
    private DatePicker dpFilterDatePicker;

    @FXML
    private ToggleButton btnToday;

    @FXML
    private ToggleButton btnAll;

    @FXML
    private ToggleButton btnNotDone;

    @FXML
    private Button btnAddNew;

    @FXML
    private Button btnSearch;

    @FXML
    private VBox vbJobs;

    @FXML
    private GridPane gpMeasurements;

    @FXML
    private Label lblCustomerMobile;

    @FXML
    private Label lblCustomerName;

    @FXML
    private ImageView ivJobDone;

    @FXML
    private ImageView ivCustomerPhoto;

    @FXML
    private ImageView ivStyle;

    @FXML
    private void initialize() throws Exception {
        ToggleGroup filterBtnGroup = new ToggleGroup();
        btnAll.setToggleGroup(filterBtnGroup);
        btnToday.setToggleGroup(filterBtnGroup);
        btnNotDone.setToggleGroup(filterBtnGroup);
        setUp();
    }

    private void setDetails(Job job) {
        gpMeasurements.getChildren().clear();
        lblCustomerName.setText(job.getCustomer().getName());
        int column = 0;
        int row = 0;
        for (Map.Entry<String, Double> entry : job.getMeasures().entrySet()) {
            VBox vbMeasureItem = new VBox(4.0);
            Label lblTitle = new Label(entry.getKey());
            Label lblValue = new Label(Double.toString(entry.getValue()));

            vbMeasureItem.getChildren().addAll(lblTitle, lblValue);

            gpMeasurements.add(vbMeasureItem, column++, row);
            if (column == 4) {
                column = 0;
                row++;
            }
        }
    }

    private void loadJobs() throws IOException {
        // remove all children
        vbJobs.getChildren().clear();

        for (Job j : jobs) {
            AnchorPane apJobItem = FXMLLoader.load(getClass().getResource("../views/view_job_item.fxml"));
            apJobItem.setOnMouseClicked((event) -> setDetails(j));
            vbJobs.getChildren().add(apJobItem);
        }
    }

    @FXML
    private void showAddNew() throws IOException {
        AnchorPane apNewJob = FXMLLoader.load(getClass().getResource("../views/view_add_job.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(apNewJob));

        Main mainApp = Utils.getMainApp();
        Stage primaryStage = mainApp.getPrimaryStage();
        stage.initOwner(primaryStage.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);

        stage.show();
    }

    /**
     * Load all jobs from persistence manager and
     * generate the list
     */
    public void setUp() {
        Task<Void> getJobs = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                PersistenceManager.init();
                return null;
            }

            @Override
            protected void succeeded() {
                jobs = PersistenceManager.getInstance().getAllJobs();
                System.out.println(jobs.size());
                try {
                    loadJobs();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread getJobsThread = new Thread(getJobs);
        getJobsThread.start();
    }
}
