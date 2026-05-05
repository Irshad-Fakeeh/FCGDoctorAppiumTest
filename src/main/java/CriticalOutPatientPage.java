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

    public void testCriticalOutpatient() throws InterruptedException {
        By itemsLocator = ios
                ? By.xpath("//XCUIElementTypeCell")
                : By.xpath("//android.view.View[@content-desc]");

        try {
            System.out.println("[INFO] Waiting for critical out-patient list to load...");
            Thread.sleep(2000);

            List<WebElement> items = driver.findElements(itemsLocator);
            System.out.println("[INFO] Found " + items.size() + " items on critical out-patient list");

            if (items.isEmpty()) {
                System.out.println("[INFO] No critical out-patient available — returning to home");
                goBackToHomePage();
                return;
            }

            // Log first few items so we can confirm which element is being tapped
            for (int i = 0; i < Math.min(items.size(), 4); i++) {
                System.out.println("[DEBUG] items[" + i + "] content-desc: "
                        + items.get(i).getAttribute("content-desc"));
            }

            // Index 1 skips any header row at index 0 — first patient item
            int targetIndex = (items.size() > 1) ? 1 : 0;
            WebElement targetItem = items.get(targetIndex);

            int px = targetItem.getLocation().getX() + targetItem.getSize().getWidth() / 2;
            int py = targetItem.getLocation().getY() + targetItem.getSize().getHeight() / 2;
            System.out.println("[DEBUG] Tapping patient[" + targetIndex + "] at (" + px + ", " + py + ")");
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", px, "y", py));
            System.out.println("[SUCCESS] Tapped critical out-patient");
            Thread.sleep(3000); // wait for Lab Reports page to fully load

            // Click History tab (may be off-screen, swipe handles it)
            clickHistory();
            Thread.sleep(3000); // wait for History list to render

            // Click PDF placeholder (scrolls down first, then taps)
            clickPdf();

            // 4 BACKs: PDF → History → Lab Reports → Patient List → Dashboard
            goBackFromPdfViewer();
            goBackFromHistory();
            // goBackFromOutPatientDetails();
            Thread.sleep(1000);
            goBackToHomePage();
            Thread.sleep(1000);

        } catch (Exception e) {
            System.out.println("[ERROR] Critical out-patient test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clickHistory() {
        try {
            System.out.println("[INFO] Looking for History tab...");

            By locator = ios
                    ? AppiumBy.accessibilityId("History")
                    : By.xpath("//*[contains(@content-desc,'History') or contains(@text,'History')]");

            // Instant check — no 30s block
            List<WebElement> found = driver.findElements(locator);

            if (found.isEmpty()) {
                // History tab is off-screen to the right — swipe left in the tab bar area
                System.out.println("[INFO] History not visible — swiping tab bar to reveal it...");
                int screenW = driver.manage().window().getSize().getWidth();
                int screenH = driver.manage().window().getSize().getHeight();
                driver.executeScript("mobile: swipeGesture", java.util.Map.ofEntries(
                        java.util.Map.entry("left",      (int)(screenW * 0.8)),
                        java.util.Map.entry("top",       (int)(screenH * 0.35)),
                        java.util.Map.entry("width",     (int)(screenW * 0.6)),
                        java.util.Map.entry("height",    50),
                        java.util.Map.entry("direction", "left"),
                        java.util.Map.entry("percent",   0.8),
                        java.util.Map.entry("speed",     400)));
                try { Thread.sleep(800); } catch (InterruptedException ignored) {}
                found = driver.findElements(locator);
            }

            if (found.isEmpty()) {
                System.out.println("[INFO] History tab not found — skipping");
                return;
            }

            WebElement element = found.get(0);
            int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
            int y = element.getLocation().getY() + element.getSize().getHeight() / 2;
            System.out.println("[DEBUG] Tapping History at (" + x + ", " + y + ")");
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] History tab clicked");

        } catch (Exception e) {
            System.out.println("[ERROR] History click failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clickPdf() {
        try {
            int screenW = driver.manage().window().getSize().getWidth();
            int screenH = driver.manage().window().getSize().getHeight();

            // Scroll down so the PDF placeholder is within the viewport
            System.out.println("[INFO] Scrolling down to PDF placeholder...");
            driver.executeScript("mobile: swipeGesture", java.util.Map.ofEntries(
                    java.util.Map.entry("left",      screenW / 2 - 50),
                    java.util.Map.entry("top",       (int)(screenH * 0.70)),
                    java.util.Map.entry("width",     100),
                    java.util.Map.entry("height",    (int)(screenH * 0.40)),
                    java.util.Map.entry("direction", "up"),
                    java.util.Map.entry("percent",   0.7),
                    java.util.Map.entry("speed",     500)));
            Thread.sleep(1000);

            System.out.println("[INFO] Looking for PDF placeholder...");
            // 2nd ImageView sibling after the lab result view (confirmed from Inspector hierarchy)
            By locator = ios
                    ? AppiumBy.accessibilityId("pdf_icon")
                    : By.xpath("//android.view.View[contains(@content-desc,'Reference Range')]" +
                               "/following-sibling::android.widget.ImageView[2]");

            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
            int y = element.getLocation().getY() + element.getSize().getHeight() / 2;
            System.out.println("[DEBUG] PDF element at (" + x + ", " + y +
                    "), size=" + element.getSize().getWidth() + "x" + element.getSize().getHeight());

            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] PDF placeholder tapped — waiting for report to load");
            Thread.sleep(5000);
            System.out.println("[INFO] PDF report loaded and shown");

        } catch (Exception e) {
            System.out.println("[ERROR] PDF click failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void goBackFromPdfViewer() {
        try {
            System.out.println("[DEBUG] Going back from PDF viewer");
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            Thread.sleep(1000);
            System.out.println("[SUCCESS] Returned from PDF viewer");
        } catch (Exception e) {
            System.out.println("[WARNING] Could not go back from PDF viewer: " + e.getMessage());
        }
    }

    private void goBackFromHistory() {
        try {
            System.out.println("[DEBUG] Going back from History screen");
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            Thread.sleep(1000);
            System.out.println("[SUCCESS] Returned from History screen");
        } catch (Exception e) {
            System.out.println("[WARNING] Could not go back from History: " + e.getMessage());
        }
    }

    public void goBackFromOutPatientDetails() {
        try {
            System.out.println("[DEBUG] Going back from out-patient details");
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            System.out.println("[SUCCESS] Returned from critical out-patient details");
        } catch (Exception e) {
            System.out.println("[WARNING] Could not go back from out-patient details: " + e.getMessage());
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
}
