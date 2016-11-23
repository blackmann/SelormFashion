package co.blackground.selormfashion.controllers;

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
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class NewJobController {

    private ArrayList<FormField> formFields;
    private Job activeJob;

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
    private void initialize() {
        formFields = new ArrayList<>();
        activeJob = new Job();
        ToggleGroup tgJobType = new ToggleGroup();

        btnTop.setToggleGroup(tgJobType);
        btnTrouser.setToggleGroup(tgJobType);

        // TOP is the default type
        toggleToTop();

        // configure gpForm
        gpForm.setVgap(4.0);
    }

    /**
     * Sets the type of job to be a #Job.Type.TOPS
     */
    @FXML
    private void toggleToTop() {
        activeJob.setJobType(Job.Type.TOPS);
        setUpForm();
    }

    /**
     * Sets the type of job to be a #Job.Type.TROUSER
     */
    @FXML
    private void toggleToTrouser() {
        activeJob.setJobType(Job.Type.TROUSER);
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
    private void saveJob() {
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

        activeJob.save();
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

    private String string(double val) {
        if (val == 0) return null;
        return Double.toString(val);
    }

    private Double toDouble(String s) {
        return Double.valueOf(s);
    }
}
