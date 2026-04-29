import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RadiologyReportsPage {

    private final AppiumDriver driver;
    private final boolean ios;
    private final String pageName = "Radiology Reports";

    public RadiologyReportsPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.ios = ios;
    }

    public void navigateRadiologyReports() {
        System.out.println("[INFO] ===== NAVIGATING RADIOLOGY REPORTS =====");

        try {
            Thread.sleep(1500); // Wait for content to load
            goBackFromRadiologyReports();
            System.out.println("[INFO] ===== RADIOLOGY REPORTS NAVIGATION COMPLETED =====");

        } catch (Exception e) {
            System.out.println("[ERROR] Error navigating Radiology Reports: " + e.getMessage());
            e.printStackTrace();
            try {
                goBackFromRadiologyReports();
            } catch (Exception backEx) {
                System.out.println("[WARNING] Could not go back: " + backEx.getMessage());
            }
        }
    }

    private void goBackFromRadiologyReports() {
        try {
            System.out.println("[DEBUG] Going back from " + pageName);

            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }

            System.out.println("[SUCCESS] Returned from " + pageName);

        } catch (Exception e) {
            System.out.println("[WARNING] Could not go back from " + pageName + ": " + e.getMessage());
        }
    }
}
