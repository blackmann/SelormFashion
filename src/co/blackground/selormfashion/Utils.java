package co.blackground.selormfashion;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.prefs.Preferences;

public class Utils {

    private static final String PASSWORD = "PASSWORD";
    private static Main mainApp;
    private static boolean loggedOn;
    private static Preferences preference;

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

    /**
     * Makes @param date readable to humans
     *
     * @param date the date to be formatted
     * @return a formatted date
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        return date.toString();
    }

    /**
     * Beautifies text to be informative, null or empty string become
     * "Not Availabe", but anything else is return as is
     *
     * @param str the text to be beautified
     * @return a beautified string
     */
    public static String friendlyText(String str) {
        return str == null || str.isEmpty() ? "Not Availabe" : str;
    }

    /**
     * Verifies if the supplied password is correct
     * with the one the preferences
     *
     * @param text the text/password to be verified as correct
     * @return Returns true if the password is correct else false
     */
    public static boolean verifyPassword(String text) {
        initPrefs();

        String password = preference.get(PASSWORD, "1234");
        if (text == null || text.isEmpty()) return false;
        loggedOn = password.equals(text);
        return loggedOn;
    }

    /**
     * @return Returns true if the user logged in successfully
     */
    static boolean isLogged() {
        return loggedOn;
    }

    /**
     * Sets a new password
     *
     * @param password the new password
     */
    public static void setNewPassword(String password) {
        initPrefs();
        preference.put(PASSWORD, password);
    }

    private static void initPrefs() {
        if (preference == null) {
            preference = Preferences.userNodeForPackage(Utils.class);
        }
    }

    public static void setIcon(Stage stage, Main main) {
        stage.getIcons().add(new Image(main.getClass().getResource("views/resources/icon.png").toString()));
    }
}
