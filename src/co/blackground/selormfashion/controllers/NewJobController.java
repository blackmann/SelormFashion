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

import static co.blackground.selormfashion.models.Job.Type.TOPS;
import static co.blackground.selormfashion.models.Job.Type.TROUSER;

public class NewJobController {

    private ArrayList<FormField> formFields;
    private Job activeJob;

    private File userPhoto;
    private File userStyle;

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

    private HomeController homeController;

    @FXML
    private void initialize() {
        formFields = new ArrayList<>();
        activeJob = new Job();
        ToggleGroup tgJobType = new ToggleGroup();

        btnTop.setToggleGroup(tgJobType);
        btnTrouser.setToggleGroup(tgJobType);

        imgStyle.setImage(new Image(getClass().getResource("../views/resources/default-placeholder.png").toString()));
        imgUser.setImage(new Image(getClass().getResource("../views/resources/nobody_m.jpg").toString()));
        // TOP is the default type
        toggleToTop();
    }

    /**
     * Sets the type of job to be a #Job.Type.TOPS
     */
    @FXML
    private void toggleToTop() {
        activeJob.setJobType(TOPS);
        btnTop.setSelected(true);
        setUpForm();
    }

    /**
     * Sets the type of job to be a #Job.Type.TROUSER
     */
    @FXML
    private void toggleToTrouser() {
        activeJob.setJobType(TROUSER);
        btnTrouser.setSelected(true);
        setUpForm();
    }

    /**
     * Switches between showing a TOPS form or a TROUSER form
     */
    private void setUpForm() {
        switch (activeJob.getJobType()) {
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

        ObservableList<Node> formNodes = gpForm.getChildren();
        int i = 0;
        int currentPosition = 0;
        while (i < formNodes.size()) {
            i++;
            TextField tfField = (TextField) formNodes.get(i++);
            activeJob.addMeasure(formFields.get(currentPosition++).getName(), toDouble(tfField.getText()));
        }

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
        imgUser.setFitHeight(225);
        imgUser.setFitWidth(319);
        imgUser.setPreserveRatio(true);
    }

    /**
     * Opens a chooser to select user's style
     */
    @FXML
    private void selectUserStyle() throws FileNotFoundException {
        userStyle = choosePhoto("Dress Style");
        if (userStyle == null) return;
        imgStyle.setImage(new Image(new FileInputStream(userStyle)));
        imgStyle.setFitHeight(225);
        imgStyle.setFitWidth(319);
        imgStyle.setPreserveRatio(true);
    }

    /**
     * Resets the user photo to null
     */
    @FXML
    private void clearUserPhoto() {
        userPhoto = null;
        imgUser.setImage(new Image(getClass().getResource("../views/resources/nobody_m.jpg").toString()));
    }

    /**
     * Resets the user style to null
     */
    @FXML
    private void clearUserStyle() {
        userStyle = null;
        imgStyle.setImage(new Image(getClass().getResource("../views/resources/default-placeholder.png").toString()));
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
        return string(activeJob.getMeasure(field));
    }

    public void setHomeController(HomeController stage) {
        this.homeController = stage;
    }

    private String string(double val) {
        if (val == 0) return null;
        return Double.toString(val);
    }

    private Double toDouble(String s) {
        return Double.valueOf(s);
    }
}
