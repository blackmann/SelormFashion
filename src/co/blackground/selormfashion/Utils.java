package co.blackground.selormfashion;

import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Utils {

    private static Main mainApp;

    /**
     * @return Returns the #mainApp instance
     */
    public static Main getMainApp() {
        return mainApp;
    }

    /**
     * Sets the instance of the #Main instance
     *
     * @param mainApp the #Main instance
     */
    static void setMainApp(Main mainApp) {
        Utils.mainApp = mainApp;
    }

    /**
     * Capitalizes a field and replaces underscores with spaces,
     * thus turning @param field into a human readable title
     *
     * @param field the field to be turned into tile
     * @return the processed field as a title
     */
    public static String toTitle(String field) {
        String result = field.replace("_", " ");
        //// TODO: 11/24/2016 use correct capitalization
        return result.toUpperCase();
    }

    /**
     * Creates files and folders required on first run
     */
    static void setUpEnvironment() {
        File jobsFile = new File("jobs_file.xml");
        if (!jobsFile.exists()) {
            try {
                jobsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File photosDir = new File(Constants.PHOTOS_DIR);
        if (!photosDir.exists()) {
            photosDir.mkdir();
        }

        File stylesDir = new File(Constants.STYLES_DIR);
        if (!stylesDir.exists()) {
            stylesDir.mkdir();
        }
    }

    /**
     * Makes a TextField accept only numbers
     * @param tf the text field to apply this property
     */
    public static void acceptNumberOnly(TextField tf) {
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            if (!newValue.matches("\\.?\\d*")) {
                tf.setText(newValue.replaceAll("[^\\d\\.]", ""));
            }
            // ensures there's only a single point
            if (!newValue.matches("\\d*\\.?\\d*")) {
                tf.setText(oldValue);
            }
        });
    }

    public static String formatDate(Date date) {
        if (date == null) return "";
        return date.toString();
    }

    public static String friendlyText(String mobile) {
        return mobile == null || mobile.isEmpty() ? "Not Availabe" : mobile;
    }
}
