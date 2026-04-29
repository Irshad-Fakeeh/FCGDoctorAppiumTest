import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

public class CriticalOutPatientPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public CriticalOutPatientPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    /**
     * Clicks the first or targeted critical out-patient from the list.
     * If the list is empty, navigates back to the home page.
     */
    public void testCriticalOutpatient() throws InterruptedException {
        By itemsLocator = ios
                ? By.xpath("//XCUIElementTypeCell")
                : By.xpath("//android.view.View[@content-desc]");

        try {
            System.out.println("[INFO] Waiting for critical out-patient items to load...");
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(itemsLocator));

            List<WebElement> items = driver.findElements(itemsLocator);
            System.out.println("[INFO] Found " + items.size() + " critical out-patient items");

            if (items.isEmpty()) {
                System.out.println("[INFO] No critical out-patient available.");
                System.out.println("Returning to Home page...");
                goBackToHomePage();
                return;
            }

            // Select 1st item (index 1 to skip header) or fallback
            int targetIndex = (items.size() > 1) ? 1 : 0;
            WebElement targetItem = wait.until(
                    ExpectedConditions.elementToBeClickable(items.get(targetIndex)));

            System.out.println("[INFO] Clicking critical out-patient item at index " + targetIndex + " (1st item)...");
            targetItem.click();
            System.out.println("[SUCCESS] Clicked critical out-patient");

            // Wait for out-patient details to load
            Thread.sleep(2000);

            clickHistory();
            // Click View EMR button
            // clickViewEMRButton();
            Thread.sleep(2000);
            clickPdf();
            Thread.sleep(2000);

            // Navigate EMR sections
            // EMRPage emrPage = new EMRPage(driver, wait, ios);
            // emrPage.navigateEMRSections();
            // Thread.sleep(1000);

            // Go back from out-patient details
            goBackFromOutPatientDetails();
            Thread.sleep(1000);

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to click critical out-patient: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void goBackToHomePage() {
        try {
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            System.out.println("[SUCCESS] Navigated to Home via Back press");
        } catch (Exception e) {
            System.out.println("[WARNING] Home navigation failed: " + e.getMessage());
        }
    }

    public void clickHistory() {
        try {
            System.out.println("[INFO] Clicking full History container...");

            By locator = ios
                    ? AppiumBy.accessibilityId("history")
                    : By.xpath("//android.view.View[@content-desc='History']");

            WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator));

            // 🔥 Flutter-safe tap (recommended)
            int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
            int y = element.getLocation().getY() + element.getSize().getHeight() / 2;

            driver.executeScript("mobile: clickGesture", java.util.Map.of(
                    "x", x,
                    "y", y));

            System.out.println("[SUCCESS] History container clicked");

        } catch (Exception e) {
            System.out.println("[ERROR] History click failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clickPdf() {
        By locator = ios
                ? AppiumBy.accessibilityId("pdf_icon")
                : By.xpath("(//android.widget.ScrollView//android.widget.ImageView)[3]");

        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

        int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
        int y = element.getLocation().getY() + element.getSize().getHeight() / 2;

        driver.executeScript("mobile: clickGesture", java.util.Map.of(
                "x", x,
                "y", y));

        System.out.println("[SUCCESS] PDF icon clicked");
    }

    private String getCurrentActivitySafe() {
        try {
            if (!ios) {
                return ((AndroidDriver) driver).currentActivity();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void clickViewEMRButton() {
        try {
            By locator = ios ? AppiumBy.accessibilityId("View EMR")
                    : By.xpath("//android.widget.ImageView[@content-desc='View EMR']");
            System.out.println("[INFO] Looking for View EMR button...");
            WebElement viewEMRBtn = wait.until(ExpectedConditions.elementToBeClickable(locator));
            viewEMRBtn.click();
            System.out.println("[SUCCESS] Clicked View EMR button");
        } catch (Exception e) {
            System.out.println("[WARNING] Could not click View EMR button: " + e.getMessage());
        }
    }

    public void goBackFromOutPatientDetails() {
        try {
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            System.out.println("[SUCCESS] Returned from critical out-patient details");
        } catch (Exception e) {
            System.out.println("[WARNING] Could not go back from critical out-patient details: " + e.getMessage());
        }
    }
}
