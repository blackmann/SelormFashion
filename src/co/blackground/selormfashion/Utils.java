package co.blackground.selormfashion;

import java.io.File;
import java.io.IOException;

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
}
