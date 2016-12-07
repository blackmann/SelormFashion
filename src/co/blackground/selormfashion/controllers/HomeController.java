package co.blackground.selormfashion.controllers;

import co.blackground.selormfashion.Main;
import co.blackground.selormfashion.Utils;
import co.blackground.selormfashion.managers.PersistenceManager;
import co.blackground.selormfashion.models.Job;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static co.blackground.selormfashion.Constants.PACKAGE_DIR;

/**
 * This class is responsible for managing the view of home
 * page, as well its interaction.
 */
public class HomeController {

    private ArrayList<Job> jobs;
    private ArrayList<Job.Filter> filters;
    private Date date;
    private String searchKeyword;

    private Stage newJobStage;

    @FXML
    private DatePicker dpFilterDatePicker;

    @FXML
    private ChoiceBox<Job.Filter> cbFilter;

    @FXML
    private VBox vbJobs;

    @FXML
    private GridPane gpMeasurements;

    @FXML
    private GridPane gpTrouserMeasurements;

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

    @FXML
    private Label lblCost;

    @FXML
    private Label lblDeposit;

    @FXML
    private Label lblType;

    @FXML
    private TextField tfSearch;

    // private Alert loading;

    private Job currentJob;
    private Job.Filter selectedFilter;

    /**
     * Called right after declaring controller
     */
    @FXML
    private void initialize() {
        filters = new ArrayList<>();
        filters.add(Job.Filter.ALL);
        filters.add(Job.Filter.TODAY);
        filters.add(Job.Filter.TROUSERS);
        filters.add(Job.Filter.TOPS);
        filters.add(Job.Filter.NOT_DONE);
        filters.add(Job.Filter.DONE);

        btnJobDone.setVisible(false);
        implementCtxtForJobDone();
        setUp();

        cbFilter.setItems(FXCollections.observableList(filters));
        cbFilter.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> setFilter(filters.get(newValue.intValue())));

        /*loading = new Alert(Alert.AlertType.INFORMATION, "Initializing. Please wait...");
        loading.setHeaderText("Getting Ready!");
        loading.setTitle("Loading");*/

        dpFilterDatePicker.setOnAction((e) -> {
            date = java.sql.Date.valueOf(dpFilterDatePicker.getValue());
            try {
                loadJobs();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    /**
     * Sets up the context menu to be shown for jobDone button
     */
    private void implementCtxtForJobDone() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem delivered = new MenuItem("Delivered");
        delivered.setOnAction((event) -> setDelivered(currentJob));

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction((e) -> deleteJob());

        MenuItem edit = new MenuItem("Edit");
        edit.setOnAction((e) -> editJob());

        contextMenu.getItems().addAll(delivered, edit, delete);
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
        lblCustomerMobile.setText(Utils.friendlyText(job.getCustomer().getMobile()));
        int column = 0;
        int row = 0;
        for (Map.Entry<String, Double> entry : job.getTopMeasures().entrySet()) {
            double cValue = entry.getValue();
            if (cValue == 0) continue;
            VBox vbMeasureItem = new VBox(4.0);
            Label lblTitle = new Label(Utils.toTitle(entry.getKey()));
            lblTitle.setFont(new Font(13));

            Label lblValue = new Label(Double.toString(cValue));
            lblValue.setFont(new Font(24));

            vbMeasureItem.getChildren().addAll(lblTitle, lblValue);

            gpMeasurements.add(vbMeasureItem, column++, row);
            if (column == 4) {
                column = 0;
                row++;
            }
        }

        column = 0;
        row = 0;
        for (Map.Entry<String, Double> entry : job.getTrouserMeasurements().entrySet()) {
            double cValue = entry.getValue();
            if (cValue == 0) continue;
            VBox vbMeasureItem = new VBox(4.0);
            Label lblTitle = new Label(Utils.toTitle(entry.getKey()));
            lblTitle.setFont(new Font(13));

            Label lblValue = new Label(Double.toString(cValue));
            lblValue.setFont(new Font(24));

            vbMeasureItem.getChildren().addAll(lblTitle, lblValue);

            gpTrouserMeasurements.add(vbMeasureItem, column++, row);
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

        lblCost.setText(Double.toString(job.getJobCost()));
        lblDeposit.setText(Double.toString(job.getDeposit()));
        // lblType.setText(job.getJobType());

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
     *
     * @throws IOException if file read fails when loading list items fxml
     */
    private void loadJobs(boolean withSearch) throws IOException {
        // remove all children
        vbJobs.getChildren().clear();

        for (Job j : jobs) {
            if (!matchesFilter(j)) continue;
            if (withSearch) {
                if (!matchesSearch(j)) {
                    continue;
                }
            }
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
            // lblJobType.setText(j.getJobType());

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
     * Overloaded method for loading jobs into the list
     *
     * @throws IOException IOException is thrown if the fxml file is
     *                     not found
     */
    private void loadJobs() throws IOException {
        loadJobs(false);
    }

    /**
     * Displays the Add new window to add a new job
     *
     * @throws IOException thrown when the fxml view file is not found
     */
    @FXML
    private NewJobController showAddNew() throws IOException {
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
        Utils.setIcon(newJobStage, mainApp);
        newJobStage.show();

        return controller;
    }

    /**
     * Load all jobs from persistence manager and
     * generate the list and makes some preparations
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
                // loading.show();
                jobs = PersistenceManager.getInstance().getAllJobs();
                System.out.println(jobs.size());
                try {
                    loadJobs();
                    selectFirstJob();

                    cbFilter.setValue(Job.Filter.ALL);
                    setUpDatePicker();

                    // loading.close();
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
        reloadJobs();
    }

    /**
     * Reloads jobs from persistence
     */
    private void reloadJobs() {
        jobs = PersistenceManager.getInstance().getAllJobs();
        try {
            loadJobs();
            selectFirstJob();
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

    /**
     * Sets the filter to be used and refreshes the list
     *
     * @param filter the filter to be set
     */
    private void setFilter(Job.Filter filter) {
        this.selectedFilter = filter;
        // if (selectedFilter == Job.Filter.ALL) date = null;
        try {
            loadJobs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        selectFirstJob();
    }

    /**
     * Verify if a job matches the selected filter
     *
     * @param job the job to process
     * @return Returns true if job passes the selected filter else false
     */
    private boolean matchesFilter(Job job) {
        if (selectedFilter == Job.Filter.ALL) {
            return date == null || isSameDate(date, job.getDateArrived());
        }

        boolean res = true;

        if (selectedFilter == Job.Filter.TODAY) {
            date = new Date();
            res = isSameDate(date, job.getDateArrived());
        }

        if (selectedFilter == Job.Filter.NOT_DONE) {
            res = !job.isDone();
        }

        if (selectedFilter == Job.Filter.DONE) {
            res = job.isDone();
        }

        return res && (date == null || isSameDate(date, job.getDateArrived()));
    }

    /**
     * Checks if both dates are on the same day
     *
     * @param date  first date to compared with the other
     * @param date2 second date to be compared with
     * @return Returns true if both dates are on the same day else false
     */
    @SuppressWarnings("deprecation")
    private boolean isSameDate(Date date, Date date2) {
        return (date.getDate() == date2.getDate() && date.getMonth() == date2.getMonth()
                && date.getYear() == date2.getYear());
    }

    /**
     * Performs a search with the keyword entered into
     * the search textfield
     */
    @FXML
    private void performSearch() {
        searchKeyword = tfSearch.getText();
        if (searchKeyword == null || searchKeyword.isEmpty()) return;

        try {
            loadJobs(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a job's customer name matches the given keyword
     *
     * @param job the job to whose customer is to be checked
     * @return Returns true if the customer has the keyword contained
     * in his/her name else false
     */
    private boolean matchesSearch(Job job) {
        if (searchKeyword == null) return true;
        return job.getCustomer().getName().toLowerCase().contains(searchKeyword.toLowerCase());
    }

    private void setUpDatePicker() {

    }

    /**
     * Shows the meta information of the app
     *
     * @throws IOException Throws IOException if the fxml file is not found
     */
    @FXML
    private void showAbout() throws IOException {
        AnchorPane apAbout = FXMLLoader.load(getClass().getResource(PACKAGE_DIR + "views/view_about.fxml"));
        Stage aboutStage = new Stage();
        aboutStage.setScene(new Scene(apAbout));

        aboutStage.initOwner(Utils.getMainApp().getPrimaryStage());
        aboutStage.initModality(Modality.WINDOW_MODAL);
        aboutStage.setResizable(false);

        aboutStage.show();
    }

    /**
     * Closes the app
     */
    @FXML
    private void close() {
        Utils.getMainApp().close();
    }

    /**
     * Reset a password for application
     */
    @FXML
    private void resetPassword() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PACKAGE_DIR + "views/view_reset_password.fxml"));
        AnchorPane pane = loader.load();
        ResetPasswordController controller = loader.getController();

        Stage resetPasswordStage = new Stage();
        resetPasswordStage.setScene(new Scene(pane));
        resetPasswordStage.setResizable(false);
        resetPasswordStage.initModality(Modality.WINDOW_MODAL);
        resetPasswordStage.initOwner(Utils.getMainApp().getPrimaryStage());

        controller.setStage(resetPasswordStage);
        resetPasswordStage.show();
    }

    /**
     * Clears the filter
     */
    @FXML
    private void clearFilter() {
        date = null;
        cbFilter.setValue(Job.Filter.ALL);
    }

    private void deleteJob() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete this job?");
        alert.setHeaderText("Sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            PersistenceManager.getInstance().deleteJob(currentJob);
            reloadJobs();
        }

    }

    private void editJob() {
        try {
            showAddNew().setActiveJob(currentJob);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
