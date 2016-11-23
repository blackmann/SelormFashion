package co.blackground.selormfashion;

public class Utils {

    private static Main mainApp;

    public static Main getMainApp() {
        return mainApp;
    }

    static void setMainApp(Main mainApp) {
        Utils.mainApp = mainApp;
    }
}
