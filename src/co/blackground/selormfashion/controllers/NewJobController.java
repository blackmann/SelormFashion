package co.blackground.selormfashion.controllers;

import co.blackground.selormfashion.Constants;
import co.blackground.selormfashion.Utils;
import co.blackground.selormfashion.extras.FormField;
import co.blackground.selormfashion.models.Customer;
import co.blackground.selormfashion.models.Job;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static co.blackground.selormfashion.Constants.PACKAGE_DIR;
import static co.blackground.selormfashion.models.Job.Type.TOPS;
import static co.blackground.selormfashion.models.Job.Type.TROUSER;

public class NewJobController {

    private ArrayList<FormField> formFields;
    private Job activeJob;

    private File userPhoto;
    private File userStyle;

    private boolean firstRun = true;

    @FXML
    private ToggleButton btnTop;

    @FXML
    private ToggleButton btnTrouser;

    @FXML
    private TextField tfCustomerName;

    @FXML
    private TextField tfCustomerMobile;

    @FXML
    private GridPane gpForm;

    @FXML
    private ImageView imgUser;

    @FXML
    private ImageView imgStyle;

    @FXML
    private TextField tfDeposit;

    @FXML
    private TextField tfAmount;

    private HomeController homeController;
    private String selectedType;

    @FXML
    private void initialize() {
        formFields = new ArrayList<>();
        activeJob = new Job();
        ToggleGroup tgJobType = new ToggleGroup();

        btnTop.setToggleGroup(tgJobType);
        btnTrouser.setToggleGroup(tgJobType);

        imgStyle.setImage(new Image(getClass().getResource(PACKAGE_DIR + "views/resources/default-placeholder.png").toString()));
        imgUser.setImage(new Image(getClass().getResource(PACKAGE_DIR + "views/resources/nobody_m.jpg").toString()));
        // TOP is the default type
        toggleToTop();
        firstRun = false;
    }

    /**
     * Sets the type of job to be a #Job.Type.TOPS
     */
    @FXML
    private void toggleToTop() {
        if (!firstRun) {
            setMeasuresToJob();
        }
        this.selectedType = TOPS;
        btnTop.setSelected(true);
        setUpForm();
    }

    /**
     * Sets the type of job to be a #Job.Type.TROUSER
     */
    @FXML
    private void toggleToTrouser() {
        if (!firstRun) {
            setMeasuresToJob();
        }
        this.selectedType = TROUSER;
        btnTrouser.setSelected(true);
        setUpForm();
    }

    /**
     * Switches between showing a TOPS form or a TROUSER form
     */
    private void setUpForm() {
        switch (selectedType) {
            case TOPS:
                setUpTopsForm();
                break;
            case TROUSER:
                setUpTrouserForm();
                break;
        }
    }

    /**
     * Arranges a TOPS form
     */
    private void setUpTopsForm() {
        formFields.clear();
        formFields.add(new FormField("Back", "back", fieldValue("back")));
        formFields.add(new FormField("Length", "length", fieldValue("length")));
        formFields.add(new FormField("Sleeve Length", "sleeve_length", fieldValue("sleeve_length")));
        formFields.add(new FormField("Sleeve Bass", "sleeve_bass", fieldValue("sleeve_bass")));
        formFields.add(new FormField("Sleeve Thighs", "sleeve_thighs", fieldValue("sleeve_thighs")));
        formFields.add(new FormField("Sleeve TBass", "sleeve_tbass", fieldValue("sleeve_tbass")));
        formFields.add(new FormField("Chest", "chest", fieldValue("chest")));
        formFields.add(new FormField("Stomach", "stomach", fieldValue("stomach")));
        formFields.add(new FormField("Neck", "neck", fieldValue("neck")));

        createForm();
    }

    /**
     * Arranges a TROUSER form
     */
    private void setUpTrouserForm() {
        formFields.clear();
        formFields.add(new FormField("Waist", "waist", fieldValue("waist")));
        formFields.add(new FormField("Length", "length", fieldValue("length")));
        formFields.add(new FormField("Thighs", "thighs", fieldValue("thighs")));
        formFields.add(new FormField("Bass", "bass", fieldValue("bass")));
        formFields.add(new FormField("Hips", "hips", fieldValue("hips")));

        createForm();
    }

    /**
     * Creates the form to be interacted with
     */
    private void createForm() {
        gpForm.getChildren().clear();
        int row = 0;
        for (FormField f : formFields) {
            Label lblTitle = new Label(f.getTitle());
            lblTitle.setFont(new Font(13.0));
            gpForm.add(lblTitle, 0, row);
            TextField tfValue = new TextField(f.getValue());
            Utils.acceptNumberOnly(tfValue);
            gpForm.add(tfValue, 1, row++);
        }
    }

    /**
     * Collects form data and creates a job to persistence
     */
    @FXML
    private void saveJob() throws IOException {
        Customer customer = activeJob.getCustomer();
        customer.setMobile(tfCustomerMobile.getText());
        customer.setName(tfCustomerName.getText());

        setMeasuresToJob();

        if (userStyle != null) {
            File dest = new File(Constants.STYLES_DIR + userStyle.getName());
            FileUtils.copyFile(userStyle, dest);
            activeJob.setUserStyle(dest.toString());
        }

        if (userPhoto != null) {
            File photoDest = new File(Constants.PHOTOS_DIR + userPhoto.getName());
            FileUtils.copyFile(userPhoto, photoDest);
            activeJob.setUserPhoto(photoDest.toString());
        }

        activeJob.setDeposit(toDouble(tfDeposit.getText()));
        activeJob.setJobCost(toDouble(tfAmount.getText()));
        activeJob.setDateArrived(new Date());
        activeJob.save();
        homeController.savedNew();
    }

    /**
     * Opens a chooser to select a user photo
     */
    @FXML
    private void selectUserPhoto() throws FileNotFoundException {
        userPhoto = choosePhoto("Customer's Photo");
        if (userPhoto == null) return;
        imgUser.setImage(new Image(new FileInputStream(userPhoto)));
    }

    /**
     * Opens a chooser to select user's style
     */
    @FXML
    private void selectUserStyle() throws FileNotFoundException {
        userStyle = choosePhoto("Dress Style");
        if (userStyle == null) return;
        imgStyle.setImage(new Image(new FileInputStream(userStyle)));
    }

    /**
     * Resets the user photo to null
     */
    @FXML
    private void clearUserPhoto() {
        userPhoto = null;
        imgUser.setImage(new Image(getClass().getResource(PACKAGE_DIR + "views/resources/nobody_m.jpg").toString()));
    }

    /**
     * Resets the user style to null
     */
    @FXML
    private void clearUserStyle() {
        userStyle = null;
        imgStyle.setImage(new Image(getClass().getResource(PACKAGE_DIR + "views/resources/default-placeholder.png").toString()));
    }

    /**
     * Opens a file chooser with @param title
     *
     * @param title the title to be displayed
     * @return returns the chosen file
     */
    private File choosePhoto(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"));

        fileChooser.setTitle("Select " + title);
        return fileChooser.showOpenDialog(Utils.getMainApp().getPrimaryStage());
    }

    /**
     * Returns the value of a job measure field
     *
     * @param field which field to request/get
     * @return the value of that field
     */
    private String fieldValue(String field) {
        if (selectedType.equals(TROUSER)) {
            return string(activeJob.getTrouserMeasure(field));
        }
        return string(activeJob.getTopMeasure(field));
    }

    void setHomeController(HomeController stage) {
        this.homeController = stage;
    }

    private String string(double val) {
        if (val == 0) return null;
        return Double.toString(val);
    }

    private double toDouble(String s) {
        if (s == null || s.isEmpty()) return 0;
        return Double.valueOf(s);
    }

    private void setMeasuresToJob() {
        ObservableList<Node> formNodes = gpForm.getChildren();
        int i = 0;
        int currentPosition = 0;
        while (i < formNodes.size()) {
            i++;
            TextField tfField = (TextField) formNodes.get(i++);
            if (selectedType.equals(TOPS)) {
                activeJob.addTopMeasure(formFields.get(currentPosition++).getName(), toDouble(tfField.getText()));
            } else if (selectedType.equals(TROUSER)) {
                activeJob.addTrouserMeasure(formFields.get(currentPosition++).getName(), toDouble(tfField.getText()));
            }
        }
    }

    public void setActiveJob(Job job) {
        this.activeJob = job;
        setUpTopsForm();
        setPhotos();
        tfCustomerName.setText(activeJob.getCustomer().getName());
        tfAmount.setText(Double.toString(activeJob.getJobCost()));
        tfDeposit.setText(Double.toString(activeJob.getDeposit()));
        tfCustomerMobile.setText(activeJob.getCustomer().getMobile());
    }

    private void setPhotos() {
        if (Utils.isValidPhoto(activeJob.getUserPhoto())) {
            Utils.setImage(imgUser, activeJob.getUserPhoto());
        } else {
            imgUser.setImage(new Image(getClass().getResource(PACKAGE_DIR + "views/resources/nobody_m.jpg").toString()));
        }

        if (Utils.isValidPhoto(activeJob.getUserStyle())) {
            Utils.setImage(imgStyle, activeJob.getUserStyle());
        } else {
            imgStyle.setImage(new Image(getClass().getResource(PACKAGE_DIR + "views/resources/default-placeholder.png").toString()));
        }
    }
}
