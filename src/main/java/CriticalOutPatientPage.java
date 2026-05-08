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

    public CriticalOutPatientPage(AppiumDriver driver,
                                  WebDriverWait wait,
                                  boolean ios) {

        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    public void testCriticalOutpatient() {

        try {

            System.out.println("[INFO] Waiting for critical out-patient list...");
            Thread.sleep(2000);

        By itemsLocator = ios
        ? By.xpath("//XCUIElementTypeCell")
        : By.xpath("//android.view.View[contains(@content-desc,'MRN -')]");

        wait.until(driver -> driver.findElements(itemsLocator).size() > 0);

        List<WebElement> items = driver.findElements(itemsLocator);

        System.out.println("[INFO] Found " + items.size() + " patients");

            if (items.isEmpty()) {
                System.out.println("[INFO] No patients found — returning to Home");
                goBackToHomePage();
                return;
            }

            // Debug and tap first patient
            WebElement targetItem = items.get(0);
            String desc = targetItem.getAttribute("content-desc");
            System.out.println("[DEBUG] First patient desc: " + (desc != null ? desc.replace("\n", "\\n") : "null"));
            System.out.println("[DEBUG] First patient bounds: " + targetItem.getAttribute("bounds"));

            int px = targetItem.getLocation().getX() + targetItem.getSize().getWidth() / 2;
            int py = targetItem.getLocation().getY() + targetItem.getSize().getHeight() / 2;
            System.out.println("[DEBUG] Tapping patient[0] at (" + px + ", " + py + ")");
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", px, "y", py));
            System.out.println("[SUCCESS] Tapped patient[0]");
            Thread.sleep(3000);

            // Verify navigation
            List<WebElement> afterTap = driver.findElements(itemsLocator);
            if (afterTap.isEmpty()) {
                System.out.println("[SUCCESS] Navigated to lab list");
            } else {
                System.out.println("[WARNING] Still on patient list (" + afterTap.size() + " items) — tap did not navigate");
            }

            // ✅ Click History
            clickHistory();
            Thread.sleep(3000);

            // ✅ Click PDF
            clickPdf();

            // ✅ Back flows
            goBackFromPdfViewer();
            goBackFromHistory();
            goBackFromOutPatientDetails();
            goBackToHomePage();

        } catch (Exception e) {

            System.out.println("[ERROR] Critical out-patient test failed");
            e.printStackTrace();
        }
    }

    public void clickHistory() {

        try {

            System.out.println("[INFO] Looking for History...");

            By locator = ios
                    ? AppiumBy.accessibilityId("History")
                    : By.xpath("//*[contains(@content-desc,'History')]");

            List<WebElement> found = driver.findElements(locator);

            if (found.isEmpty()) {

                System.out.println("[INFO] History not visible");

                return;
            }

            WebElement element = found.get(0);
            int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
            int y = element.getLocation().getY() + element.getSize().getHeight() / 2;
            System.out.println("[DEBUG] Tapping History at (" + x + ", " + y + ")");
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] History tapped");

        } catch (Exception e) {

            System.out.println("[ERROR] History click failed");
            e.printStackTrace();
        }
    }

    public void clickPdf() {

        try {

            System.out.println("[INFO] Looking for PDF...");

            By locator = ios
                    ? AppiumBy.accessibilityId("pdf_icon")
                    : By.xpath(
                    "//android.view.View[contains(@content-desc,'Reference Range')]"
                            + "/following-sibling::android.widget.ImageView[2]"
            );

            WebElement element = wait.until(
                    ExpectedConditions.presenceOfElementLocated(locator)
            );

            int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
            int y = element.getLocation().getY() + element.getSize().getHeight() / 2;
            System.out.println("[DEBUG] Tapping PDF at (" + x + ", " + y + ")");
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] PDF tapped");

            Thread.sleep(5000);

        } catch (Exception e) {

            System.out.println("[ERROR] PDF click failed");
            e.printStackTrace();
        }
    }

    private void goBackFromPdfViewer() {

        try {

            System.out.println("[INFO] Going back from PDF viewer");

            if (!ios) {
                ((AndroidDriver) driver)
                        .pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }

            Thread.sleep(1000);

        } catch (Exception e) {

            System.out.println("[WARNING] Could not go back from PDF viewer");
        }
    }

    private void goBackFromHistory() {

        try {

            System.out.println("[INFO] Going back from History");

            if (!ios) {
                ((AndroidDriver) driver)
                        .pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }

            Thread.sleep(1000);

        } catch (Exception e) {

            System.out.println("[WARNING] Could not go back from History");
        }
    }

    public void goBackFromOutPatientDetails() {

        try {

            System.out.println("[INFO] Going back from patient details");

            if (!ios) {
                ((AndroidDriver) driver)
                        .pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }

            Thread.sleep(1000);

        } catch (Exception e) {

            System.out.println("[WARNING] Could not go back from details");
        }
    }

    public void goBackToHomePage() {

        try {

            System.out.println("[INFO] Going back to Home");

            if (!ios) {
                ((AndroidDriver) driver)
                        .pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }

            Thread.sleep(1000);

            System.out.println("[SUCCESS] Returned to Home");

        } catch (Exception e) {

            System.out.println("[WARNING] Home navigation failed");
        }
    }
}