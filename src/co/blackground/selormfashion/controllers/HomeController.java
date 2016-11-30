package co.blackground.selormfashion.controllers;

import co.blackground.selormfashion.Main;
import co.blackground.selormfashion.Utils;
import co.blackground.selormfashion.managers.PersistenceManager;
import co.blackground.selormfashion.models.Job;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static co.blackground.selormfashion.Constants.PACKAGE_DIR;

/**
 * This class is responsible for managing the view of home
 * page, as well its interaction.
 */
public class HomeController {

    private static final int DONE_COLOR = 1;

    private ArrayList<Job> jobs;
    private ArrayList<Job.Filter> filters;
    private String searchKeyword;

    private Stage newJobStage;

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
    private Button btnJobDone;

    @FXML
    private ImageView ivCustomerPhoto;

    @FXML
    private ImageView ivStyle;


    private Job currentJob;

    /**
     * Called right after declaring controller
     */
    @FXML
    private void initialize() {
        ToggleGroup filterBtnGroup = new ToggleGroup();
        btnAll.setToggleGroup(filterBtnGroup);
        btnToday.setToggleGroup(filterBtnGroup);
        btnNotDone.setToggleGroup(filterBtnGroup);

        btnJobDone.setVisible(false);
        implementCtxtForJobDone();
        setUp();
    }

    /**
     * Sets up the context menu to be shown for jobDone button
     */
    private void implementCtxtForJobDone() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem delivered = new MenuItem("Delivered");
        delivered.setOnAction((event) -> {
            setDelivered(currentJob);
        });

        contextMenu.getItems().addAll(delivered);
        btnJobDone.setContextMenu(contextMenu);
    }

    /**
     * Displays the details of a job
     *
     * @param job the job to be viewed
     */
    private void setDetails(Job job) {
        this.currentJob = job;
        btnJobDone.setVisible(true);
        gpMeasurements.getChildren().clear();
        lblCustomerName.setText(job.getCustomer().getName());
        lblCustomerMobile.setText(job.getCustomer().getMobile());
        int column = 0;
        int row = 0;
        for (Map.Entry<String, Double> entry : job.getMeasures().entrySet()) {
            VBox vbMeasureItem = new VBox(4.0);
            Label lblTitle = new Label(Utils.toTitle(entry.getKey()));
            lblTitle.setFont(new Font(13));

            Label lblValue = new Label(Double.toString(entry.getValue()));
            lblValue.setFont(new Font(24));

            vbMeasureItem.getChildren().addAll(lblTitle, lblValue);

            gpMeasurements.add(vbMeasureItem, column++, row);
            if (column == 4) {
                column = 0;
                row++;
            }
        }

        if (validPhoto(job.getUserPhoto())) {
            setImage(ivCustomerPhoto, job.getUserPhoto());
        } else {
            ivCustomerPhoto.setImage(new Image(getClass().getResource(PACKAGE_DIR + "views/resources/nobody_m.jpg").toString()));
        }

        if (validPhoto(job.getUserStyle())) {
            setImage(ivStyle, job.getUserStyle());
        } else {
            ivStyle.setImage(new Image(getClass().getResource(PACKAGE_DIR + "views/resources/default-placeholder.png").toString()));
        }

        setBtnStateForDone(job);
    }

    /**
     * Checks if @param photo is valid to be set
     *
     * @param photo the photo to validate
     * @return true if photo is valid else false
     */
    private boolean validPhoto(String photo) {
        return photo != null && !photo.isEmpty();
    }

    /**
     * Set the @param photo into @param iv
     *
     * @param iv    the ImageView to
     * @param photo photo to be set
     */
    private void setImage(ImageView iv, String photo) {
        try {
            iv.setImage(new Image(new FileInputStream(new File(photo))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads saved jobs into list
     * @throws IOException if file read fails when loading list items fxml
     */
    private void loadJobs() throws IOException {
        // remove all children
        vbJobs.getChildren().clear();

        for (Job j : jobs) {
            AnchorPane apJobItem = FXMLLoader.load(getClass().getResource(PACKAGE_DIR + "views/view_job_item.fxml"));
            apJobItem.setOnMouseClicked((event) -> setDetails(j));
            Label lblCustomerName = (Label) apJobItem.lookup("#customerName");
            lblCustomerName.setText(j.getCustomer().getName());

            ImageView ivCustomerPhoto = (ImageView) apJobItem.lookup("#customerPhoto");
            if (validPhoto(j.getUserPhoto())) {
                setImage(ivCustomerPhoto, j.getUserPhoto());
            } else {
                ivCustomerPhoto.setImage(new Image(getClass().getResource(PACKAGE_DIR + "views/resources/nobody_m.jpg").toString()));
            }

            Label lblJobType = (Label) apJobItem.lookup("#jobType");
            lblJobType.setText(j.getJobType());

            Label lblArrivalDate = (Label) apJobItem.lookup("#arrivalDate");
            lblArrivalDate.setText(Utils.formatDate(j.getDateArrived()));

            Circle cDone = (Circle) apJobItem.lookup("#isDone");
            if (j.isDone()) {
                cDone.setFill(Color.DEEPSKYBLUE);
            } else {
                cDone.setFill(Color.GRAY);
            }

            if (j.isDelivered()) {
                cDone.setFill(Color.MEDIUMSPRINGGREEN);
            }

            vbJobs.getChildren().add(apJobItem);
        }
    }

    /**
     * Displays the Add new window to add a new job
     * @throws IOException thrown when the fxml view file is not found
     */
    @FXML
    private void showAddNew() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PACKAGE_DIR + "views/view_add_job.fxml"));
        AnchorPane apNewJob = loader.load();
        NewJobController controller = loader.getController();
        controller.setHomeController(this);
        newJobStage = new Stage();
        newJobStage.setScene(new Scene(apNewJob));
        newJobStage.setTitle("Add New");

        Main mainApp = Utils.getMainApp();
        Stage primaryStage = mainApp.getPrimaryStage();
        newJobStage.initOwner(primaryStage);
        newJobStage.initModality(Modality.WINDOW_MODAL);

        newJobStage.show();
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
                    selectFirstJob();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread getJobsThread = new Thread(getJobs);
        getJobsThread.start();
    }

    /**
     * Reloads list when new job has been added
     */
    void savedNew() {
        newJobStage.close();
        jobs = PersistenceManager.getInstance().getAllJobs();
        try {
            loadJobs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Selects first job to set details
     */
    private void selectFirstJob() {
        if (jobs.size() > 0) {
            setDetails(jobs.get(0));
        }
    }

    /**
     * Sets a job as done or not
     */
    @FXML
    private void toggleDone() {
        if (currentJob != null) {
            currentJob.setDone(!currentJob.isDone());
            currentJob.save();

            setBtnStateForDone(currentJob);
        }
    }

    /**
     * Set button state for done
     */
    private void setBtnStateForDone(Job job) {
        if (job.isDone()) {
            btnJobDone.setText("Completed!");
            btnJobDone.setStyle("-fx-background-color:#00bfff;-fx-text-fill:#fff");
        } else {
            btnJobDone.setText("Done");
            btnJobDone.setStyle("-fx-background-color:#cccccc;");
        }

        if (job.isDelivered()) {
            btnJobDone.setText("Delivered!");
            btnJobDone.setStyle("-fx-background-color:#00fa9a;-fx-text-fill:#000");
        }
    }

    /**
     * Sets a job as delivered
     */
    private void setDelivered(Job job) {
        if (job == null) return;
        job.setDelivered(true);
        job.save();

        setBtnStateForDone(job);
    }
}
